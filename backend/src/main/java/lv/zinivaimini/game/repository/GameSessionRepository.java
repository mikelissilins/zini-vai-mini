package lv.zinivaimini.game.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.zinivaimini.game.domain.GameSession;

public interface GameSessionRepository extends JpaRepository<GameSession, UUID> {
    List<GameSession> findAllByOrderByUpdatedAtDesc();
    Optional<GameSession> findByPublicToken(String publicToken);
}
