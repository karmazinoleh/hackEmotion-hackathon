package com.ai.hackemotion.asset;

import com.ai.hackemotion.emotion.Emotion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "asset")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "asset_emotions",
            joinColumns = @JoinColumn(name = "asset_id"),
            inverseJoinColumns = @JoinColumn(name = "emotion_id")
    )
    @Builder.Default
    private Set<Emotion> emotions = new HashSet<>();

    public void addEmotion(Emotion emotion) {
        emotions.add(emotion);
        emotion.getAssets().add(this);
    }

    public void removeEmotion(Emotion emotion) {
        emotions.remove(emotion);
        emotion.getAssets().remove(this);
    }

    @Column(nullable = false)
    private LocalDateTime localDateTime;

    @PrePersist
    protected void onCreate() {
        this.localDateTime = LocalDateTime.now();
    }

}
