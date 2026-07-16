package lv.zinivaimini.game.web;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lv.zinivaimini.game.config.AppProperties;

@RestController
@RequestMapping("/api/public")
public class PublicConfigController {

    private final AppProperties properties;

    public PublicConfigController(AppProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/config")
    Map<String, Object> config() {
        return Map.of(
                "clerkPublishableKey", properties.clerkPublishableKey(),
                "locales", List.of("lv", "en"));
    }
}
