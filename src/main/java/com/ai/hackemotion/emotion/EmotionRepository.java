package com.ai.hackemotion.emotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {

    public Optional<Emotion> findByName(String name);

}