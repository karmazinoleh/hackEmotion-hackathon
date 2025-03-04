package com.ai.hackemotion.emotion;

import com.ai.hackemotion.asset.Asset;
import com.ai.hackemotion.asset.AssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FinalAssetEmotionService {
    private final FinalAssetEmotionRepository finalAssetEmotionRepository;
    private final UserAssetEmotionRepository userAssetEmotionRepository;
    private final AssetRepository assetRepository; // Added repository

    public FinalAssetEmotionService(FinalAssetEmotionRepository finalAssetEmotionRepository,
                                    UserAssetEmotionRepository userAssetEmotionRepository,
                                    EmotionService emotionService,
                                    AssetRepository assetRepository) { // Added constructor parameter
        this.finalAssetEmotionRepository = finalAssetEmotionRepository;
        this.userAssetEmotionRepository = userAssetEmotionRepository;
        this.assetRepository = assetRepository;
    }

    @Transactional
    public void updateFinalEmotion(Long assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + assetId));

        List<UserAssetEmotion> userAssetEmotions = userAssetEmotionRepository.findAllByAssetId(assetId);
        Map<Emotion, Long> emotionCount = userAssetEmotions.stream()
                .collect(Collectors.groupingBy(UserAssetEmotion::getEmotion, Collectors.counting()));

        Emotion mostVotedEmotion = emotionCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        FinalAssetEmotion finalAssetEmotion = finalAssetEmotionRepository.findByAssetId(assetId)
                .orElse(new FinalAssetEmotion());

        // Set both the asset and assetId
        finalAssetEmotion.setAsset(asset);
        finalAssetEmotion.setEmotion(mostVotedEmotion);

        finalAssetEmotionRepository.save(finalAssetEmotion);
    }
}