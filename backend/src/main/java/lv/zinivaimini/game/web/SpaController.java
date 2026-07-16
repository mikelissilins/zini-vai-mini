package lv.zinivaimini.game.web;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SpaController {

    @GetMapping({ "/", "/dashboard" })
    String root() {
        return "forward:/index.html";
    }

    @GetMapping({ "/games/{id}/edit", "/games/{id}/start", "/sessions/{id}/host" })
    String privateRoute(@PathVariable UUID id) {
        return "forward:/index.html";
    }

    @GetMapping("/live/{token}")
    String publicRoute(@PathVariable String token) {
        return "forward:/index.html";
    }
}
