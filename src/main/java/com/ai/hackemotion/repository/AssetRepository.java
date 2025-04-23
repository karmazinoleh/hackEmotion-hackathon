package com.ai.hackemotion.repository;

import com.ai.hackemotion.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByName (String name);

    List<Asset> findByUserId (Long userId);

    List<Asset> findAllByUserIdNot (Long userId);
}
