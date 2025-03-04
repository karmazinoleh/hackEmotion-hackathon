package com.ai.hackemotion.emotion;

import com.ai.hackemotion.asset.Asset;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "final_asset_emotion")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinalAssetEmotion {

    @Id
    private Long assetId;

    @OneToOne
    @JoinColumn(name = "asset_id", referencedColumnName = "id")
    @MapsId
    private Asset asset;

    @ManyToOne
    @JoinColumn(name = "emotion_id", nullable = false)
    private Emotion emotion;
}
