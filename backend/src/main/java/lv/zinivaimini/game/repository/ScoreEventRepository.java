package lv.zinivaimini.game.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.zinivaimini.game.domain.GameSession;
import lv.zinivaimini.game.domain.ScoreEvent;

public interface ScoreEventRepository extends JpaRepository<ScoreEvent, UUID> {
    Optional<ScoreEvent> findFirstBySessionAndUndoneFalseOrderByCreatedAtDesc(GameSession session);
    boolean existsBySessionAndUndoneFalse(GameSession session);
}
