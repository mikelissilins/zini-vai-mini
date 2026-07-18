package lv.zinivaimini.game.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_asset_id")
    private MediaAsset mediaAsset;
    private int points;
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;
    private String prompt;
    private String answer;
    private String explanation;
    private int timeLimitSeconds;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<QuestionOption> options = new ArrayList<>();

    protected Question() {
    }

    public Question(int points, QuestionType questionType, String prompt, String answer, String explanation, MediaAsset mediaAsset) {
        this(points, questionType, prompt, answer, explanation, mediaAsset, 40);
    }

    public Question(int points, QuestionType questionType, String prompt, String answer, String explanation,
            MediaAsset mediaAsset, int timeLimitSeconds) {
        this.id = UUID.randomUUID();
        this.points = points;
        this.questionType = questionType;
        this.prompt = prompt;
        this.answer = answer;
        this.explanation = explanation;
        this.mediaAsset = mediaAsset;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    void attachTo(Category category) {
        this.category = category;
    }

    public void addOption(QuestionOption option) {
        option.attachTo(this);
        options.add(option);
        options.sort(Comparator.comparingInt(QuestionOption::getPosition));
    }

    public UUID getId() { return id; }
    public int getPoints() { return points; }
    public QuestionType getQuestionType() { return questionType; }
    public String getPrompt() { return prompt; }
    public String getAnswer() { return answer; }
    public String getExplanation() { return explanation; }
    public int getTimeLimitSeconds() { return timeLimitSeconds; }
    public MediaAsset getMediaAsset() { return mediaAsset; }
    public List<QuestionOption> getOptions() { return List.copyOf(options); }
}
