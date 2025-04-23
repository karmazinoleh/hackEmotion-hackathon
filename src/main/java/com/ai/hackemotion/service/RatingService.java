package com.ai.hackemotion.service;

import com.ai.hackemotion.entity.Rating;
import com.ai.hackemotion.entity.User;

public interface RatingService {
    void updateScore(User user, int pointsToAdd);
}
