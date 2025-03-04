package com.ai.hackemotion.emotion;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FinalAssetEmotionRepository extends JpaRepository<FinalAssetEmotion, Long> {
    Optional<FinalAssetEmotion> findByAssetId(Long assetId);
}

