package com.ai.hackemotion.emotion;

import org.springframework.stereotype.Service;

@Service
public class EmotionService {

    public Emotion addEmotion(String name) {

        Emotion emotion = Emotion.builder()
                .name(name)
                .build();

        return emotion;
    }
}
