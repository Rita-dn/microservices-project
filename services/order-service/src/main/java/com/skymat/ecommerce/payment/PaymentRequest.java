package com.skymat.ecommerce.payment;

import com.skymat.ecommerce.customer.CustomerResponse;
import com.skymat.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer) {

}
