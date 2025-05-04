package com.ai.hackemotion.service.impl;

import com.ai.hackemotion.entity.Asset;
import com.ai.hackemotion.entity.Emotion;
import com.ai.hackemotion.entity.FinalAssetEmotion;
import com.ai.hackemotion.entity.UserAssetEmotion;
import com.ai.hackemotion.repository.AssetRepository;
import com.ai.hackemotion.repository.FinalAssetEmotionRepository;
import com.ai.hackemotion.repository.UserAssetEmotionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinalAssetEmotionServiceImplTest {

    @Mock
    private FinalAssetEmotionRepository finalAssetEmotionRepository;

    @Mock
    private UserAssetEmotionRepository userAssetEmotionRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private FinalAssetEmotionServiceImpl finalAssetEmotionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateFinalEmotion_AssetExists_UpdatesFinalEmotion() {
        // Arrange
        Long assetId = 1L;
        Asset asset = new Asset();
        asset.setId(assetId);

        Emotion happy = new Emotion();
        Emotion sad = new Emotion();

        UserAssetEmotion userEmotion1 = new UserAssetEmotion();
        userEmotion1.setEmotion(happy);

        UserAssetEmotion userEmotion2 = new UserAssetEmotion();
        userEmotion2.setEmotion(happy);

        UserAssetEmotion userEmotion3 = new UserAssetEmotion();
        userEmotion3.setEmotion(sad);

        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));
        when(userAssetEmotionRepository.findAllByAssetId(assetId))
                .thenReturn(List.of(userEmotion1, userEmotion2, userEmotion3));
        when(finalAssetEmotionRepository.findByAssetId(assetId)).thenReturn(Optional.empty());

        // Act
        finalAssetEmotionService.updateFinalEmotion(assetId);

        // Assert
        verify(finalAssetEmotionRepository, times(1)).save(any(FinalAssetEmotion.class));
    }

    @Test
    void updateFinalEmotion_AssetNotFound_ThrowsException() {
        // Arrange
        when(assetRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> finalAssetEmotionService.updateFinalEmotion(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Asset not found");
    }
}
