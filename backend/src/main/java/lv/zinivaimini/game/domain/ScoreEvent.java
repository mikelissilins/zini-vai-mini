package lv.zinivaimini.game.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "score_events")
public class ScoreEvent {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id")
    private GameSession session;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id")
    private SessionTeam team;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private SessionQuestion question;
    private int points;
    private boolean correct;
    private int activeTeamBefore;
    private int activeTeamAfter;
    private boolean undone;
    private Instant createdAt;

    protected ScoreEvent() {
    }

    public ScoreEvent(GameSession session, SessionTeam team, SessionQuestion question, int points, boolean correct,
            int activeTeamBefore, int activeTeamAfter) {
        this.id = UUID.randomUUID();
        this.session = session;
        this.team = team;
        this.question = question;
        this.points = points;
        this.correct = correct;
        this.activeTeamBefore = activeTeamBefore;
        this.activeTeamAfter = activeTeamAfter;
    }

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }

    public void markUndone() { undone = true; }

    public UUID getId() { return id; }
    public UUID getTeamId() { return team.getId(); }
    public UUID getQuestionId() { return question.getId(); }
    public int getPoints() { return points; }
    public boolean isCorrect() { return correct; }
    public int getActiveTeamBefore() { return activeTeamBefore; }
    public int getActiveTeamAfter() { return activeTeamAfter; }
    public boolean isUndone() { return undone; }
    public Instant getCreatedAt() { return createdAt; }
}
