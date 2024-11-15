package com.ai.hackemotion.emotion;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    private String name;

    /* Basic emotion, pre-created
    HAPPINESS,
    SADNESS,
    FEAR,
    ANGER,
    REVULSION,
    SURPRISE */

}
