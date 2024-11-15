package com.ai.hackemotion.asset;

import com.ai.hackemotion.emotion.Emotion;
import com.ai.hackemotion.emotion.EmotionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetService {
    private final AssetRepository assetRepository;
    private final EmotionRepository emotionRepository;

    public AssetService(AssetRepository assetRepository, EmotionRepository emotionRepository) {
        this.assetRepository = assetRepository;
        this.emotionRepository = emotionRepository;
    }

    public List<String> getEmotionNamesByAssetId(String assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + assetId));

        return asset.getEmotions().stream()
                .map(Emotion::getName)
                .collect(Collectors.toList());
    }

    public Asset createAsset(AssetRequest request) {
        Asset asset = Asset.builder()
                .url(request.getUrl())
                .name(request.getName())
                .build();
        return assetRepository.save(asset);
    }

    public Asset addEmotionsToAsset(String assetName, List<String> emotionNames) {
        Asset asset = assetRepository.findByName(assetName)
                .orElseThrow(() -> new RuntimeException("Asset not found with name: " + assetName));

        List<Emotion> emotions = emotionNames.stream()
                .map(name -> emotionRepository.findByName(name)
                        .orElseThrow(() -> new RuntimeException("Emotion not found: " + name)))
                .toList();

        asset.getEmotions().addAll(emotions);
        return assetRepository.save(asset);
    }

}