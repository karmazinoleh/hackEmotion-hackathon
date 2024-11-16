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
                .name(request.getName())
                .url(request.getUrl())
                .build();

        return assetRepository.save(asset);
    }


    public Asset addEmotionsToAsset(String assetId, List<EmotionWithIntensityRequest> emotionsWithIntensity) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        emotionsWithIntensity.forEach(request -> {
            Emotion emotion = emotionRepository.findByName(request.getEmotionName())
                    .orElseThrow(() -> new RuntimeException("Emotion not found"));
            asset.getEmotions().add(new EmotionAssignment(emotion, request.getIntensity()));
        });

        return assetRepository.save(asset);
    }

    public void addAudioToAsset(String assetId, String audioUrl) {
        // Знайти актив за його ID
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found with ID: " + assetId));

        // Додати URL аудіо до активу
        asset.setUrl(audioUrl);

        // Зберегти оновлений актив у базу даних
        assetRepository.save(asset);
    }



}