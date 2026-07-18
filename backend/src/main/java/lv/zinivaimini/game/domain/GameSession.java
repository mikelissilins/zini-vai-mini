package lv.zinivaimini.game.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.springframework.dao.OptimisticLockingFailureException;

import lv.zinivaimini.game.service.InvalidGameException;

@Entity
@Table(name = "game_sessions")
public class GameSession {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id")
    private Game game;
    private String publicToken;
    private String title;
    private String locale;
    @Enumerated(EnumType.STRING)
    private SessionStatus status;
    private int activeTeamIndex;
    private UUID selectedQuestionId;
    private UUID selectedOptionId;
    private boolean answerRevealed;
    @Version
    private long version;
    private Instant createdAt;
    private Instant updatedAt;

    protected GameSession() {
    }

    public GameSession(Game game, String publicToken) {
        this.id = UUID.randomUUID();
        this.game = game;
        this.publicToken = publicToken;
        this.title = game.getTitle();
        this.locale = game.getLocale();
        this.status = SessionStatus.ACTIVE;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void requireVersion(long expected) {
        if (version != expected) {
            throw new OptimisticLockingFailureException("Sesija ir mainīta citā vadītāja logā. Pārlādē spēli.");
        }
    }

    public void selectQuestion(SessionQuestion question) {
        requireActive();
        if (question.isUsed()) throw new InvalidGameException("Šis jautājums jau ir izmantots.");
        selectedQuestionId = question.getId();
        selectedOptionId = null;
        answerRevealed = false;
    }

    public void revealAnswer(UUID optionId) {
        requireActive();
        if (selectedQuestionId == null) throw new InvalidGameException("Vispirms izvēlies jautājumu.");
        answerRevealed = true;
        selectedOptionId = optionId;
    }

    public void hideAnswer() {
        requireActive();
        if (selectedQuestionId == null || !answerRevealed) {
            throw new InvalidGameException("Nav atklātas atbildes, ko paslēpt.");
        }
        selectedOptionId = null;
        answerRevealed = false;
    }

    public void clearSelectedQuestion() {
        requireActive();
        if (selectedQuestionId == null || answerRevealed) {
            throw new InvalidGameException("Nav jautājuma, pie kura atgriezties.");
        }
        selectedQuestionId = null;
        selectedOptionId = null;
    }

    public void useHint(SessionQuestion question) {
        requireActive();
        if (!question.getId().equals(selectedQuestionId)) {
            throw new InvalidGameException("Vispirms izvēlies jautājumu.");
        }
        question.toggleHint();
    }

    public ScoreEvent score(SessionTeam team, SessionQuestion question, boolean correct, int teamCount) {
        requireActive();
        if (!answerRevealed || !question.getId().equals(selectedQuestionId)) {
            throw new InvalidGameException("Pirms vērtēšanas atklāj izvēlētā jautājuma atbildi.");
        }
        int before = activeTeamIndex;
        int awarded = correct ? question.getPoints() - (question.isHintUsed() ? 5 : 0) : 0;
        team.addScore(awarded);
        question.markUsed();
        activeTeamIndex = (activeTeamIndex + 1) % teamCount;
        selectedQuestionId = null;
        selectedOptionId = null;
        answerRevealed = false;
        return new ScoreEvent(this, team, question, awarded, correct, before, activeTeamIndex);
    }

    public void undo(ScoreEvent event, SessionTeam team, SessionQuestion question) {
        team.addScore(-event.getPoints());
        question.markUnused();
        activeTeamIndex = event.getActiveTeamBefore();
        selectedQuestionId = question.getId();
        selectedOptionId = null;
        answerRevealed = true;
        status = SessionStatus.ACTIVE;
        event.markUndone();
    }

    public void finish() {
        status = SessionStatus.FINISHED;
        selectedQuestionId = null;
        selectedOptionId = null;
        answerRevealed = false;
    }

    private void requireActive() {
        if (status != SessionStatus.ACTIVE) throw new InvalidGameException("Spēle jau ir pabeigta.");
    }

    public UUID getId() { return id; }
    public UUID getGameId() { return game.getId(); }
    public String getPublicToken() { return publicToken; }
    public String getTitle() { return title; }
    public String getLocale() { return locale; }
    public SessionStatus getStatus() { return status; }
    public int getActiveTeamIndex() { return activeTeamIndex; }
    public UUID getSelectedQuestionId() { return selectedQuestionId; }
    public UUID getSelectedOptionId() { return selectedOptionId; }
    public boolean isAnswerRevealed() { return answerRevealed; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
