package lv.zinivaimini.game.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lv.zinivaimini.game.service.SessionEventHub;
import lv.zinivaimini.game.service.SessionService;
import lv.zinivaimini.game.web.dto.SessionDtos.SessionView;

@RestController
@RequestMapping("/api/public/sessions")
public class PublicSessionController {
    private final SessionService service;
    private final SessionEventHub eventHub;

    public PublicSessionController(SessionService service, SessionEventHub eventHub) {
        this.service = service;
        this.eventHub = eventHub;
    }

    @GetMapping("/{token}")
    SessionView session(@PathVariable String token) {
        return service.getPublic(token);
    }

    @GetMapping(value = "/{token}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter events(@PathVariable String token) {
        return eventHub.subscribe(token, service.getPublic(token));
    }
}
