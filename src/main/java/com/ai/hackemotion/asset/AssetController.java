package com.ai.hackemotion.asset;

import com.ai.hackemotion.emotion.*;
import com.ai.hackemotion.user.User;
import com.ai.hackemotion.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("asset")
public class AssetController {
    private final AssetService assetService;
    private final EmotionService service;
    private final UserAssetEmotionRepository UAErepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final EmotionRepository emotionRepository;
    private final FinalAssetEmotionService finalAssetEmotionService;

    public AssetController(AssetService assetService, EmotionService request,
                           EmotionService service, UserAssetEmotionRepository repository,
                           UserAssetEmotionRepository uaErepository,
                           UserRepository userRepository, AssetRepository assetRepository,
                           EmotionRepository emotionRepository,
                           FinalAssetEmotionService finalAssetEmotionService) {
        this.assetService = assetService;
        this.service = service;
        this.UAErepository = uaErepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.emotionRepository = emotionRepository;
        this.finalAssetEmotionService = finalAssetEmotionService;
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

    @GetMapping("/rate/{username}")
    public ResponseEntity<List<AssetResponse>> getAssetsToRate(@PathVariable String username) {
        // Get the user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();

        // Get all assets that the user has NOT created
        List<Asset> allAssets = assetRepository.findAllByUserIdNot(userId);

        // Get all assets that the user has already rated
        List<UserAssetEmotion> userRatings = UAErepository.findAllByUserId(userId);
        Set<Long> ratedAssetIds = userRatings.stream()
                .map(rating -> rating.getAsset().getId())
                .collect(Collectors.toSet());

        // Filter out assets that the user has already rated
        List<Asset> assetsToRate = allAssets.stream()
                .filter(asset -> !ratedAssetIds.contains(asset.getId()))
                .collect(Collectors.toList());

        // Convert to response objects
        List<AssetResponse> response = assetsToRate.stream()
                .map(asset -> new AssetResponse(asset.getId(), asset.getName(), asset.getUrl()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
    // todo: add security check – if user tries to rate asset he already rated
    @PostMapping("/rate/{username}")
    public ResponseEntity<String> voteForEmotion(@PathVariable String username, @RequestBody VoteRequest voteRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long assetId = voteRequest.getAssetId();

        // Check if user has already rated this asset
        boolean alreadyRated = UAErepository.existsByUserIdAndAssetId(user.getId(), assetId);
        if (alreadyRated) {
            return ResponseEntity.badRequest().body("You have already rated this asset");
        }

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        Emotion emotion = emotionRepository.findById(voteRequest.getEmotionId())
                .orElseThrow(() -> new RuntimeException("Emotion not found"));

        UserAssetEmotion vote = new UserAssetEmotion();
        vote.setUser(user);
        vote.setAsset(asset);
        vote.setEmotion(emotion);

        UAErepository.save(vote);
        finalAssetEmotionService.updateFinalEmotion(assetId);

        return ResponseEntity.ok("Vote recorded");
    }





}
