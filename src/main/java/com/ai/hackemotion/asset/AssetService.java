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

    public List<String> getEmotionNamesWithIntensityByAssetId(String assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + assetId));

        return asset.getEmotions().stream()
                .map(assignment -> assignment.getEmotion().getName() + " (Intensity: " + assignment.getIntensity() + ")")
                .collect(Collectors.toList());
    }

    public Asset createAsset(AssetRequest request) {
        Asset asset = Asset.builder()
                .url(request.getUrl())
                .name(request.getName())
                .build();
        return assetRepository.save(asset);
    }

    public Asset addEmotionsToAsset(String assetId, List<EmotionWithIntensityRequest> emotionsWithIntensity) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + assetId));

        List<EmotionAssignment> assignments = emotionsWithIntensity.stream()
                .map(request -> {
                    Emotion emotion = emotionRepository.findByName(request.getEmotionName())
                            .orElseThrow(() -> new RuntimeException("Emotion not found: " + request.getEmotionName()));

                    return EmotionAssignment.builder()
                            .emotion(emotion)
                            .intensity(request.getIntensity())
                            .build();
                })
                .collect(Collectors.toList());

        asset.getEmotions().addAll(assignments);
        return assetRepository.save(asset);
    }

}