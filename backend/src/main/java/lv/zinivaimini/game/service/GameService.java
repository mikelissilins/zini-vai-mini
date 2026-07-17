package lv.zinivaimini.game.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lv.zinivaimini.game.domain.Category;
import lv.zinivaimini.game.domain.Game;
import lv.zinivaimini.game.domain.MediaAsset;
import lv.zinivaimini.game.domain.Question;
import lv.zinivaimini.game.domain.QuestionOption;
import lv.zinivaimini.game.domain.QuestionType;
import lv.zinivaimini.game.repository.GameRepository;
import lv.zinivaimini.game.repository.MediaAssetRepository;
import lv.zinivaimini.game.web.dto.GameDtos.CategoryInput;
import lv.zinivaimini.game.web.dto.GameDtos.CategoryView;
import lv.zinivaimini.game.web.dto.GameDtos.CreateFromTemplateInput;
import lv.zinivaimini.game.web.dto.GameDtos.GameInput;
import lv.zinivaimini.game.web.dto.GameDtos.GameSummary;
import lv.zinivaimini.game.web.dto.GameDtos.GameView;
import lv.zinivaimini.game.web.dto.GameDtos.OptionView;
import lv.zinivaimini.game.web.dto.GameDtos.QuestionInput;
import lv.zinivaimini.game.web.dto.GameDtos.QuestionView;

@Service
public class GameService {
    private final GameRepository games;
    private final MediaAssetRepository mediaAssets;
    private final GameDefinitionValidator validator;

    public GameService(GameRepository games, MediaAssetRepository mediaAssets, GameDefinitionValidator validator) {
        this.games = games;
        this.mediaAssets = mediaAssets;
        this.validator = validator;
    }

    @Transactional(readOnly = true)
    public List<GameSummary> listTemplates() {
        return games.findByTemplateOrderByCreatedAtAsc(true).stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<GameSummary> listGames() {
        return games.findByTemplateOrderByCreatedAtAsc(false).stream()
                .sorted(Comparator.comparing(Game::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public GameView get(UUID id) {
        return toView(requireGame(id));
    }

    @Transactional
    public GameView createFromTemplate(UUID templateId, CreateFromTemplateInput input) {
        Game template = requireGame(templateId);
        if (!template.isTemplate()) throw new InvalidGameException("Izvēlētais ieraksts nav templates.");
        Game copy = new Game(input.title(), template.getDescription(), input.locale());
        template.getCategories().forEach(sourceCategory -> copy.addCategory(cloneCategory(sourceCategory)));
        return toView(games.save(copy));
    }

    @Transactional
    public GameView create(GameInput input) {
        validator.validateDraft(input);
        Game game = new Game(input.title(), input.description(), input.locale());
        game.replaceCategories(toCategories(input.categories()));
        return toView(games.save(game));
    }

    @Transactional
    public GameView update(UUID id, GameInput input) {
        validator.validateDraft(input);
        Game game = requireGame(id);
        if (game.isTemplate()) throw new InvalidGameException("Iebūvētu template nevar mainīt.");
        if (input.version() == null || input.version() != game.getVersion()) {
            throw new OptimisticLockingFailureException("Spēli jau ir izmainījusi cita pārlūka sesija.");
        }
        game.update(input.title(), input.description(), input.locale());
        game.clearCategories();
        games.flush();
        game.replaceCategories(toCategories(input.categories()));
        return toView(games.saveAndFlush(game));
    }

    @Transactional
    public void delete(UUID id) {
        Game game = requireGame(id);
        if (game.isTemplate()) throw new InvalidGameException("Iebūvētu template nevar dzēst.");
        games.delete(game);
    }

    private Game requireGame(UUID id) {
        return games.findById(id).orElseThrow(() -> new ResourceNotFoundException("Spēle nav atrasta."));
    }

    private List<Category> toCategories(List<CategoryInput> inputs) {
        if (inputs == null) return List.of();
        List<Category> result = new ArrayList<>();
        for (int index = 0; index < inputs.size(); index++) {
            CategoryInput input = inputs.get(index);
            Category category = new Category(input.name(), input.color(), index);
            if (input.questions() != null) input.questions().forEach(question -> category.addQuestion(toQuestion(question)));
            result.add(category);
        }
        return result;
    }

    private Question toQuestion(QuestionInput input) {
        MediaAsset media = input.mediaAssetId() == null ? null : mediaAssets.findById(input.mediaAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Jautājuma attēls nav atrasts."));
        Question question = new Question(
                input.points(),
                input.type() == null ? QuestionType.FREE_TEXT : input.type(),
                input.prompt() == null ? "" : input.prompt(),
                input.answer() == null ? "" : input.answer(),
                input.explanation(),
                media);
        if (input.options() != null) {
            for (int index = 0; index < input.options().size(); index++) {
                var option = input.options().get(index);
                question.addOption(new QuestionOption(option.text(), index, option.correct()));
            }
        }
        return question;
    }

    private Category cloneCategory(Category source) {
        Category copy = new Category(source.getName(), source.getColor(), source.getPosition());
        source.getQuestions().forEach(question -> {
            Question questionCopy = new Question(
                    question.getPoints(), question.getQuestionType(), question.getPrompt(), question.getAnswer(),
                    question.getExplanation(), question.getMediaAsset());
            question.getOptions().forEach(option -> questionCopy.addOption(
                    new QuestionOption(option.getText(), option.getPosition(), option.isCorrect())));
            copy.addQuestion(questionCopy);
        });
        return copy;
    }

    private boolean isPlayable(Game game) {
        return validator.isPlayable(game);
    }

    private GameSummary toSummary(Game game) {
        return new GameSummary(game.getId(), game.getTitle(), game.getDescription(), game.getLocale(), game.isTemplate(),
                game.getTemplateKey(), game.getVersion(), isPlayable(game), game.getCategories().size(), game.getUpdatedAt());
    }

    private GameView toView(Game game) {
        return new GameView(game.getId(), game.getTitle(), game.getDescription(), game.getLocale(), game.isTemplate(),
                game.getTemplateKey(), game.getVersion(), isPlayable(game), game.getCreatedAt(), game.getUpdatedAt(),
                game.getCategories().stream().map(category -> new CategoryView(
                        category.getId(), category.getName(), category.getColor(), category.getPosition(),
                        category.getQuestions().stream().map(question -> new QuestionView(
                                question.getId(), question.getPoints(), question.getQuestionType(), question.getPrompt(),
                                question.getAnswer(), question.getExplanation(),
                                question.getMediaAsset() == null ? null : question.getMediaAsset().getId(),
                                question.getMediaAsset() == null ? null : "/api/public/media/" + question.getMediaAsset().getId(),
                                question.getOptions().stream().map(option -> new OptionView(
                                        option.getId(), option.getText(), option.isCorrect())).toList())).toList())).toList());
    }
}
