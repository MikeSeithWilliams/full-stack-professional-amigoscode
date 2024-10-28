package com.seith_amigoscode.auth;

import com.seith_amigoscode.customer.CustomerDTO;

public record AuthenticationResponse (
        String token,
        CustomerDTO customerDTO
) {
}
