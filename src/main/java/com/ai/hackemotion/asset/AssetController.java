package com.ai.hackemotion.asset;

import com.ai.hackemotion.emotion.*;
import com.ai.hackemotion.user.User;
import com.ai.hackemotion.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("asset")
public class AssetController {
    private final AssetService assetService;
    private final EmotionService service;
    private final UserAssetEmotionRepository UAErepository;
    private final UserRepository userRepository;

    public AssetController(AssetService assetService, EmotionService request, EmotionService service, UserAssetEmotionRepository repository, UserAssetEmotionRepository uaErepository, UserRepository userRepository) {
        this.assetService = assetService;
        this.service = service;
        UAErepository = uaErepository;
        this.userRepository = userRepository;
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

    /*@GetMapping("/{assetId}/emotions")
    public ResponseEntity<List<String>> getEmotionsByAssetId(@PathVariable Long assetId) {
        return ResponseEntity.ok(assetService.getEmotionNamesByAssetId(assetId));
    }*/

    @GetMapping("/{username}")
    public ResponseEntity<List<UserAssetEmotionRequest>> getAssets(@PathVariable String username) {
        Long userId = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Assets not found!")).getId();

        List<UserAssetEmotion> assetsList = UAErepository.findAllByUserId(userId);
        Map<Long, UserAssetEmotionRequest> assetMap = new HashMap<>();

        for (UserAssetEmotion asset : assetsList) {
            Long assetId = asset.getAsset().getId();
            assetMap.putIfAbsent(assetId, new UserAssetEmotionRequest(assetId, asset.getAsset().getName(), new ArrayList<>()));
            String emotionName = asset.getEmotion().getName();
            if (!assetMap.get(assetId).getEmotionNames().contains(emotionName)) {
                assetMap.get(assetId).getEmotionNames().add(emotionName);
            }
        }

        return ResponseEntity.ok(new ArrayList<>(assetMap.values()));
    }




}
