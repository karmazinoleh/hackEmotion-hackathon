package com.ai.hackemotion.dto.request;

import com.ai.hackemotion.entity.Emotion;
import com.ai.hackemotion.entity.User;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmotionRequest {
    private List<Emotion> emotions;
    private User user;
}

