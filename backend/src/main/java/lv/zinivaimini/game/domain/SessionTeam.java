package lv.zinivaimini.game.domain;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "session_teams")
public class SessionTeam {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id")
    private GameSession session;
    private String name;
    private String color;
    private int position;
    private int score;

    protected SessionTeam() {
    }

    public SessionTeam(GameSession session, String name, String color, int position) {
        this.id = UUID.randomUUID();
        this.session = session;
        this.name = name;
        this.color = color;
        this.position = position;
    }

    public void addScore(int points) {
        score += points;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public int getPosition() { return position; }
    public int getScore() { return score; }
}
