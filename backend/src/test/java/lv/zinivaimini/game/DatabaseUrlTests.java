package lv.zinivaimini.game;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DatabaseUrlTests {

	@Test
	void removesRenderCredentialsFromJdbcUrl() {
		String renderUrl = "postgresql://game_user:secret@db.example.com:5432/game?sslmode=require";

		assertThat(ZiniVaiMiniApplication.normalizeDatabaseUrl(renderUrl))
				.isEqualTo("jdbc:postgresql://db.example.com:5432/game?sslmode=require");
	}

	@Test
	void leavesJdbcUrlUnchanged() {
		String jdbcUrl = "jdbc:postgresql://localhost:5432/game";

		assertThat(ZiniVaiMiniApplication.normalizeDatabaseUrl(jdbcUrl)).isEqualTo(jdbcUrl);
	}
}
