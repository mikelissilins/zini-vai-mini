package lv.zinivaimini.game.web;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lv.zinivaimini.game.service.GameService;
import lv.zinivaimini.game.web.dto.GameDtos.CreateFromTemplateInput;
import lv.zinivaimini.game.web.dto.GameDtos.GameInput;
import lv.zinivaimini.game.web.dto.GameDtos.GameSummary;
import lv.zinivaimini.game.web.dto.GameDtos.GameView;

@RestController
@RequestMapping("/api")
@PreAuthorize("@ownerAccess.isOwner(authentication)")
public class GameController {
    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    @GetMapping("/templates")
    List<GameSummary> templates() {
        return service.listTemplates();
    }

    @GetMapping("/games")
    List<GameSummary> games() {
        return service.listGames();
    }

    @GetMapping("/games/{id}")
    GameView game(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping("/games")
    ResponseEntity<GameView> create(@Valid @RequestBody GameInput input) {
        GameView created = service.create(input);
        return ResponseEntity.created(URI.create("/api/games/" + created.id())).body(created);
    }

    @PostMapping("/games/from-template/{templateId}")
    ResponseEntity<GameView> createFromTemplate(
            @PathVariable UUID templateId,
            @Valid @RequestBody CreateFromTemplateInput input) {
        GameView created = service.createFromTemplate(templateId, input);
        return ResponseEntity.created(URI.create("/api/games/" + created.id())).body(created);
    }

    @PutMapping("/games/{id}")
    GameView update(@PathVariable UUID id, @Valid @RequestBody GameInput input) {
        return service.update(id, input);
    }

    @DeleteMapping("/games/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
