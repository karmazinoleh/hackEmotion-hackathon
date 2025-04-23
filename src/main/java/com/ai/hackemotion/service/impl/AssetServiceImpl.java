package com.ai.hackemotion.service.impl;

import com.ai.hackemotion.entity.Asset;
import com.ai.hackemotion.dto.AssetRequest;
import com.ai.hackemotion.entity.Emotion;
import com.ai.hackemotion.repository.AssetRepository;
import com.ai.hackemotion.repository.EmotionRepository;
import com.ai.hackemotion.dto.EmotionRequest;
import com.ai.hackemotion.entity.UserAssetEmotion;
import com.ai.hackemotion.repository.UserAssetEmotionRepository;
import com.ai.hackemotion.entity.User;
import com.ai.hackemotion.repository.UserRepository;
import com.ai.hackemotion.service.AssetService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AssetServiceImpl implements AssetService {
    private final AssetRepository assetRepository;
    private final EmotionRepository emotionRepository;
    private final UserAssetEmotionRepository userAssetEmotionRepository;
    private final UserRepository userRepository;

    public AssetServiceImpl(AssetRepository assetRepository, EmotionRepository emotionRepository, UserAssetEmotionRepository userAssetEmotionRepository, UserRepository userRepository) {
        this.assetRepository = assetRepository;
        this.emotionRepository = emotionRepository;
        this.userAssetEmotionRepository = userAssetEmotionRepository;
        this.userRepository = userRepository;
    }

    /*public List<String> getEmotionNamesByAssetId(Long assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + assetId));

        return userAssetEmotionRepository.findEmotionsByAssetId(asset.getId());
    }*/

    public Asset createAsset(AssetRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUsername()));

        Asset asset = Asset.builder()
                .name(request.getName())
                .url(request.getUrl())
                .user(user)
                .build();

        return assetRepository.save(asset);
    }


    public Asset addEmotionsToAsset(Long assetId, EmotionRequest request) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        User user = userRepository.findByUsername(request.getUser().getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        for (Emotion emotion : request.getEmotions()) {
            Emotion existingEmotion = emotionRepository.findById(emotion.getId()) // На стороні сторінки ми передаємо назву, а не ID
                    .orElseThrow(() -> new RuntimeException("Emotion not found: " + emotion.getId()));

            UserAssetEmotion userAssetEmotion = UserAssetEmotion.builder()
                    .user(user)
                    .asset(asset)
                    .emotion(existingEmotion)
                    .build();
            userAssetEmotionRepository.save(userAssetEmotion);
        }

        return asset;
    }

}
