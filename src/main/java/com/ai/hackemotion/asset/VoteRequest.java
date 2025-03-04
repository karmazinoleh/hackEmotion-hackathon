package com.ai.hackemotion.asset;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VoteRequest {
    private Long assetId;
    private Long emotionId;
}
