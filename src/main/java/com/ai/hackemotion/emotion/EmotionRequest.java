package com.ai.hackemotion.emotion;

import com.ai.hackemotion.user.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class EmotionRequest {
    private List<Emotion> emotions;
    private User user;
}

