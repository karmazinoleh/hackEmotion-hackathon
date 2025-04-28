package com.ai.hackemotion.service.impl;

import com.ai.hackemotion.dto.request.AssetRequest;
import com.ai.hackemotion.dto.request.EmotionRequest;
import com.ai.hackemotion.dto.request.UserAssetEmotionRequest;
import com.ai.hackemotion.entity.Asset;
import com.ai.hackemotion.entity.Emotion;
import com.ai.hackemotion.entity.User;
import com.ai.hackemotion.entity.UserAssetEmotion;
import com.ai.hackemotion.repository.AssetRepository;
import com.ai.hackemotion.repository.EmotionRepository;
import com.ai.hackemotion.repository.UserAssetEmotionRepository;
import com.ai.hackemotion.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssetServiceImplTest {

    @Mock
    private AssetRepository assetRepository;
    @Mock
    private EmotionRepository emotionRepository;
    @Mock
    private UserAssetEmotionRepository userAssetEmotionRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AssetServiceImpl assetService;

    private User user;
    private Asset asset;
    private Emotion emotion;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        asset = Asset.builder()
                .id(1L)
                .name("Test Asset")
                .url("http://example.com")
                .user(user)
                .build();

        emotion = Emotion.builder()
                .id(1L)
                .name("Happy")
                .build();
    }

    @Test
    void testCreateAsset_success() {
        AssetRequest request = new AssetRequest();
        request.setName("Test Asset");
        request.setUrl("http://example.com");
        request.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(assetRepository.save(any(Asset.class))).thenReturn(asset);

        Asset createdAsset = assetService.createAsset(request);

        assertNotNull(createdAsset);
        assertEquals("Test Asset", createdAsset.getName());
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    void testCreateAsset_userNotFound() {
        AssetRequest request = new AssetRequest();
        request.setUsername("unknownUser");

        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> assetService.createAsset(request));
    }

    @Test
    void testAddEmotionsToAsset_success() {
        EmotionRequest request = new EmotionRequest();
        request.setUser(user);
        request.setEmotions(List.of(emotion));

        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(emotionRepository.findById(1L)).thenReturn(Optional.of(emotion));

        Asset result = assetService.addEmotionsToAsset(1L, request);

        assertNotNull(result);
        verify(userAssetEmotionRepository, times(1)).save(any(UserAssetEmotion.class));
    }

    @Test
    void testAddEmotionsToAsset_assetNotFound() {
        when(assetRepository.findById(1L)).thenReturn(Optional.empty());

        EmotionRequest request = new EmotionRequest();
        request.setUser(user);
        request.setEmotions(List.of(emotion));

        assertThrows(RuntimeException.class, () -> assetService.addEmotionsToAsset(1L, request));
    }

    @Test
    void testAddEmotionsToAsset_userNotFound() {
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        EmotionRequest request = new EmotionRequest();
        request.setUser(user);
        request.setEmotions(List.of(emotion));

        assertThrows(EntityNotFoundException.class, () -> assetService.addEmotionsToAsset(1L, request));
    }

    @Test
    void testGetAssetsByUsername_success() {
        UserAssetEmotion userAssetEmotion = UserAssetEmotion.builder()
                .asset(asset)
                .emotion(emotion)
                .user(user)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userAssetEmotionRepository.findAllByUserId(1L)).thenReturn(List.of(userAssetEmotion));

        List<UserAssetEmotionRequest> result = assetService.getAssetsByUsername("testuser");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Asset", result.get(0).getName());
    }

    @Test
    void testGetAssetsByUsername_userNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> assetService.getAssetsByUsername("unknownUser"));
    }

    @Test
    void testGetAllAssets_success() {
        UserAssetEmotion userAssetEmotion = UserAssetEmotion.builder()
                .asset(asset)
                .emotion(emotion)
                .user(user)
                .build();

        when(userAssetEmotionRepository.findAll()).thenReturn(List.of(userAssetEmotion));

        List<UserAssetEmotionRequest> result = assetService.getAllAssets();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Asset", result.get(0).getName());
    }

    @Test
    void testIsAssetAvailableForUser_true() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(assetRepository.findByUserId(1L)).thenReturn(List.of(asset));

        boolean available = assetService.isAssetAvailableForUser("Test Asset", "testuser");

        assertTrue(available);
    }

    @Test
    void testIsAssetAvailableForUser_false() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(assetRepository.findByUserId(1L)).thenReturn(List.of());

        boolean available = assetService.isAssetAvailableForUser("Nonexistent Asset", "testuser");

        assertFalse(available);
    }
}
