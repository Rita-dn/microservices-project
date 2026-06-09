package com.skymat.ecommerce.customer;

public record CustomerResponse(
        String id,
        String firstName,
        String lastName,
        String email //we want to send an email using the notification ms
) {
}
