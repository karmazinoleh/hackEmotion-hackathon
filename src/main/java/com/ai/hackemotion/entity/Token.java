package com.ai.hackemotion.entity;

import com.ai.hackemotion.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue
    private Long id;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime validatedAt;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType = TokenType.ACCESS;

    private boolean expired;
    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}