package com.ai.hackemotion.emotion;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface EmotionRepository extends MongoRepository<Emotion, String> {
    Optional<Emotion> findByName(String name);
}