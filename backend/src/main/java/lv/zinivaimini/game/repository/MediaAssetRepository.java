package lv.zinivaimini.game.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import lv.zinivaimini.game.domain.MediaAsset;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {
}
