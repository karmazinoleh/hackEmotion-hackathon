package com.ai.hackemotion.service.impl;

import com.ai.hackemotion.entity.Rating;
import com.ai.hackemotion.repository.RatingRepository;
import com.ai.hackemotion.entity.User;
import com.ai.hackemotion.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Transactional
    public void updateScore(User user, int pointsToAdd) {
        Rating currentRating = ratingRepository.findByUser(user) // Check if exist in Rating table
                .orElseGet(() -> createNewRating(user)); // if not create new one

        // Update score
        currentRating.setScore(currentRating.getScore() + pointsToAdd);
        ratingRepository.save(currentRating);

        // Update rank
        List<Rating> ratings = ratingRepository.findAllByOrderByScoreDesc();

        int newRank = binarySearchRank(ratings, currentRating.getScore());
        currentRating.setRank(newRank);
        ratingRepository.save(currentRating);

        reorderRanks(ratings);
    }

    private Rating createNewRating(User user) {
        return Rating.builder()
                .user(user)
                .score(0)
                .rank(0)
                .build();
    }

    private int binarySearchRank(List<Rating> ratings, int score) {
        int left = 0, right = ratings.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (ratings.get(mid).getScore() == score) {
                return mid + 1;
            }
            if (ratings.get(mid).getScore() < score) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return left + 1;
    }

    private void reorderRanks(List<Rating> ratings) {
        for (int i = 0; i < ratings.size(); i++) {
            ratings.get(i).setRank(i + 1);
            ratingRepository.save(ratings.get(i));
        }
    }
}
