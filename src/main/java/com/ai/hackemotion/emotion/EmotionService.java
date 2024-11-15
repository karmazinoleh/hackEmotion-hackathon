package com.ai.hackemotion.emotion;

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