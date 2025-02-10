package com.ai.hackemotion.asset;

import com.ai.hackemotion.user.User;
import lombok.Data;

@Data
public class AssetRequest {
    private String url;
    private String name;
    private String username;
}