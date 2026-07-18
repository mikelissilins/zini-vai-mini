package lv.zinivaimini.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(properties = "app.owner-clerk-user-id=owner-user")
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class GameSessionApiIntegrationTests {
    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"));

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void campTemplateIsReadyWithQuestionsAnswersAndHints() throws Exception {
        JsonNode templates = json.readTree(mvc.perform(get("/api/templates").with(owner()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        JsonNode camp = templates.valueStream()
                .filter(template -> "camp".equals(template.path("templateKey").asText()))
                .findFirst().orElseThrow();
        JsonNode game = json.readTree(mvc.perform(get("/api/games/{id}", camp.path("id").asText()).with(owner()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());

        assertThat(game.path("playable").asBoolean()).isTrue();
        assertThat(game.path("categories")).hasSize(6);
        assertThat(game.path("categories").valueStream().flatMap(category -> category.path("questions").valueStream()))
                .hasSize(30)
                .allSatisfy(question -> {
                    assertThat(question.path("prompt").asText()).isNotBlank();
                    assertThat(question.path("answer").asText()).isNotBlank();
                    assertThat(question.path("explanation").asText()).isNotBlank();
                });
    }

    @Test
    void existingGameCanBeSavedWithoutCategoryPositionConflicts() throws Exception {
        JsonNode created = request(post("/api/games"), completeGame());
        Map<String, Object> update = new HashMap<>(completeGame());
        update.put("title", "Rediģēta integrācijas spēle");
        update.put("version", created.path("version").asLong());

        JsonNode updated = request(put("/api/games/{id}", created.path("id").asText()), update);

        assertThat(updated.path("title").asText()).isEqualTo("Rediģēta integrācijas spēle");
        assertThat(updated.path("categories")).hasSize(1);
        assertThat(updated.path("categories").get(0).path("questions")).hasSize(5);
    }

    @Test
    void scoresPersistsRotatesAndUndoRestoresSession() throws Exception {
        JsonNode game = request(post("/api/games"), completeGame()).path("id");
        JsonNode session = request(post("/api/sessions"), Map.of(
                "gameId", game.asText(),
                "teams", List.of(
                        Map.of("name", "Viļņi", "color", "#0E758C"),
                        Map.of("name", "Bākas", "color", "#F77F5B"))));
        String sessionId = session.path("id").asText();
        String questionId = session.path("categories").get(0).path("questions").get(0).path("id").asText();

        session = request(post("/api/sessions/{id}/select", sessionId), Map.of("questionId", questionId, "version", 0));
        session = request(post("/api/sessions/{id}/reveal", sessionId), Map.of("version", session.path("version").asLong()));
        session = request(post("/api/sessions/{id}/score", sessionId), Map.of(
                "correct", true, "version", session.path("version").asLong()));

        assertThat(session.path("teams").get(0).path("score").asInt()).isEqualTo(10);
        assertThat(session.path("activeTeamIndex").asInt()).isEqualTo(1);
        assertThat(session.path("usedCount").asInt()).isEqualTo(1);

        JsonNode restored = json.readTree(mvc.perform(get("/api/sessions/{id}", sessionId).with(owner()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertThat(restored.path("teams").get(0).path("score").asInt()).isEqualTo(10);

        JsonNode undone = request(post("/api/sessions/{id}/undo", sessionId), Map.of("version", restored.path("version").asLong()));
        assertThat(undone.path("teams").get(0).path("score").asInt()).isZero();
        assertThat(undone.path("activeTeamIndex").asInt()).isZero();
        assertThat(undone.path("usedCount").asInt()).isZero();
        assertThat(undone.path("selectedQuestion").path("id").asText()).isEqualTo(questionId);
        assertThat(undone.path("answerRevealed").asBoolean()).isFalse();
    }

    private JsonNode request(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request,
            Object body) throws Exception {
        String response = mvc.perform(request.with(owner()).contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsBytes(body)))
                .andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
        return json.readTree(response);
    }

    private org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor owner() {
        return jwt().jwt(token -> token.subject("owner-user"));
    }

    private Map<String, Object> completeGame() {
        return Map.of(
                "title", "Integrācijas spēle",
                "locale", "lv",
                "categories", List.of(Map.of(
                        "name", "Bībele",
                        "color", "#0E758C",
                        "questions", List.of(10, 20, 30, 40, 50).stream().map(points -> Map.of(
                                "points", points,
                                "type", "FREE_TEXT",
                                "prompt", "Jautājums par " + points,
                                "answer", "Atbilde " + points,
                                "options", List.of())).toList())));
    }
}
