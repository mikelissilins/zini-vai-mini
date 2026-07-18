package lv.zinivaimini.game.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import lv.zinivaimini.game.domain.Category;
import lv.zinivaimini.game.domain.Game;
import lv.zinivaimini.game.domain.GameSession;
import lv.zinivaimini.game.domain.Question;
import lv.zinivaimini.game.domain.QuestionType;
import lv.zinivaimini.game.domain.SessionQuestion;
import lv.zinivaimini.game.domain.SessionTeam;
import lv.zinivaimini.game.repository.GameRepository;
import lv.zinivaimini.game.repository.GameSessionRepository;
import lv.zinivaimini.game.repository.ScoreEventRepository;
import lv.zinivaimini.game.repository.SessionQuestionRepository;
import lv.zinivaimini.game.repository.SessionTeamRepository;

class SessionServiceTests {

    @Test
    void restoredSnapshotKeepsProgressAndCompetitionRanks() {
        Game game = new Game("Nometne", null, "lv");
        Category category = new Category("Jūra", "#0E758C", 0);
        Question source = new Question(20, QuestionType.FREE_TEXT, "Kas?", "Atbilde", null, null);
        category.addQuestion(source);
        game.addCategory(category);
        GameSession session = new GameSession(game, "token");
        SessionQuestion question = new SessionQuestion(session, category, source);
        question.markUsed();
        SessionTeam first = new SessionTeam(session, "Viļņi", "#0E758C", 0);
        SessionTeam second = new SessionTeam(session, "Bākas", "#F77F5B", 1);
        SessionTeam third = new SessionTeam(session, "Laivas", "#55B8CC", 2);
        first.addScore(50);
        second.addScore(50);
        third.addScore(20);

        GameSessionRepository sessions = mock(GameSessionRepository.class);
        SessionTeamRepository teams = mock(SessionTeamRepository.class);
        SessionQuestionRepository questions = mock(SessionQuestionRepository.class);
        ScoreEventRepository events = mock(ScoreEventRepository.class);
        when(sessions.findById(session.getId())).thenReturn(Optional.of(session));
        when(teams.findBySessionIdOrderByPositionAsc(session.getId())).thenReturn(List.of(first, second, third));
        when(questions.findBySessionIdOrderByCategoryPositionAscPointsAsc(session.getId())).thenReturn(List.of(question));
        when(events.findBySessionAndUndoneFalse(session)).thenReturn(List.of());
        when(events.existsBySessionAndUndoneFalse(session)).thenReturn(true);

        SessionService service = new SessionService(mock(GameRepository.class), sessions, teams, questions, events,
                mock(GameDefinitionValidator.class), mock(SessionEventHub.class));
        var restored = service.get(session.getId());

        assertThat(restored.usedCount()).isEqualTo(1);
        assertThat(restored.canUndo()).isTrue();
        assertThat(restored.teams()).extracting(team -> team.rank()).containsExactly(1, 1, 3);
        assertThat(restored.teams()).allSatisfy(team -> {
            assertThat(team.correctAnswers()).isZero();
            assertThat(team.wrongAnswers()).isZero();
        });
    }
}
