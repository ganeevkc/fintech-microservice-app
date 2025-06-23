package com.finverse.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TokenResponse {
    private String token;
    private String userId;

    public TokenResponse() {}

    public TokenResponse(String token) {
        this.token = token;
    }

    public TokenResponse(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }
}