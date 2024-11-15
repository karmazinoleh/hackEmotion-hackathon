package com.ai.hackemotion.emotion;

import com.ai.hackemotion.asset.Asset;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "emotions")
public class Emotion {
    @Id
    private String id;

    private String name;

    @DocumentReference(lazy = true)
    @JsonIgnore
    @Builder.Default
    private Set<Asset> assets = new HashSet<>();
}