package lv.zinivaimini.game.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GameSessionTests {

    @Test
    void correctAnswerScoresRotatesAndUndoRestoresTurn() {
        Fixture fixture = fixture();

        fixture.session.selectQuestion(fixture.question);
        fixture.session.revealAnswer();
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
        fixture.session.revealAnswer();

        ScoreEvent event = fixture.session.score(fixture.firstTeam, fixture.question, false, 2);

        assertThat(event.getPoints()).isZero();
        assertThat(fixture.firstTeam.getScore()).isZero();
        assertThat(fixture.question.isUsed()).isTrue();
        assertThat(fixture.session.getActiveTeamIndex()).isEqualTo(1);
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
