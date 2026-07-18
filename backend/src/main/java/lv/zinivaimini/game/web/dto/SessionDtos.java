package lv.zinivaimini.game.web.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lv.zinivaimini.game.domain.QuestionType;
import lv.zinivaimini.game.domain.SessionStatus;

public final class SessionDtos {
    private SessionDtos() {
    }

    public record TeamInput(
            @NotBlank @Size(max = 80) String name,
            @Pattern(regexp = "^#[0-9A-Fa-f]{6}$") String color) {
    }

    public record CreateSessionInput(
            @NotNull UUID gameId,
            @Size(min = 2, max = 12) List<@Valid TeamInput> teams) {
    }

    public record VersionInput(@PositiveOrZero long version) {
    }

    public record RevealInput(UUID optionId, @PositiveOrZero long version) {
    }

    public record SelectQuestionInput(@NotNull UUID questionId, @PositiveOrZero long version) {
    }

    public record ScoreInput(boolean correct, @PositiveOrZero long version) {
    }

    public record SessionSummary(
            UUID id,
            UUID gameId,
            String title,
            SessionStatus status,
            int teamCount,
            int usedCount,
            int totalQuestions,
            Instant updatedAt) {
    }

    public record TeamView(UUID id, String name, String color, int position, int score, int rank,
            int correctAnswers, int wrongAnswers) {
    }

    public record OptionView(UUID id, String text, boolean correct) {
    }

    public record BoardQuestionView(UUID id, int points, boolean used, Boolean correct, String teamColor) {
    }

    public record BoardCategoryView(String name, String color, int position, List<BoardQuestionView> questions) {
    }

    public record SelectedQuestionView(
            UUID id,
            String categoryName,
            String categoryColor,
            int points,
            QuestionType type,
            String prompt,
            String answer,
            String explanation,
            String mediaUrl,
            boolean hasHint,
            boolean hintUsed,
            List<OptionView> options) {
    }

    public record SessionView(
            UUID id,
            UUID gameId,
            String publicToken,
            String title,
            String locale,
            SessionStatus status,
            long version,
            int activeTeamIndex,
            UUID activeTeamId,
            UUID selectedOptionId,
            int usedCount,
            int totalQuestions,
            boolean answerRevealed,
            boolean canUndo,
            Instant createdAt,
            Instant updatedAt,
            List<TeamView> teams,
            List<BoardCategoryView> categories,
            SelectedQuestionView selectedQuestion) {
    }
}
