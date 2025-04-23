package com.ai.hackemotion.controller;

import com.ai.hackemotion.dto.request.AssetRequest;
import com.ai.hackemotion.dto.request.EmotionRequest;
import com.ai.hackemotion.dto.request.VoteRequest;
import com.ai.hackemotion.entity.Asset;
import com.ai.hackemotion.entity.Emotion;
import com.ai.hackemotion.entity.User;
import com.ai.hackemotion.entity.UserAssetEmotion;
import com.ai.hackemotion.repository.*;
import com.ai.hackemotion.service.impl.AssetServiceImpl;
import com.ai.hackemotion.service.impl.EmotionServiceImpl;
import com.ai.hackemotion.service.impl.FinalAssetEmotionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.*;

class AssetControllerTest {

    @Mock private AssetServiceImpl assetService;
    @Mock private EmotionServiceImpl emotionService;
    @Mock private FinalAssetEmotionServiceImpl finalAssetEmotionService;
    @Mock private UserAssetEmotionRepository UAErepository;
    @Mock private UserRepository userRepository;
    @Mock private AssetRepository assetRepository;
    @Mock private EmotionRepository emotionRepository;

    @InjectMocks
    private AssetController assetController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAsset() {
        AssetRequest request = new AssetRequest();
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setName("Image1");

        when(assetService.createAsset(request)).thenReturn(asset);

        ResponseEntity<Asset> response = assetController.createAsset(request);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getName()).isEqualTo("Image1");
    }

    @Test
    void testAddEmotions() {
        Long assetId = 1L;
        EmotionRequest request = new EmotionRequest();

        Asset asset = new Asset();
        asset.setId(assetId);
        asset.setName("Image1");

        when(assetService.addEmotionsToAsset(assetId, request)).thenReturn(asset);

        ResponseEntity<Asset> response = assetController.addEmotions(assetId, request);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(assetId);
    }

    @Test
    void testVoteForEmotionSuccess() {
        String username = "testuser";
        VoteRequest voteRequest = new VoteRequest();
        voteRequest.setAssetId(1L);
        voteRequest.setEmotionId(2L);

        User user = new User();
        user.setId(10L);
        user.setUsername(username);

        Asset asset = new Asset();
        asset.setId(1L);

        Emotion emotion = new Emotion();
        emotion.setId(2L);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(UAErepository.existsByUserIdAndAssetId(10L, 1L)).thenReturn(false);
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(emotionRepository.findById(2L)).thenReturn(Optional.of(emotion));

        ResponseEntity<String> response = assetController.voteForEmotion(username, voteRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Vote recorded");

        verify(UAErepository).save(any(UserAssetEmotion.class));
        verify(finalAssetEmotionService).updateFinalEmotion(1L);
    }

    @Test
    void testVoteForEmotionAlreadyRated() {
        String username = "testuser";
        VoteRequest voteRequest = new VoteRequest();
        voteRequest.setAssetId(1L);

        User user = new User();
        user.setId(10L);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(UAErepository.existsByUserIdAndAssetId(10L, 1L)).thenReturn(true);

        ResponseEntity<String> response = assetController.voteForEmotion(username, voteRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("You have already rated this asset");
    }
}
