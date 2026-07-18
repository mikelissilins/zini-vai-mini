package lv.zinivaimini.game.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

import lv.zinivaimini.game.domain.QuestionType;
import lv.zinivaimini.game.web.dto.GameDtos.CategoryInput;
import lv.zinivaimini.game.web.dto.GameDtos.GameInput;
import lv.zinivaimini.game.web.dto.GameDtos.OptionInput;
import lv.zinivaimini.game.web.dto.GameDtos.QuestionInput;

class GameDefinitionValidatorTests {
    private final GameDefinitionValidator validator = new GameDefinitionValidator();

    @Test
    void completeSevenQuestionCategoryIsPlayable() {
        GameInput game = gameWith(List.of(10, 20, 30, 40, 50, 60, 70).stream()
                .map(points -> question(points, "Jautājums", "Atbilde"))
                .toList());

        assertThat(validator.isPlayable(game)).isTrue();
    }

    @Test
    void completeFiveQuestionCategoryIsPlayable() {
        GameInput game = gameWith(List.of(10, 20, 30, 40, 50).stream()
                .map(points -> question(points, "Jautājums", "Atbilde"))
                .toList());

        assertThat(validator.isPlayable(game)).isTrue();
    }

    @Test
    void missingAnswerKeepsDraftUnplayable() {
        GameInput game = gameWith(List.of(
                question(10, "Jautājums", ""), question(20, "J", "A"), question(30, "J", "A"),
                question(40, "J", "A"), question(50, "J", "A"), question(60, "J", "A"), question(70, "J", "A")));

        assertThat(validator.isPlayable(game)).isFalse();
    }

    @Test
    void duplicatePointsAreRejected() {
        GameInput game = gameWith(List.of(question(10, "J", "A"), question(10, "J2", "A2")));

        assertThatThrownBy(() -> validator.validateDraft(game)).isInstanceOf(InvalidGameException.class);
    }

    @Test
    void multipleChoiceRequiresExactlyOneCorrectOption() {
        QuestionInput choice = new QuestionInput(10, QuestionType.MULTIPLE_CHOICE, "J?", "A", null, null,
                List.of(new OptionInput("A", false), new OptionInput("B", false)));

        assertThatThrownBy(() -> validator.validateDraft(gameWith(List.of(choice))))
                .isInstanceOf(InvalidGameException.class);
    }

    private GameInput gameWith(List<QuestionInput> questions) {
        return new GameInput("Spēle", null, "lv", 0L,
                List.of(new CategoryInput("Bībele", "#0E758C", questions)));
    }

    private QuestionInput question(int points, String prompt, String answer) {
        return new QuestionInput(points, QuestionType.FREE_TEXT, prompt, answer, null, null, List.of());
    }
}
