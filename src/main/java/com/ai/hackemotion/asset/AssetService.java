package com.ai.hackemotion.asset;

import com.ai.hackemotion.emotion.Emotion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public Asset saveAsset(String assetName, String url, List<Emotion> emotions) {
        Asset asset = Asset.builder()
                .url(url)
                .emotions(emotions)
                .name(assetName)
                .build();

        return assetRepository.save(asset);
    }


    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }
}
