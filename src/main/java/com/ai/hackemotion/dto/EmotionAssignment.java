package com.ai.hackemotion.dto;

import com.ai.hackemotion.entity.Emotion;
import lombok.*;
import org.springframework.data.annotation.Id;

@Data
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
