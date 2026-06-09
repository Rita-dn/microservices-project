package com.skymat.ecommerce.kafka;

import com.skymat.ecommerce.customer.CustomerResponse;
import com.skymat.ecommerce.order.PaymentMethod;
import com.skymat.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

//contains the data that we need to send to the kafka broker
public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products //The list of products that the user purchased
) {
}
