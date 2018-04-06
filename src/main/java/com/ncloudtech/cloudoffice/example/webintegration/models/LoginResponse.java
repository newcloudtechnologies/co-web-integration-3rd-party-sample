package com.ncloudtech.cloudoffice.example.webintegration.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String coApiUrl;
}

