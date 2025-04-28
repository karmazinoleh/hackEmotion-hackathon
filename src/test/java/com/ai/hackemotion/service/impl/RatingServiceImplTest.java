package com.ai.hackemotion.service.impl;

import com.ai.hackemotion.entity.Rating;
import com.ai.hackemotion.entity.User;
import com.ai.hackemotion.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RatingServiceImplTest {

    @InjectMocks
    private RatingServiceImpl ratingService;

    @Mock
    private RatingRepository ratingRepository;

    private User user;
    private Rating existingRating;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        existingRating = Rating.builder()
                .user(user)
                .score(10)
                .rank(1)
                .build();
    }

    @Test
    void updateScore_ShouldCreateNewRating_WhenRatingDoesNotExist() {
        // given
        when(ratingRepository.findByUser(user)).thenReturn(Optional.empty());
        when(ratingRepository.findAllByOrderByScoreDesc()).thenReturn(List.of());

        // when
        ratingService.updateScore(user, 5);

        // then
        verify(ratingRepository, atLeastOnce()).save(any(Rating.class));
    }

    @Test
    void updateScore_ShouldUpdateExistingRating_WhenRatingExists() {
        // given
        when(ratingRepository.findByUser(user)).thenReturn(Optional.of(existingRating));
        when(ratingRepository.findAllByOrderByScoreDesc()).thenReturn(List.of(existingRating));

        // when
        ratingService.updateScore(user, 5);

        // then
        assertEquals(15, existingRating.getScore());
        verify(ratingRepository, atLeast(2)).save(existingRating);
    }

    @Test
    void updateScore_ShouldCorrectlyReorderRanks() {
        // given
        User anotherUser = User.builder()
                .id(2L)
                .username("anotheruser")
                .build();

        Rating rating1 = Rating.builder().user(user).score(30).build();
        Rating rating2 = Rating.builder().user(anotherUser).score(20).build();

        when(ratingRepository.findByUser(user)).thenReturn(Optional.of(rating1));
        when(ratingRepository.findAllByOrderByScoreDesc()).thenReturn(List.of(rating1, rating2));

        // when
        ratingService.updateScore(user, 5); // user 30+5=35

        // then
        verify(ratingRepository, atLeast(3)).save(any(Rating.class)); // rating1 updated, both reordered
    }
}
