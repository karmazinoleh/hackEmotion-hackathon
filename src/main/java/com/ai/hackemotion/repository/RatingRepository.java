package com.ai.hackemotion.repository;

import com.ai.hackemotion.entity.Rating;
import com.ai.hackemotion.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByUser(User user);

    List<Rating> findAllByOrderByScoreDesc();
}
