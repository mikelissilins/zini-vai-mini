package lv.zinivaimini.game.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lv.zinivaimini.game.domain.Game;
import lv.zinivaimini.game.domain.Question;
import lv.zinivaimini.game.domain.QuestionType;
import lv.zinivaimini.game.web.dto.GameDtos.CategoryInput;
import lv.zinivaimini.game.web.dto.GameDtos.GameInput;
import lv.zinivaimini.game.web.dto.GameDtos.QuestionInput;

@Component
public class GameDefinitionValidator {
    private static final Set<Integer> REQUIRED_POINTS = Set.of(10, 20, 30, 40, 50);

    public void validateDraft(GameInput input) {
        if (input.categories() == null) return;
        for (CategoryInput category : input.categories()) {
            List<QuestionInput> questions = category.questions() == null ? List.of() : category.questions();
            Set<Integer> points = questions.stream().map(QuestionInput::points).collect(Collectors.toSet());
            if (points.size() != questions.size() || !REQUIRED_POINTS.containsAll(points)) {
                throw new InvalidGameException("Katrā sadaļā drīkst būt tikai viens jautājums katram punktu līmenim 10–50.");
            }
            questions.forEach(this::validateQuestionShape);
        }
    }

    public boolean isPlayable(GameInput input) {
        if (input.categories() == null || input.categories().isEmpty()) return false;
        return input.categories().stream().allMatch(category -> {
            List<QuestionInput> questions = category.questions() == null ? List.of() : category.questions();
            if (!questions.stream().map(QuestionInput::points).collect(Collectors.toSet()).equals(REQUIRED_POINTS)) return false;
            return questions.stream().allMatch(this::isComplete);
        });
    }

    public boolean isPlayable(Game game) {
        if (game.getCategories().isEmpty()) return false;
        return game.getCategories().stream().allMatch(category -> {
            if (!category.getQuestions().stream().map(Question::getPoints).collect(Collectors.toSet()).equals(REQUIRED_POINTS)) {
                return false;
            }
            return category.getQuestions().stream().allMatch(question -> {
                if (question.getPrompt().isBlank() || question.getAnswer().isBlank()) return false;
                if (question.getQuestionType() != QuestionType.MULTIPLE_CHOICE) return true;
                int size = question.getOptions().size();
                return size >= 2 && size <= 4 && question.getOptions().stream().filter(option -> option.isCorrect()).count() == 1;
            });
        });
    }

    private void validateQuestionShape(QuestionInput question) {
        int optionCount = question.options() == null ? 0 : question.options().size();
        if (question.type() != null && question.type().name().equals("MULTIPLE_CHOICE") && optionCount > 0) {
            if (optionCount < 2 || optionCount > 4) {
                throw new InvalidGameException("Izvēles jautājumam vajag 2–4 atbilžu variantus.");
            }
            long correctCount = question.options().stream().filter(option -> option.correct()).count();
            if (correctCount != 1) {
                throw new InvalidGameException("Izvēles jautājumam jābūt tieši vienai pareizai atbildei.");
            }
        }
    }

    private boolean isComplete(QuestionInput question) {
        if (question.prompt() == null || question.prompt().isBlank() || question.answer() == null || question.answer().isBlank()) {
            return false;
        }
        if (question.type() != null && question.type().name().equals("MULTIPLE_CHOICE")) {
            try {
                validateQuestionShape(question);
            } catch (InvalidGameException exception) {
                return false;
            }
        }
        return true;
    }
}
