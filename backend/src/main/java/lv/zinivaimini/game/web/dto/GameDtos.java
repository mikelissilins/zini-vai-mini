package lv.zinivaimini.game.web.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lv.zinivaimini.game.domain.QuestionType;

public final class GameDtos {
    private GameDtos() {
    }

    public record OptionInput(
            @NotBlank @Size(max = 600) String text,
            boolean correct) {
    }

    public record QuestionInput(
            int points,
            QuestionType type,
            @Size(max = 1200) String prompt,
            @Size(max = 1200) String answer,
            @Size(max = 1600) String explanation,
            UUID mediaAssetId,
            @Min(5) @Max(300) Integer timeLimitSeconds,
            List<@Valid OptionInput> options) {
    }

    public record CategoryInput(
            @NotBlank @Size(max = 80) String name,
            @Pattern(regexp = "^#[0-9A-Fa-f]{6}$") String color,
            List<@Valid QuestionInput> questions) {
    }

    public record GameInput(
            @NotBlank @Size(max = 160) String title,
            @Size(max = 600) String description,
            @Pattern(regexp = "^(lv|en)$") String locale,
            Long version,
            List<@Valid CategoryInput> categories) {
    }

    public record CreateFromTemplateInput(
            @NotBlank @Size(max = 160) String title,
            @Pattern(regexp = "^(lv|en)$") String locale) {
    }

    public record OptionView(UUID id, String text, boolean correct) {
    }

    public record QuestionView(
            UUID id,
            int points,
            QuestionType type,
            String prompt,
            String answer,
            String explanation,
            UUID mediaAssetId,
            String mediaUrl,
            int timeLimitSeconds,
            List<OptionView> options) {
    }

    public record CategoryView(UUID id, String name, String color, int position, List<QuestionView> questions) {
    }

    public record GameView(
            UUID id,
            String title,
            String description,
            String locale,
            boolean template,
            String templateKey,
            long version,
            boolean playable,
            Instant createdAt,
            Instant updatedAt,
            List<CategoryView> categories) {
    }

    public record GameSummary(
            UUID id,
            String title,
            String description,
            String locale,
            boolean template,
            String templateKey,
            long version,
            boolean playable,
            int categoryCount,
            Instant updatedAt) {
    }

    public record MediaView(UUID id, String fileName, String contentType, long byteSize, String url) {
    }
}
