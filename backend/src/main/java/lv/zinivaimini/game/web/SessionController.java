package lv.zinivaimini.game.web;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lv.zinivaimini.game.service.SessionService;
import lv.zinivaimini.game.web.dto.SessionDtos.CreateSessionInput;
import lv.zinivaimini.game.web.dto.SessionDtos.ScoreInput;
import lv.zinivaimini.game.web.dto.SessionDtos.RevealInput;
import lv.zinivaimini.game.web.dto.SessionDtos.SelectQuestionInput;
import lv.zinivaimini.game.web.dto.SessionDtos.SessionSummary;
import lv.zinivaimini.game.web.dto.SessionDtos.SessionView;
import lv.zinivaimini.game.web.dto.SessionDtos.VersionInput;

@RestController
@RequestMapping("/api")
@PreAuthorize("@ownerAccess.isOwner(authentication)")
public class SessionController {
    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    @GetMapping("/sessions")
    List<SessionSummary> sessions() { return service.list(); }

    @PostMapping("/sessions")
    ResponseEntity<SessionView> create(@Valid @RequestBody CreateSessionInput input) {
        SessionView created = service.create(input);
        return ResponseEntity.created(URI.create("/api/sessions/" + created.id())).body(created);
    }

    @GetMapping("/sessions/{id}")
    SessionView session(@PathVariable UUID id) { return service.get(id); }

    @PostMapping("/sessions/{id}/select")
    SessionView select(@PathVariable UUID id, @Valid @RequestBody SelectQuestionInput input) {
        return service.select(id, input.questionId(), input.version());
    }

    @PostMapping("/sessions/{id}/reveal")
    SessionView reveal(@PathVariable UUID id, @RequestBody RevealInput input) {
        return service.reveal(id, input.optionId(), input.version());
    }

    @PostMapping("/sessions/{id}/hint")
    SessionView hint(@PathVariable UUID id, @RequestBody VersionInput input) {
        return service.useHint(id, input.version());
    }

    @PostMapping("/sessions/{id}/score")
    SessionView score(@PathVariable UUID id, @RequestBody ScoreInput input) {
        return service.score(id, input.correct(), input.version());
    }

    @PostMapping("/sessions/{id}/undo")
    SessionView undo(@PathVariable UUID id, @RequestBody VersionInput input) {
        return service.undo(id, input.version());
    }

    @PostMapping("/sessions/{id}/finish")
    SessionView finish(@PathVariable UUID id, @RequestBody VersionInput input) {
        return service.finish(id, input.version());
    }
}
