package com.ai.hackemotion.emotion;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("emotion")
public class EmotionController {
    private final EmotionService emotionService;

    public EmotionController(EmotionService emotionService) {
        this.emotionService = emotionService;
    }

    @PostMapping("add")
    public String addEmotion(@RequestBody EmotionRequest request) {
        emotionService.addEmotion(request.getEmotionName());
        return "Emotion was created!";
    }
}