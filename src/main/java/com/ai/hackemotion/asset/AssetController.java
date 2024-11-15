package com.ai.hackemotion.asset;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.List;

@RestController
@RequestMapping("asset")
public class AssetController {
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping("/{assetName}/add-emotions")
    public ResponseEntity<Asset> addEmotions(
            @PathVariable String assetName,
            @RequestBody List<String> emotionNames) {
        return ResponseEntity.ok(assetService.addEmotionsToAsset(assetName, emotionNames));
    }

    @PostMapping("/create-asset")
    public ResponseEntity<Asset> createAsset(@RequestBody AssetRequest request) {
        return ResponseEntity.ok(assetService.createAsset(request));
    }

    @GetMapping("/{assetId}/emotions")
    public ResponseEntity<List<String>> getEmotionsByAssetId(@PathVariable String assetId) {
        return ResponseEntity.ok(assetService.getEmotionNamesByAssetId(assetId));
    }


}
