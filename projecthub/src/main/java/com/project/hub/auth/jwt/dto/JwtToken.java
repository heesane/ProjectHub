package com.project.hub.auth.jwt.dto;

import lombok.Builder;

@Builder
public record JwtToken(
    String accessToken,
    String refreshToken
) {

}