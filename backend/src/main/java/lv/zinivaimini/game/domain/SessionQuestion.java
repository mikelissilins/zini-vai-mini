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
@Table(name = "session_questions")
public class SessionQuestion {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id")
    private GameSession session;
    private UUID sourceQuestionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_asset_id")
    private MediaAsset mediaAsset;
    private String categoryName;
    private String categoryColor;
    private int categoryPosition;
    private int points;
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;
    private String prompt;
    private String answer;
    private String explanation;
    private boolean used;
    private boolean hintUsed;

    @OneToMany(mappedBy = "sessionQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<SessionQuestionOption> options = new ArrayList<>();

    protected SessionQuestion() {
    }

    public SessionQuestion(GameSession session, Category category, Question question) {
        this.id = UUID.randomUUID();
        this.session = session;
        this.sourceQuestionId = question.getId();
        this.mediaAsset = question.getMediaAsset();
        this.categoryName = category.getName();
        this.categoryColor = category.getColor();
        this.categoryPosition = category.getPosition();
        this.points = question.getPoints();
        this.questionType = question.getQuestionType();
        this.prompt = question.getPrompt();
        this.answer = question.getAnswer();
        this.explanation = question.getExplanation();
        question.getOptions().forEach(option -> addOption(new SessionQuestionOption(
                option.getText(), option.getPosition(), option.isCorrect())));
    }

    public void addOption(SessionQuestionOption option) {
        option.attachTo(this);
        options.add(option);
        options.sort(Comparator.comparingInt(SessionQuestionOption::getPosition));
    }

    public void markUsed() { used = true; }
    public void markUnused() { used = false; }
    public void toggleHint() { hintUsed = !hintUsed; }

    public UUID getId() { return id; }
    public String getCategoryName() { return categoryName; }
    public String getCategoryColor() { return categoryColor; }
    public int getCategoryPosition() { return categoryPosition; }
    public int getPoints() { return points; }
    public QuestionType getQuestionType() { return questionType; }
    public String getPrompt() { return prompt; }
    public String getAnswer() { return answer; }
    public String getExplanation() { return explanation; }
    public MediaAsset getMediaAsset() { return mediaAsset; }
    public boolean isUsed() { return used; }
    public boolean isHintUsed() { return hintUsed; }
    public List<SessionQuestionOption> getOptions() { return List.copyOf(options); }
}
