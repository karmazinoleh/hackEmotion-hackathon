package com.ai.hackemotion.service;

import com.ai.hackemotion.dto.request.AssetRequest;
import com.ai.hackemotion.dto.request.EmotionRequest;
import com.ai.hackemotion.entity.Asset;
import com.ai.hackemotion.entity.Emotion;
import com.ai.hackemotion.entity.UserAssetEmotion;
import com.ai.hackemotion.repository.AssetRepository;
import com.ai.hackemotion.repository.EmotionRepository;
import com.ai.hackemotion.repository.UserAssetEmotionRepository;
import com.ai.hackemotion.service.impl.AssetServiceImpl;
import com.ai.hackemotion.entity.User;
import com.ai.hackemotion.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    private AssetServiceImpl assetServiceImpl;

    private User testUser;
    private Asset testAsset;
    private Emotion testEmotion;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testUser")
                .build();

        testAsset = Asset.builder()
                .id(1L)
                .name("Test Asset")
                .url("http://test.com/asset")
                .user(testUser)
                .build();

        testEmotion = Emotion.builder()
                .id(1L)
                .name("Happy")
                .build();
    }

    @Test
    void createAsset_Success() {
        AssetRequest request = new AssetRequest("http://test.com/asset","Test Asset", "testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(assetRepository.save(any(Asset.class))).thenReturn(testAsset);

        Asset result = assetServiceImpl.createAsset(request);

        assertNotNull(result);
        assertEquals("Test Asset", result.getName());
        assertEquals("http://test.com/asset", result.getUrl());
        assertEquals(testUser, result.getUser());

        verify(userRepository).findByUsername("testUser");
        verify(assetRepository).save(any(Asset.class));

    }

    @Test
    void createAsset_UserNotFound(){
        AssetRequest request = new AssetRequest("http://test.com/asset","Test Asset", "nonExistentUser");

        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            assetServiceImpl.createAsset(request);
        });

        assertEquals("User not found: nonExistentUser", exception.getMessage());

        verify(userRepository).findByUsername("nonExistentUser");
        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void addEmotionsToAsset_Success() {
        Long assetId = 1L;
        Emotion emotion = Emotion.builder().id(1L).name("Happy").build();
        EmotionRequest emotionRequest = new EmotionRequest();
        emotionRequest.setEmotions(Arrays.asList(testEmotion));
        emotionRequest.setUser(testUser);

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(testAsset));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(emotionRepository.findById(emotion.getId())).thenReturn(Optional.of(emotion));
        when(userAssetEmotionRepository.save(any(UserAssetEmotion.class)))
                .thenReturn(UserAssetEmotion.builder()
                        .user(testUser)
                        .asset(testAsset)
                        .emotion(testEmotion)
                        .build()
                );

        Asset result = assetServiceImpl.addEmotionsToAsset(assetId, emotionRequest);

        assertNotNull(result);
        assertEquals(testAsset.getId(), result.getId());

        verify(assetRepository).findById(assetId);
        verify(userRepository).findByUsername(testUser.getUsername());
        verify(emotionRepository).findById(emotion.getId());
        verify(userAssetEmotionRepository).save(any(UserAssetEmotion.class));
    }

    // addEmotionsToAsset_UserNotFound
    @Test
    void addEmotionsToAsset_UserNotFound(){
        Long assetId = 1L;
        Emotion emotion = Emotion.builder().id(1L).name("Happy").build();
        EmotionRequest emotionRequest = new EmotionRequest();
        emotionRequest.setEmotions(Arrays.asList(testEmotion));
        User nonExistentUser = new User();
        nonExistentUser.setUsername("nonExistentUser");
        nonExistentUser.setId(1L);
        nonExistentUser.setFullName("nonExistentUser Fullname");
        nonExistentUser.setEmail("nonExistentUser@mail.com");
        emotionRequest.setUser(nonExistentUser);

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(testAsset));
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            assetServiceImpl.addEmotionsToAsset(assetId, emotionRequest);
        });

        assertTrue(exception.getMessage().contains("User not found"));

        verify(userRepository).findByUsername("nonExistentUser");
        verify(emotionRepository, never()).findById(emotion.getId());
        verify(assetRepository).findById(assetId);
        verify(userAssetEmotionRepository, never()).save(any(UserAssetEmotion.class));

    }

    // addEmotionsToAsset_AssetNotFound
    @Test
    void addEmotionsToAsset_AssetNotFound(){
        Long assetId = 999L;
        Emotion emotion = Emotion.builder().id(1L).name("Happy").build();
        EmotionRequest emotionRequest = new EmotionRequest();
        emotionRequest.setEmotions(Arrays.asList(testEmotion));

        when(assetRepository.findById(assetId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            assetServiceImpl.addEmotionsToAsset(assetId, emotionRequest);
        });

        assertEquals("Asset not found", exception.getMessage());

        verify(userRepository, never()).findByUsername("testUser");
        verify(emotionRepository, never()).findById(emotion.getId());
        verify(assetRepository).findById(assetId);
        verify(userAssetEmotionRepository, never()).save(any(UserAssetEmotion.class));

    }
    // addEmotionsToAsset_EmotionNotFound
    @Test
    void addEmotionsToAsset_EmotionNotFound(){
        Long assetId = 1L;
        Emotion nonExistentEmotion = Emotion.builder().id(999L).name("NonExistent").build();
        EmotionRequest emotionRequest = new EmotionRequest();
        emotionRequest.setUser(testUser);
        emotionRequest.setEmotions(Arrays.asList(nonExistentEmotion));

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(testAsset));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(emotionRepository.findById(nonExistentEmotion.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            assetServiceImpl.addEmotionsToAsset(assetId, emotionRequest);
        });

        assertTrue(exception.getMessage().contains("Emotion not found"));
        //assertEquals("Emotion not found: 999", exception.getMessage());

        verify(userRepository).findByUsername("testUser");
        verify(emotionRepository).findById(nonExistentEmotion.getId());
        verify(assetRepository).findById(assetId);
        verify(userAssetEmotionRepository, never()).save(any(UserAssetEmotion.class));

    }
}