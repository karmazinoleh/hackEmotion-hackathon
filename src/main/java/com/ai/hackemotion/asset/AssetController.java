package com.ai.hackemotion.asset;

import com.ai.hackemotion.emotion.Emotion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("asset")
public class AssetController {

    private final Emotion emotion;

    public AssetController(Emotion emotion) {
        this.emotion = emotion;
    }
}
