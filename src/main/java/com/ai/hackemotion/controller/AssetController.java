package com.ai.hackemotion.controller;

import com.ai.hackemotion.dto.request.AssetRequest;
import com.ai.hackemotion.dto.request.EmotionRequest;
import com.ai.hackemotion.dto.request.UserAssetEmotionRequest;
import com.ai.hackemotion.dto.request.VoteRequest;
import com.ai.hackemotion.dto.response.AssetResponse;
import com.ai.hackemotion.entity.Asset;
import com.ai.hackemotion.entity.Emotion;
import com.ai.hackemotion.entity.UserAssetEmotion;
import com.ai.hackemotion.repository.AssetRepository;
import com.ai.hackemotion.repository.EmotionRepository;
import com.ai.hackemotion.repository.UserAssetEmotionRepository;
import com.ai.hackemotion.security.service.impl.JwtServiceImpl;
import com.ai.hackemotion.service.impl.AssetServiceImpl;
import com.ai.hackemotion.service.impl.EmotionServiceImpl;
import com.ai.hackemotion.service.impl.FinalAssetEmotionServiceImpl;
import com.ai.hackemotion.entity.User;
import com.ai.hackemotion.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("asset")
@RequiredArgsConstructor
public class AssetController {
    private final AssetServiceImpl assetServiceImpl;
    private final EmotionServiceImpl service;
    private final UserAssetEmotionRepository UAErepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final EmotionRepository emotionRepository;
    private final FinalAssetEmotionServiceImpl finalAssetEmotionServiceImpl;
    private final JwtServiceImpl jwtServiceImpl;

    @PostMapping("/{assetId}/add-emotions")
    public ResponseEntity<Asset> addEmotions(
            @PathVariable Long assetId,
            @RequestBody EmotionRequest request) {
        return ResponseEntity.ok(assetServiceImpl.addEmotionsToAsset(assetId, request));
    }

    @PostMapping("/create-asset")
    public ResponseEntity<Asset> createAsset(@RequestBody AssetRequest request) {
        return ResponseEntity.ok(assetServiceImpl.createAsset(request));
    }

    @GetMapping("/")
    public ResponseEntity<List<UserAssetEmotionRequest>> getAssets(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String cleanToken = token.replace("Bearer ", "");
        String username = jwtServiceImpl.extractUsername(token);

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(jwtServiceImpl.hasRole(cleanToken, "ADMIN")){
            return ResponseEntity.ok(new ArrayList<>(assetServiceImpl.getAllAssets()));
        } else {
            return ResponseEntity.ok(new ArrayList<>(assetServiceImpl.getAssetsByUsername(username)));
        }
    }

    @GetMapping("/rate")
    public ResponseEntity<List<AssetResponse>> getAssetsToRate(HttpServletRequest request) {
        String username = jwtServiceImpl.extractUsername(request.getHeader("Authorization"));
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
    @PostMapping("/rate")
    public ResponseEntity<HttpStatus> voteForEmotion(HttpServletRequest request, @RequestBody VoteRequest voteRequest) {
        String username = jwtServiceImpl.extractUsername(request.getHeader("Authorization"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long assetId = voteRequest.getAssetId();

        // Check if user has already rated this asset
        boolean alreadyRated = UAErepository.existsByUserIdAndAssetId(user.getId(), assetId);
        if (alreadyRated) {
            return ResponseEntity.badRequest().body(HttpStatus.CONFLICT);
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
        finalAssetEmotionServiceImpl.updateFinalEmotion(assetId);

        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }





}
