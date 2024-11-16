package com.ai.hackemotion.emotion;

import lombok.Data;

@Data
public class EmotionRequest {
    private String emotionName;
    private Double intensity;
}

