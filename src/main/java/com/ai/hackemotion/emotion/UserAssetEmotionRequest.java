package com.ai.hackemotion.emotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAssetEmotionRequest {
    private Long id;
    private String name;
    private List<String> emotionNames;

}
