package com.ai.hackemotion.emotion;

import com.ai.hackemotion.user.User;
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

