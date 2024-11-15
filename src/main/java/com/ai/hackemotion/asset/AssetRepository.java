package com.ai.hackemotion.asset;


import com.ai.hackemotion.emotion.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findAllByEmotions(Emotion emotion);
}
