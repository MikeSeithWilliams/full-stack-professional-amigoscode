package com.seith_amigoscode.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}
