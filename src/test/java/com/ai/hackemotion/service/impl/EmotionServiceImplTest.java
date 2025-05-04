package com.ai.hackemotion.service.impl;

import com.ai.hackemotion.repository.EmotionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

class EmotionServiceImplTest {

    @Mock
    private EmotionRepository emotionRepository;

    @InjectMocks
    private EmotionServiceImpl emotionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addEmotion_SavesEmotionToRepository() {
        // Arrange
        String emotionName = "Joy";

        // Act
        emotionService.addEmotion(emotionName);

        // Assert
        verify(emotionRepository, times(1)).save(argThat(emotion ->
                emotion.getName().equals(emotionName)
        ));
    }
}
