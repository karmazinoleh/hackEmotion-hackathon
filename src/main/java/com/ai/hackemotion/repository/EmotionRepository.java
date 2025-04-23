package com.ai.hackemotion.repository;

import com.ai.hackemotion.entity.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {

    public Optional<Emotion> findByName(String name);

}