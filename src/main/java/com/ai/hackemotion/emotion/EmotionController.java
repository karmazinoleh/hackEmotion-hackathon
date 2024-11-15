package com.ai.hackemotion.emotion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("emotion")
public class EmotionController {

    private final EmotionService emotionService;
    private final EmotionRepository emotionRepository;

    public EmotionController(EmotionService emotionService, EmotionRepository emotionRepository) {
        this.emotionService = emotionService;
        this.emotionRepository = emotionRepository;
    }

    @PostMapping("add")
    public String addEmotion(@RequestBody EmotionRequest request) {
        emotionService.addEmotion(request.getEmotionName());
        return "Emotion was created!";
    }


}
