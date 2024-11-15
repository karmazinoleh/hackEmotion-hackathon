package com.ai.hackemotion.asset;

import com.ai.hackemotion.emotion.Emotion;
import com.ai.hackemotion.emotion.EmotionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final EmotionRepository emotionRepository;

    public AssetService(AssetRepository assetRepository, EmotionRepository emotionRepository) {
        this.assetRepository = assetRepository;
        this.emotionRepository = emotionRepository;
    }

    @Transactional
    public Asset createAsset(AssetRequest request) {
        Asset asset = Asset.builder()
                .url(request.getUrl())
                .name(request.getName())
                .build();
        return assetRepository.save(asset);
    }

    @Transactional
    public Asset addEmotionsToAsset(Long assetId, List<String> emotionNames) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new EntityNotFoundException("Asset not found with id: " + assetId));

        Set<Emotion> emotions = emotionNames.stream()
                .map(name -> emotionRepository.findByName(name)
                        .orElseThrow(() -> new EntityNotFoundException("Emotion not found: " + name)))
                .collect(Collectors.toSet());

        for (Emotion emotion : emotions) {
            asset.addEmotion(emotion);
        }

        return assetRepository.save(asset);
    }
}