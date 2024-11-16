package com.ai.hackemotion.asset;

import com.ai.hackemotion.emotion.Emotion;
import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmotionAssignment {
    @Id
    private String id;

    private Emotion emotion;

    private double intensity; // 0 ->- 1

    public EmotionAssignment(Emotion emotion, double intensity) {
        this.emotion = emotion;
        this.intensity = intensity;
    }

}
