package com.ai.hackemotion.asset;

import com.ai.hackemotion.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "asset")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    private String name;

    private LocalDateTime localDateTime; // do not use

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}