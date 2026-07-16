package lv.zinivaimini.game.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.zinivaimini.game.domain.SessionTeam;

public interface SessionTeamRepository extends JpaRepository<SessionTeam, UUID> {
    List<SessionTeam> findBySessionIdOrderByPositionAsc(UUID sessionId);
}
