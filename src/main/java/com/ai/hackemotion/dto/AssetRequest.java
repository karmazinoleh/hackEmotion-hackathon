package com.ai.hackemotion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssetRequest {
    private String url;
    private String name;
    private String username;
}