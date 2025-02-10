package com.ai.hackemotion.asset;

import com.ai.hackemotion.emotion.Emotion;
import com.ai.hackemotion.emotion.EmotionRepository;
import com.ai.hackemotion.emotion.EmotionRequest;
import com.ai.hackemotion.emotion.UserAssetEmotion;
import com.ai.hackemotion.emotion.UserAssetEmotionRepository;
import com.ai.hackemotion.user.User;
import com.ai.hackemotion.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssetService {
    private final AssetRepository assetRepository;
    private final EmotionRepository emotionRepository;
    private final UserAssetEmotionRepository userAssetEmotionRepository;
    private final UserRepository userRepository;

    public AssetService(AssetRepository assetRepository, EmotionRepository emotionRepository, UserAssetEmotionRepository userAssetEmotionRepository, UserRepository userRepository) {
        this.assetRepository = assetRepository;
        this.emotionRepository = emotionRepository;
        this.userAssetEmotionRepository = userAssetEmotionRepository;
        this.userRepository = userRepository;
    }

    public List<String> getEmotionNamesByAssetId(Long assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + assetId));

        return userAssetEmotionRepository.findEmotionsByAssetId(asset.getId());
    }

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

        User user_test = userRepository.findByEmail("westwest@gmail.com")
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        for (Emotion emotion : request.getEmotions()) {
            Emotion existingEmotion = emotionRepository.findById(emotion.getId()) // На стороні сторінки ми передаємо назву, а не ID
                    .orElseThrow(() -> new RuntimeException("Emotion not found: " + emotion.getId()));

            UserAssetEmotion userAssetEmotion = UserAssetEmotion.builder()
                    .user(user_test)
                    .asset(asset)
                    .emotion(existingEmotion)
                    .build();
            userAssetEmotionRepository.save(userAssetEmotion);
        }

        return asset;
    }

}
