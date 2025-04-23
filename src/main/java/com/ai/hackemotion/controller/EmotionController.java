package com.ai.hackemotion.controller;

import com.ai.hackemotion.dto.EmotionRequest;
import com.ai.hackemotion.service.EmotionService;
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
        //emotionService.addEmotion(request.getEmotions()[0]);
        return "Emotion was created!";
    }
}