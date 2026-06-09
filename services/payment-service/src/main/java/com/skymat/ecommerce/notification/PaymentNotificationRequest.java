package com.skymat.ecommerce.notification;

import com.skymat.ecommerce.payment.PaymentMethod;

import java.math.BigDecimal;

public record PaymentNotificationRequest(
        String orderReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String CustomerFirstName,
        String CustomerLastName,
        String CustomerEmail

) {
}
