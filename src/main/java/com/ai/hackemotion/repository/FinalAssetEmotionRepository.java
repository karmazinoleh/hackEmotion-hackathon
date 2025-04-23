package com.ai.hackemotion.repository;

import com.ai.hackemotion.entity.FinalAssetEmotion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FinalAssetEmotionRepository extends JpaRepository<FinalAssetEmotion, Long> {
    Optional<FinalAssetEmotion> findByAssetId(Long assetId);
}

