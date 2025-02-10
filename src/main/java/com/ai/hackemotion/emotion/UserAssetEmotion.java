package com.ai.hackemotion.emotion;

import com.ai.hackemotion.asset.Asset;
import com.ai.hackemotion.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_asset_emotions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "asset_id", "emotion_id"})
})
public class UserAssetEmotion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @ManyToOne
    @JoinColumn(name = "emotion_id", nullable = false)
    private Emotion emotion;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
