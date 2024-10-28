package com.seith_amigoscode.auth;

public record AuthenticationRequest (
        String username,
        String password
) {
}
