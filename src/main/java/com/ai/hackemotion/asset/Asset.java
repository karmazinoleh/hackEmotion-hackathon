package com.ai.hackemotion.asset;

import com.ai.hackemotion.emotion.Emotion;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "assets")
public class Asset {
    @Id
    private String id;

    private String url;

    private String name;

    @Builder.Default
    private Set<EmotionAssignment> emotions = new HashSet<>();

    private LocalDateTime localDateTime;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private Long authorId;
}