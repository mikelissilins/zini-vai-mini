package lv.zinivaimini.game.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("ownerAccess")
public class OwnerAccess {

    private final AppProperties properties;

    public OwnerAccess(AppProperties properties) {
        this.properties = properties;
    }

    public boolean isOwner(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            return false;
        }
        String ownerId = properties.ownerClerkUserId();
        return ownerId == null || ownerId.isBlank() || ownerId.equals(jwt.getSubject());
    }
}
