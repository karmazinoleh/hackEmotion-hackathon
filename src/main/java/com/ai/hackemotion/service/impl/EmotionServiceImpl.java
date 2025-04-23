package com.ai.hackemotion.service.impl;

import com.ai.hackemotion.entity.Emotion;
import com.ai.hackemotion.repository.EmotionRepository;
import com.ai.hackemotion.service.EmotionService;
import org.springframework.stereotype.Service;

@Service
public class EmotionServiceImpl implements EmotionService {
    private final EmotionRepository emotionRepository;

    public EmotionServiceImpl(EmotionRepository emotionRepository) {
        this.emotionRepository = emotionRepository;
    }

    public void addEmotion(String name) {
        Emotion emotion = Emotion.builder()
                .name(name)
                .build();
        emotionRepository.save(emotion);
    }
}