package com.ai.hackemotion.emotion;

import com.ai.hackemotion.asset.Asset;
import com.ai.hackemotion.asset.AssetRepository;
import com.ai.hackemotion.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import javax.management.InstanceAlreadyExistsException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinalAssetEmotionServiceTest {

    @Mock
    private FinalAssetEmotionRepository finalAssetEmotionRepository;
    @Mock
    private UserAssetEmotionRepository userAssetEmotionRepository;
    @Mock
    private AssetRepository assetRepository;
    @InjectMocks
    private FinalAssetEmotionService finalAssetEmotionService;
    private Asset asset;
    private User user;
    private UserAssetEmotion userAssetEmotion;
    private Emotion emotion;

    private final String ENCODED_PASSWORD = "encodedPassword";

    @BeforeEach
    void setUp() {
        this.asset = new Asset();
        this.asset.setId(1L);
        this.asset.setName("test");
        this.asset.setUrl("asset-url");
        this.asset.setCreatedAt(LocalDateTime.now());

        this.user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword(ENCODED_PASSWORD);
        user.setEmail("email@email.com");

        this.emotion = new Emotion();
        this.emotion.setId(1L);
        this.emotion.setName("testEmotionName");

        this.userAssetEmotion = new UserAssetEmotion();
        this.userAssetEmotion.setId(1L);
        this.userAssetEmotion.setUser(user);
        this.userAssetEmotion.setAsset(asset);
        this.userAssetEmotion.setEmotion(emotion);
    }

    @Test
    void updateFinalEmotion_Success() {
        // Arrange
        when(assetRepository.findById(asset.getId())).thenReturn(Optional.of(asset));
        when(userAssetEmotionRepository.findAllByAssetId(asset.getId())).thenReturn(List.of(userAssetEmotion));
        when(finalAssetEmotionRepository.findByAssetId(asset.getId())).thenReturn(Optional.of(new FinalAssetEmotion()));

        ArgumentCaptor<FinalAssetEmotion> captor = ArgumentCaptor.forClass(FinalAssetEmotion.class);
        // Act
        finalAssetEmotionService.updateFinalEmotion(1L);

        // Assert
        verify(finalAssetEmotionRepository).save(captor.capture());

        FinalAssetEmotion savedEmotion = captor.getValue();
        assertEquals(asset, savedEmotion.getAsset());
        assertEquals(emotion, savedEmotion.getEmotion());
    }
}