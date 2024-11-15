package com.ai.hackemotion.emotion;

import com.ai.hackemotion.asset.Asset;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "emotion")
public class Emotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToMany (mappedBy = "emotions", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private Set<Asset> assets = new HashSet<>();

    /* Basic emotion, pre-created
    HAPPINESS,
    SADNESS,
    FEAR,
    ANGER,
    REVULSION,
    SURPRISE */

}
