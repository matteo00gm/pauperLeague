package com.mtgleague.dto.auth.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {
    private String token;
    private Long userId;
    private long expiresIn;
}
