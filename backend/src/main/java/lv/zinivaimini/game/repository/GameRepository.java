package lv.zinivaimini.game.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.zinivaimini.game.domain.Game;

public interface GameRepository extends JpaRepository<Game, UUID> {
    List<Game> findByTemplateOrderByCreatedAtAsc(boolean template);
}
