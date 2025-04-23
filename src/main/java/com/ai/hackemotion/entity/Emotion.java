package com.ai.hackemotion.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "emotion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Emotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}