package com.ai.hackemotion.emotion;

public class EmotionRequest {
    private String emotionName;

    // Default constructor needed for JSON deserialization
    public EmotionRequest() {}

    public String getEmotionName() {
        return emotionName;
    }

    public void setEmotionName(String emotionName) {
        this.emotionName = emotionName;
    }
}
