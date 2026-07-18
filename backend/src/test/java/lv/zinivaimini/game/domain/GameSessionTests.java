package lv.zinivaimini.game.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class GameSessionTests {

    @Test
    void correctAnswerScoresRotatesAndUndoRestoresTheRevealedQuestion() {
        Fixture fixture = fixture();

        fixture.session.selectQuestion(fixture.question);
        fixture.session.revealAnswer(null);
        ScoreEvent event = fixture.session.score(fixture.firstTeam, fixture.question, true, 2);

        assertThat(fixture.firstTeam.getScore()).isEqualTo(30);
        assertThat(fixture.question.isUsed()).isTrue();
        assertThat(fixture.session.getActiveTeamIndex()).isEqualTo(1);
        assertThat(fixture.session.getSelectedQuestionId()).isNull();

        fixture.session.undo(event, fixture.firstTeam, fixture.question);

        assertThat(fixture.firstTeam.getScore()).isZero();
        assertThat(fixture.question.isUsed()).isFalse();
        assertThat(fixture.session.getActiveTeamIndex()).isZero();
        assertThat(fixture.session.getSelectedQuestionId()).isEqualTo(fixture.question.getId());
        assertThat(fixture.session.isAnswerRevealed()).isTrue();
        assertThat(event.isUndone()).isTrue();
    }

    @Test
    void incorrectAnswerScoresZeroAndStillRotates() {
        Fixture fixture = fixture();
        fixture.session.selectQuestion(fixture.question);
        fixture.session.revealAnswer(null);

        ScoreEvent event = fixture.session.score(fixture.firstTeam, fixture.question, false, 2);

        assertThat(event.getPoints()).isZero();
        assertThat(fixture.firstTeam.getScore()).isZero();
        assertThat(fixture.question.isUsed()).isTrue();
        assertThat(fixture.session.getActiveTeamIndex()).isEqualTo(1);
    }

    @Test
    void hideAnswerKeepsTheSameQuestionAndTurnReadyToRevealAgain() {
        Fixture fixture = fixture();
        fixture.session.selectQuestion(fixture.question);
        fixture.session.revealAnswer(UUID.randomUUID());

        fixture.session.hideAnswer();

        assertThat(fixture.session.getSelectedQuestionId()).isEqualTo(fixture.question.getId());
        assertThat(fixture.session.getSelectedOptionId()).isNull();
        assertThat(fixture.session.isAnswerRevealed()).isFalse();
        assertThat(fixture.question.isUsed()).isFalse();
        assertThat(fixture.session.getActiveTeamIndex()).isZero();

        fixture.session.clearSelectedQuestion();

        assertThat(fixture.session.getSelectedQuestionId()).isNull();
    }

    @Test
    void hintReducesTheAwardedScoreByFivePoints() {
        Fixture fixture = fixture();
        fixture.session.selectQuestion(fixture.question);
        fixture.session.useHint(fixture.question);
        fixture.session.revealAnswer(null);

        ScoreEvent event = fixture.session.score(fixture.firstTeam, fixture.question, true, 2);

        assertThat(event.getPoints()).isEqualTo(25);
        assertThat(fixture.firstTeam.getScore()).isEqualTo(25);
        assertThat(fixture.question.isHintUsed()).isTrue();

        fixture.session.undo(event, fixture.firstTeam, fixture.question);
        fixture.session.useHint(fixture.question);
        fixture.session.revealAnswer(null);
        ScoreEvent restored = fixture.session.score(fixture.firstTeam, fixture.question, true, 2);

        assertThat(restored.getPoints()).isEqualTo(30);
    }

    private Fixture fixture() {
        Game game = new Game("Testa spēle", null, "lv");
        Category category = new Category("Bībele", "#0E758C", 0);
        Question source = new Question(30, QuestionType.FREE_TEXT, "Jautājums?", "Atbilde", null, null);
        category.addQuestion(source);
        game.addCategory(category);
        GameSession session = new GameSession(game, "public-token");
        return new Fixture(session, new SessionTeam(session, "Viļņi", "#0E758C", 0),
                new SessionTeam(session, "Bākas", "#F77F5B", 1), new SessionQuestion(session, category, source));
    }

    private record Fixture(GameSession session, SessionTeam firstTeam, SessionTeam secondTeam, SessionQuestion question) {
    }
}
