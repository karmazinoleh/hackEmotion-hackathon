package com.ai.hackemotion.service.impl;

import com.ai.hackemotion.dto.request.UserAssetEmotionRequest;
import com.ai.hackemotion.entity.Asset;
import com.ai.hackemotion.dto.request.AssetRequest;
import com.ai.hackemotion.entity.Emotion;
import com.ai.hackemotion.repository.AssetRepository;
import com.ai.hackemotion.repository.EmotionRepository;
import com.ai.hackemotion.dto.request.EmotionRequest;
import com.ai.hackemotion.entity.UserAssetEmotion;
import com.ai.hackemotion.repository.UserAssetEmotionRepository;
import com.ai.hackemotion.entity.User;
import com.ai.hackemotion.repository.UserRepository;
import com.ai.hackemotion.service.AssetService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<UserAssetEmotionRequest> getAssetsByUsername(String username) {
        Long userId = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Assets not found!")).getId();

        List<UserAssetEmotion> assetsList = userAssetEmotionRepository.findAllByUserId(userId);
        Map<Long, UserAssetEmotionRequest> assetMap = new HashMap<>();

        for (UserAssetEmotion asset : assetsList) {
            Long assetId = asset.getAsset().getId();
            assetMap.putIfAbsent(assetId, new UserAssetEmotionRequest(assetId, asset.getAsset().getName(), new ArrayList<>()));
            String emotionName = asset.getEmotion().getName();
            if (!assetMap.get(assetId).getEmotionNames().contains(emotionName)) {
                assetMap.get(assetId).getEmotionNames().add(emotionName);
            }
        }

        return new ArrayList<>(assetMap.values());
    }

    public List<UserAssetEmotionRequest> getAllAssets() {
        List<UserAssetEmotion> assetsList = userAssetEmotionRepository.findAll();
        Map<Long, UserAssetEmotionRequest> assetMap = new HashMap<>();

        for (UserAssetEmotion asset : assetsList) {
            Long assetId = asset.getAsset().getId();
            assetMap.putIfAbsent(assetId, new UserAssetEmotionRequest(assetId, asset.getAsset().getName(), new ArrayList<>()));
            String emotionName = asset.getEmotion().getName();
            if (!assetMap.get(assetId).getEmotionNames().contains(emotionName)) {
                assetMap.get(assetId).getEmotionNames().add(emotionName);
            }
        }

        return new ArrayList<>(assetMap.values());
    }

}
