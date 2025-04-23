package com.ai.hackemotion.controller;

import com.ai.hackemotion.dto.request.EmotionRequest;
import com.ai.hackemotion.service.impl.EmotionServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("emotion")
public class EmotionController {
    private final EmotionServiceImpl emotionServiceImpl;

    public EmotionController(EmotionServiceImpl emotionServiceImpl) {
        this.emotionServiceImpl = emotionServiceImpl;
    }

    @PostMapping("add")
    public String addEmotion(@RequestBody EmotionRequest request) {
        //emotionService.addEmotion(request.getEmotions()[0]);
        return "Emotion was created!";
    }
}