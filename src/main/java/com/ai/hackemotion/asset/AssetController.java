package com.ai.hackemotion.asset;

import com.ai.hackemotion.emotion.Emotion;
import com.ai.hackemotion.emotion.EmotionRequest;
import com.ai.hackemotion.emotion.EmotionService;
import com.ai.hackemotion.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("asset")
public class AssetController {
    private final AssetService assetService;
    private final EmotionService service;

    public AssetController(AssetService assetService, EmotionService request, EmotionService service) {
        this.assetService = assetService;
        this.service = service;
    }

    @PostMapping("/{assetId}/add-emotions")
    public ResponseEntity<Asset> addEmotions(
            @PathVariable Long assetId,
            @RequestBody EmotionRequest request) {
        return ResponseEntity.ok(assetService.addEmotionsToAsset(assetId, request));
    }

    @PostMapping("/create-asset")
    public ResponseEntity<Asset> createAsset(@RequestBody AssetRequest request) {
        return ResponseEntity.ok(assetService.createAsset(request));
    }

    @GetMapping("/{assetId}/emotions")
    public ResponseEntity<List<String>> getEmotionsByAssetId(@PathVariable Long assetId) {
        return ResponseEntity.ok(assetService.getEmotionNamesByAssetId(assetId));
    }



}
