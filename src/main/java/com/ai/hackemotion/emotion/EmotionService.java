package com.ai.hackemotion.emotion;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class EmotionService {

    private final EmotionRepository emotionRepository;

    public EmotionService(EmotionRepository emotionRepository) {
        this.emotionRepository = emotionRepository;
    }

    @Transactional
    public Emotion addEmotion(String name) {
        return emotionRepository.save(Emotion.builder()
                .name(name)
                .build());
    }

    @Transactional
    public void initializeBasicEmotions() {
        String[] basicEmotions = {"HAPPINESS", "SADNESS", "FEAR", "ANGER", "REVULSION", "SURPRISE"};
        for (String emotion : basicEmotions) {
            if (emotionRepository.findByName(emotion).isEmpty()) {
                addEmotion(emotion);
            }
        }
    }
}
