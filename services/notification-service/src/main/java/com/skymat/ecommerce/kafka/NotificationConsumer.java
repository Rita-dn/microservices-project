package com.skymat.ecommerce.kafka;

import com.skymat.ecommerce.email.EmailService;
import com.skymat.ecommerce.kafka.order.OrderConfirmation;
import com.skymat.ecommerce.kafka.payment.PaymentConfirmation;
import com.skymat.ecommerce.notification.Notification;
import com.skymat.ecommerce.notification.NotificationRepository;
import com.skymat.ecommerce.notification.NotificationType;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    // we will create two methods, one for consuming the payment notifications,
// the second is for consuming order confirmation notification
    // in both we need first to persist the notification, and then send the email
    private final NotificationRepository repo;
    private final EmailService emailService;

    @KafkaListener(topics = "payment-topic")
    //The attribute names for both PaymentConfirmation and PaymentConfirmationRequest which is in the Payment service should be the same
    public void consumePaymentSuccessNotification(PaymentConfirmation paymentConfirmation) throws MessagingException {
        log.info("Consuming the msg from payment-topic Topic:: {}", paymentConfirmation);

        //save notification payment
        repo.save(Notification.builder()
                .type(NotificationType.PAYMENT_CONFIRMATION)
                .notificationDate(LocalDateTime.now())
                .paymentConfirmation(paymentConfirmation)
                .build()
        );
        // send email
        var customerName = paymentConfirmation.customerFirstname() + " " + paymentConfirmation.customerLastname();
        emailService.sendPaymentSuccessEmail(
                paymentConfirmation.customerEmail(),
                customerName,
                paymentConfirmation.amount(),
                paymentConfirmation.orderReference()

        );
    }


    @KafkaListener(topics = "order-topic")
    //The attribute names for both PaymentConfirmation and PaymentConfirmationRequest which is in the Payment service should be the same
    public void consumeOrderConfirmationSuccessNotification(OrderConfirmation orderConfirmation) throws MessagingException {
        log.info("Consuming the msg from order-topic Topic:: {}", orderConfirmation);

        //save notification payment
        repo.save(Notification.builder()
                .type(NotificationType.ORDER_CONFIRMATION)
                .notificationDate(LocalDateTime.now())
                .orderConfirmation(orderConfirmation)
                .build()
        );
        // send email
        var customerName = orderConfirmation.customer().firstname() + " " + orderConfirmation.customer().lastname();
        emailService.sendOrderConfirmationEmail(
                orderConfirmation.customer().email(),
                customerName,
                orderConfirmation.totalAmount(),
                orderConfirmation.orderReference(),
                orderConfirmation.products()
        );
    }
}
