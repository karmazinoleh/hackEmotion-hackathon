package com.ai.hackemotion.asset;

import lombok.Data;

@Data
public class EmotionWithIntensityRequest {
    private String emotionName;
    private double intensity;
}
