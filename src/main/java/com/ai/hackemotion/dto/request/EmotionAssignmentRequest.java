package com.ai.hackemotion.dto.request;

import com.ai.hackemotion.entity.Emotion;
import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmotionAssignmentRequest {
    @Id
    private String id;

    private Emotion emotion;

    private double intensity; // 0 ->- 1

    public EmotionAssignmentRequest(Emotion emotion, double intensity) {
        this.emotion = emotion;
        this.intensity = intensity;
    }

}
