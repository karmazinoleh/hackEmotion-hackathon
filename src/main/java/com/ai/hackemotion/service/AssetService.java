package com.ai.hackemotion.service;

import com.ai.hackemotion.dto.request.AssetRequest;
import com.ai.hackemotion.dto.request.EmotionRequest;
import com.ai.hackemotion.entity.Asset;

public interface AssetService {
    Asset createAsset(AssetRequest assetRequest);
    Asset addEmotionsToAsset(Long assetId, EmotionRequest request);
}
