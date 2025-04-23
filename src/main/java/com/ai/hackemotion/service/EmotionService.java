package com.ai.hackemotion.service;

import com.ai.hackemotion.entity.Emotion;
import com.ai.hackemotion.repository.EmotionRepository;
import org.springframework.stereotype.Service;

@Service
public class EmotionService {
    private final EmotionRepository emotionRepository;

    public EmotionService(EmotionRepository emotionRepository) {
        this.emotionRepository = emotionRepository;
    }

    public void addEmotion(String name) {
        Emotion emotion = Emotion.builder()
                .name(name)
                .build();
        emotionRepository.save(emotion);
    }
}