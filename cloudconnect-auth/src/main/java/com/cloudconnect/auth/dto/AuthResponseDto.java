package com.cloudconnect.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    private String tokenType;

    // Default constructor setting tokenType to "Bearer"
    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }
}
