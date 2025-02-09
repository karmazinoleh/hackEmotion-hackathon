package com.ai.hackemotion.emotion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAssetEmotionRepository extends JpaRepository<UserAssetEmotion, Long> {
    public List<String> findEmotionsByAssetId(Long assetId);
}
