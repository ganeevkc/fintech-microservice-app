package com.finverse.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TokenResponse {
    private String token;

    public TokenResponse() {}

    public TokenResponse(String token) {
        this.token = token;
    }

}
