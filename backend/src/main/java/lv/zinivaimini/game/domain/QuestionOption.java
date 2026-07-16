package lv.zinivaimini.game.domain;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "question_options")
public class QuestionOption {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private Question question;
    private String text;
    private int position;
    private boolean correct;

    protected QuestionOption() {
    }

    public QuestionOption(String text, int position, boolean correct) {
        this.id = UUID.randomUUID();
        this.text = text;
        this.position = position;
        this.correct = correct;
    }

    void attachTo(Question question) {
        this.question = question;
    }

    public UUID getId() { return id; }
    public String getText() { return text; }
    public int getPosition() { return position; }
    public boolean isCorrect() { return correct; }
}
