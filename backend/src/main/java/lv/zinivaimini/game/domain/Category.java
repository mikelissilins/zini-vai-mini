package lv.zinivaimini.game.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id")
    private Game game;
    private String name;
    private String color;
    private int position;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("points ASC")
    private List<Question> questions = new ArrayList<>();

    protected Category() {
    }

    public Category(String name, String color, int position) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.color = color;
        this.position = position;
    }

    void attachTo(Game game) {
        this.game = game;
    }

    public void addQuestion(Question question) {
        question.attachTo(this);
        questions.add(question);
        questions.sort(Comparator.comparingInt(Question::getPoints));
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public int getPosition() { return position; }
    public List<Question> getQuestions() { return List.copyOf(questions); }
}
