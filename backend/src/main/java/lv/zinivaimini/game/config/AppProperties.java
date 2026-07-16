package lv.zinivaimini.game.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String clerkPublishableKey,
        String ownerClerkUserId,
        List<String> allowedOrigins) {
}
