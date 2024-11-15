package com.ai.hackemotion.asset;

import com.ai.hackemotion.emotion.Emotion;
import com.ai.hackemotion.emotion.EmotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("asset")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping("/{assetId}/add-emotions")
    public ResponseEntity<Asset> addEmotions(
            @PathVariable Long assetId,
            @RequestBody List<String> emotionNames) {
        return ResponseEntity.ok(assetService.addEmotionsToAsset(assetId, emotionNames));
    }

    @PostMapping("/create-asset")
    public ResponseEntity<Asset> createAsset(@RequestBody AssetRequest request) {
        return ResponseEntity.ok(assetService.createAsset(request));
    }

    /* @PostMapping("create-batch-assets")
    public Asset createBatchAssets(@RequestBody List<Asset> assets) {
        return assets;
    } */


}
