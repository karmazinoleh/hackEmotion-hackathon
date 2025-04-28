package com.ai.hackemotion.service.impl;

import com.ai.hackemotion.dto.request.UserAssetEmotionRequest;
import com.ai.hackemotion.dto.response.AssetResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final EmotionRepository emotionRepository;
    private final UserAssetEmotionRepository userAssetEmotionRepository;
    private final UserRepository userRepository;


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

    public List<AssetResponse> getAssetsToRate(Long userId) {
        // Отримуємо всі активи, які не створені користувачем
        List<Asset> allAssets = assetRepository.findAllByUserIdNot(userId);

        // Отримуємо всі активи, які користувач вже оцінив
        List<UserAssetEmotion> userRatings = userAssetEmotionRepository.findAllByUserId(userId);
        Set<Long> ratedAssetIds = userRatings.stream()
                .map(rating -> rating.getAsset().getId())
                .collect(Collectors.toSet());

        // Фільтруємо активи, які ще не були оцінені
        List<Asset> assetsToRate = allAssets.stream()
                .filter(asset -> !ratedAssetIds.contains(asset.getId()))
                .collect(Collectors.toList());

        // Перетворюємо в об'єкти відповіді
        return assetsToRate.stream()
                .map(asset -> new AssetResponse(asset.getId(), asset.getName(), asset.getUrl()))
                .collect(Collectors.toList());
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

    public boolean isAssetAvailableForUser(String assetName, String username) {
        var user = userRepository.findByUsername(username);
        List<Asset> assets = assetRepository.findByUserId(user.get().getId());

        if (assets == null || assetName == null) {
            return false;
        }

        return assets.stream()
                .anyMatch(asset -> assetName.equals(asset.getName()));
    }

}
