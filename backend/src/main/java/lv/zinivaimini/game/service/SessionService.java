package lv.zinivaimini.game.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lv.zinivaimini.game.domain.Game;
import lv.zinivaimini.game.domain.GameSession;
import lv.zinivaimini.game.domain.ScoreEvent;
import lv.zinivaimini.game.domain.SessionQuestion;
import lv.zinivaimini.game.domain.SessionStatus;
import lv.zinivaimini.game.domain.SessionTeam;
import lv.zinivaimini.game.domain.QuestionType;
import lv.zinivaimini.game.repository.GameRepository;
import lv.zinivaimini.game.repository.GameSessionRepository;
import lv.zinivaimini.game.repository.ScoreEventRepository;
import lv.zinivaimini.game.repository.SessionQuestionRepository;
import lv.zinivaimini.game.repository.SessionTeamRepository;
import lv.zinivaimini.game.web.dto.SessionDtos.BoardCategoryView;
import lv.zinivaimini.game.web.dto.SessionDtos.BoardQuestionView;
import lv.zinivaimini.game.web.dto.SessionDtos.CreateSessionInput;
import lv.zinivaimini.game.web.dto.SessionDtos.OptionView;
import lv.zinivaimini.game.web.dto.SessionDtos.SelectedQuestionView;
import lv.zinivaimini.game.web.dto.SessionDtos.SessionSummary;
import lv.zinivaimini.game.web.dto.SessionDtos.SessionView;
import lv.zinivaimini.game.web.dto.SessionDtos.TeamView;

@Service
public class SessionService {
    private final GameRepository games;
    private final GameSessionRepository sessions;
    private final SessionTeamRepository teams;
    private final SessionQuestionRepository questions;
    private final ScoreEventRepository events;
    private final GameDefinitionValidator validator;
    private final SessionEventHub eventHub;

    public SessionService(GameRepository games, GameSessionRepository sessions, SessionTeamRepository teams,
            SessionQuestionRepository questions, ScoreEventRepository events, GameDefinitionValidator validator,
            SessionEventHub eventHub) {
        this.games = games;
        this.sessions = sessions;
        this.teams = teams;
        this.questions = questions;
        this.events = events;
        this.validator = validator;
        this.eventHub = eventHub;
    }

    @Transactional(readOnly = true)
    public List<SessionSummary> list() {
        return sessions.findAllByOrderByUpdatedAtDesc().stream().map(session -> {
            List<SessionTeam> sessionTeams = teams.findBySessionIdOrderByPositionAsc(session.getId());
            List<SessionQuestion> sessionQuestions = questions.findBySessionIdOrderByCategoryPositionAscPointsAsc(session.getId());
            return new SessionSummary(session.getId(), session.getGameId(), session.getTitle(), session.getStatus(),
                    sessionTeams.size(), usedCount(sessionQuestions), sessionQuestions.size(), session.getUpdatedAt());
        }).toList();
    }

    @Transactional
    public SessionView create(CreateSessionInput input) {
        if (input.gameId() == null) throw new InvalidGameException("Izvēlies spēli.");
        Game game = games.findById(input.gameId()).orElseThrow(() -> new ResourceNotFoundException("Spēle nav atrasta."));
        if (!validator.isPlayable(game)) throw new InvalidGameException("Pirms sākšanas aizpildi visus spēles jautājumus un atbildes.");
        if (input.teams() == null || input.teams().size() < 2 || input.teams().size() > 12) {
            throw new InvalidGameException("Spēlei vajag 2–12 komandas.");
        }

        GameSession session = sessions.save(new GameSession(game, UUID.randomUUID().toString().replace("-", "")));
        for (int index = 0; index < input.teams().size(); index++) {
            var team = input.teams().get(index);
            teams.save(new SessionTeam(session, team.name().trim(), team.color(), index));
        }
        game.getCategories().forEach(category -> category.getQuestions().forEach(question ->
                questions.save(new SessionQuestion(session, category, question))));
        return toView(session, false);
    }

    @Transactional(readOnly = true)
    public SessionView get(UUID id) {
        return toView(requireSession(id), false);
    }

    @Transactional
    public void delete(UUID id) {
        sessions.delete(requireSession(id));
    }

    @Transactional(readOnly = true)
    public SessionView getPublic(String token) {
        return toView(sessions.findByPublicToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Spēles pārraide nav atrasta.")), true);
    }

    @Transactional
    public SessionView select(UUID id, UUID questionId, long version) {
        GameSession session = requireSession(id);
        session.requireVersion(version);
        SessionQuestion question = requireQuestion(session, questionId);
        session.selectQuestion(question);
        return saveAndPublish(session);
    }

    @Transactional
    public SessionView reveal(UUID id, UUID optionId, long version) {
        GameSession session = requireSession(id);
        session.requireVersion(version);
        SessionQuestion question = requireQuestion(session, session.getSelectedQuestionId());
        if (optionId != null && question.getOptions().stream().noneMatch(option -> option.getId().equals(optionId))) {
            throw new InvalidGameException("Izvēlētā atbilde nepieder šim jautājumam.");
        }
        session.revealAnswer(optionId);
        return saveAndPublish(session);
    }

    @Transactional
    public SessionView stepBack(UUID id, long version) {
        GameSession session = requireSession(id);
        session.requireVersion(version);
        if (session.isAnswerRevealed()) {
            session.hideAnswer();
        } else if (session.getSelectedQuestionId() != null) {
            session.clearSelectedQuestion();
        } else {
            ScoreEvent event = events.findFirstBySessionAndUndoneFalseOrderByCreatedAtDesc(session)
                    .orElseThrow(() -> new InvalidGameException("Nav darbības, ko atcelt."));
            SessionTeam team = teams.findById(event.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Komanda nav atrasta."));
            SessionQuestion question = requireQuestion(session, event.getQuestionId());
            session.undo(event, team, question);
        }
        return saveAndPublish(session);
    }

    @Transactional
    public SessionView useHint(UUID id, long version) {
        GameSession session = requireSession(id);
        session.requireVersion(version);
        SessionQuestion question = requireQuestion(session, session.getSelectedQuestionId());
        if (question.getExplanation() == null || question.getExplanation().isBlank()) {
            throw new InvalidGameException("Šim jautājumam nav pavediena.");
        }
        session.useHint(question);
        return saveAndPublish(session);
    }

    @Transactional
    public SessionView score(UUID id, boolean correct, long version) {
        GameSession session = requireSession(id);
        session.requireVersion(version);
        List<SessionTeam> sessionTeams = teams.findBySessionIdOrderByPositionAsc(id);
        if (sessionTeams.isEmpty()) throw new InvalidGameException("Sesijai nav komandu.");
        SessionQuestion question = requireQuestion(session, session.getSelectedQuestionId());
        SessionTeam team = sessionTeams.get(session.getActiveTeamIndex());
        boolean awardedCorrect = correct;
        if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            UUID selectedOptionId = session.getSelectedOptionId();
            awardedCorrect = question.getOptions().stream()
                    .filter(option -> option.getId().equals(selectedOptionId))
                    .findFirst()
                    .orElseThrow(() -> new InvalidGameException("Vispirms izvēlies atbildes variantu."))
                    .isCorrect();
        }
        ScoreEvent event = session.score(team, question, awardedCorrect, sessionTeams.size());
        events.save(event);
        List<SessionQuestion> sessionQuestions = questions.findBySessionIdOrderByCategoryPositionAscPointsAsc(id);
        if (sessionQuestions.stream().allMatch(SessionQuestion::isUsed)) session.finish();
        return saveAndPublish(session);
    }

    @Transactional
    public SessionView finish(UUID id, long version) {
        GameSession session = requireSession(id);
        session.requireVersion(version);
        session.finish();
        return saveAndPublish(session);
    }

    private GameSession requireSession(UUID id) {
        return sessions.findById(id).orElseThrow(() -> new ResourceNotFoundException("Spēles sesija nav atrasta."));
    }

    private SessionView saveAndPublish(GameSession session) {
        sessions.saveAndFlush(session);
        SessionView hostView = toView(session, false);
        eventHub.publish(session.getPublicToken(), toView(session, true));
        return hostView;
    }

    private SessionQuestion requireQuestion(GameSession session, UUID questionId) {
        if (questionId == null) throw new InvalidGameException("Vispirms izvēlies jautājumu.");
        return questions.findByIdAndSessionId(questionId, session.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Sesijas jautājums nav atrasts."));
    }

    private SessionView toView(GameSession session, boolean publicView) {
        List<SessionTeam> sessionTeams = teams.findBySessionIdOrderByPositionAsc(session.getId());
        List<SessionQuestion> sessionQuestions = questions.findBySessionIdOrderByCategoryPositionAscPointsAsc(session.getId());
        List<ScoreEvent> scoreEvents = events.findBySessionAndUndoneFalse(session);
        List<TeamView> teamViews = rankedTeams(sessionTeams, scoreEvents);
        UUID activeTeamId = sessionTeams.isEmpty() ? null : sessionTeams.get(session.getActiveTeamIndex()).getId();
        SelectedQuestionView selected = session.getSelectedQuestionId() == null ? null
                : selectedView(requireQuestion(session, session.getSelectedQuestionId()), publicView && !session.isAnswerRevealed());
        return new SessionView(session.getId(), session.getGameId(), session.getPublicToken(), session.getTitle(),
                session.getLocale(), session.getStatus(), session.getVersion(), session.getActiveTeamIndex(), activeTeamId,
                session.getSelectedOptionId(),
                usedCount(sessionQuestions), sessionQuestions.size(), session.isAnswerRevealed(),
                events.existsBySessionAndUndoneFalse(session), session.getCreatedAt(), session.getUpdatedAt(), teamViews,
                categoryViews(sessionQuestions, sessionTeams, scoreEvents), selected);
    }

    private List<TeamView> rankedTeams(List<SessionTeam> sessionTeams, List<ScoreEvent> scoreEvents) {
        List<SessionTeam> ranked = sessionTeams.stream()
                .sorted(Comparator.comparingInt(SessionTeam::getScore).reversed().thenComparingInt(SessionTeam::getPosition))
                .toList();
        Map<UUID, Integer> ranks = new LinkedHashMap<>();
        int previousScore = Integer.MIN_VALUE;
        int rank = 0;
        for (int index = 0; index < ranked.size(); index++) {
            if (ranked.get(index).getScore() != previousScore) rank = index + 1;
            ranks.put(ranked.get(index).getId(), rank);
            previousScore = ranked.get(index).getScore();
        }
        Map<UUID, int[]> answerCounts = new LinkedHashMap<>();
        scoreEvents.forEach(event -> {
            int[] counts = answerCounts.computeIfAbsent(event.getTeamId(), ignored -> new int[2]);
            counts[event.isCorrect() ? 0 : 1]++;
        });
        return sessionTeams.stream().map(team -> {
            int[] counts = answerCounts.getOrDefault(team.getId(), new int[2]);
            return new TeamView(team.getId(), team.getName(), team.getColor(), team.getPosition(), team.getScore(),
                    ranks.get(team.getId()), counts[0], counts[1]);
        }).toList();
    }

    private List<BoardCategoryView> categoryViews(List<SessionQuestion> sessionQuestions, List<SessionTeam> sessionTeams,
            List<ScoreEvent> scoreEvents) {
        Map<UUID, ScoreEvent> eventsByQuestion = new LinkedHashMap<>();
        scoreEvents.forEach(event -> eventsByQuestion.put(event.getQuestionId(), event));
        Map<UUID, String> teamColors = new LinkedHashMap<>();
        sessionTeams.forEach(team -> teamColors.put(team.getId(), team.getColor()));
        Map<Integer, List<SessionQuestion>> grouped = new LinkedHashMap<>();
        sessionQuestions.forEach(question -> grouped.computeIfAbsent(question.getCategoryPosition(), ignored -> new ArrayList<>()).add(question));
        return grouped.values().stream().map(group -> new BoardCategoryView(group.getFirst().getCategoryName(),
                group.getFirst().getCategoryColor(), group.getFirst().getCategoryPosition(), group.stream()
                        .map(question -> {
                            ScoreEvent event = eventsByQuestion.get(question.getId());
                            return new BoardQuestionView(question.getId(), question.getPoints(), question.isUsed(),
                                    event == null ? null : event.isCorrect(),
                                    event == null ? null : teamColors.get(event.getTeamId()));
                        }).toList()))
                .toList();
    }

    private SelectedQuestionView selectedView(SessionQuestion question, boolean hideAnswer) {
        boolean hideHint = hideAnswer && !question.isHintUsed();
        return new SelectedQuestionView(question.getId(), question.getCategoryName(), question.getCategoryColor(),
                question.getPoints(), question.getQuestionType(), question.getPrompt(), hideAnswer ? null : question.getAnswer(),
                hideHint ? null : question.getExplanation(),
                question.getMediaAsset() == null ? null : "/api/public/media/" + question.getMediaAsset().getId(),
                question.isHintUsed(),
                question.getOptions().stream().map(option -> new OptionView(option.getId(), option.getText(),
                        !hideAnswer && option.isCorrect())).toList());
    }

    private int usedCount(List<SessionQuestion> sessionQuestions) {
        return (int) sessionQuestions.stream().filter(SessionQuestion::isUsed).count();
    }
}
