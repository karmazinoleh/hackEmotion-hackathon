package com.ai.hackemotion.rating;

import com.ai.hackemotion.user.User;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    public User updateScore(User user, int pointToAdd){
        int updatedScore = user.getScore() + pointToAdd;
        user.setScore(updatedScore);
        return user;
    }
}
