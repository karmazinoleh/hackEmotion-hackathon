package com.ai.hackemotion.asset;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AssetRepository extends MongoRepository<Asset, String> {
    Optional<Asset> findByName(String name);
}
