package com.ai.hackemotion.emotion;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface EmotionRepository extends MongoRepository<Emotion, String> {
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    Optional<Emotion> findByName(String name);
    Optional<Emotion> findByNameIgnoreCase(String name);
}