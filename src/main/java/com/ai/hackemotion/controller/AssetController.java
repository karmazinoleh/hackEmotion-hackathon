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
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("asset")
@RequiredArgsConstructor
public class AssetController {
    private final AssetServiceImpl assetServiceImpl;
    private final EmotionServiceImpl service;
    private final UserAssetEmotionRepository userAssetEmotionRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final EmotionRepository emotionRepository;
    private final FinalAssetEmotionServiceImpl finalAssetEmotionServiceImpl;
    private final JwtServiceImpl jwtServiceImpl;
    String authorizationHeader = "Authorization";

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
        String authHeader = request.getHeader(authorizationHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        String username = jwtServiceImpl.extractUsername(token);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(jwtServiceImpl.hasRole(token, "ADMIN")){
            return ResponseEntity.ok(new ArrayList<>(assetServiceImpl.getAllAssets()));
        } else {
            return ResponseEntity.ok(new ArrayList<>(assetServiceImpl.getAssetsByUsername(username)));
        }
    }

    @GetMapping("/rate")
    public ResponseEntity<List<AssetResponse>> getAssetsToRate(HttpServletRequest request) {
        String username = jwtServiceImpl.extractUsername(request.getHeader(authorizationHeader));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<AssetResponse> assetsToRate = assetServiceImpl.getAssetsToRate(user.getId());

        return ResponseEntity.ok(assetsToRate);
    }

    @PostMapping("/rate")
    public ResponseEntity<HttpStatus> voteForEmotion(HttpServletRequest request, @RequestBody VoteRequest voteRequest) {
        String username = jwtServiceImpl.extractUsername(request.getHeader(authorizationHeader));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long assetId = voteRequest.getAssetId();

        // Check if user has already rated this asset
        boolean alreadyRated = userAssetEmotionRepository
                .existsByUserIdAndAssetId(user.getId(), assetId);
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

        userAssetEmotionRepository.save(vote);
        finalAssetEmotionServiceImpl.updateFinalEmotion(assetId);

        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }





}
