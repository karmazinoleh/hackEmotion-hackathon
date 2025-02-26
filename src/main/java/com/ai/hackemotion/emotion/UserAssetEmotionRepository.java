package com.ai.hackemotion.emotion;

import com.ai.hackemotion.asset.Asset;
import com.ai.hackemotion.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserAssetEmotionRepository extends JpaRepository<UserAssetEmotion, Long> {
    @Query("SELECT uae FROM UserAssetEmotion uae WHERE uae.asset.id = :assetId")
    List<UserAssetEmotion> findEmotionsByAssetId(Long assetId);

    public List<UserAssetEmotion> findAllByUserId(Long userId);

    List<UserAssetEmotion> findAllByAssetId(Long assetId);

    List<UserAssetEmotion> findAllByUserIdNot (Long userId);
    boolean existsByUserIdAndAssetId(Long userId, Long assetId);
}
