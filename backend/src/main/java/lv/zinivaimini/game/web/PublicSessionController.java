package lv.zinivaimini.game.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lv.zinivaimini.game.service.SessionService;
import lv.zinivaimini.game.web.dto.SessionDtos.SessionView;

@RestController
@RequestMapping("/api/public/sessions")
public class PublicSessionController {
    private final SessionService service;

    public PublicSessionController(SessionService service) {
        this.service = service;
    }

    @GetMapping("/{token}")
    SessionView session(@PathVariable String token) {
        return service.getPublic(token);
    }
}
