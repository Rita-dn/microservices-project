package com.skymat.ecommerce.order;

import com.skymat.ecommerce.customer.CustomerClient;
import com.skymat.ecommerce.exception.BusinessException;
import com.skymat.ecommerce.kafka.OrderConfirmation;
import com.skymat.ecommerce.kafka.OrderProducer;
import com.skymat.ecommerce.orderline.OrderLineRequest;
import com.skymat.ecommerce.orderline.OrderLineService;
import com.skymat.ecommerce.payment.PaymentClient;
import com.skymat.ecommerce.payment.PaymentRequest;
import com.skymat.ecommerce.product.ProductClient;
import com.skymat.ecommerce.product.PurchaseRequest;
import com.skymat.ecommerce.product.PurchaseResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerClient client;
    private final ProductClient productClient;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(OrderRequest request) {
        // check the customer-> using openFeign
        var customer = this.client.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID::" + request.customerId()));

        //purchase the products (using product ms)  (the call is going to be with RestTemplate)

        List<PurchaseResponse> purchasedProducts = this.productClient.purchaseProducts(request.products());

        //persist order

        var order = this.orderRepository.save(mapper.toOrder(request));

        //persist order lines
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    ));
        }

        //start payment process

        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        // send the order confirmation --> (using notification ms(kafka))
        // we need to define the serializers for both the producer and the consumer
        // Since we are sending an object, it should be serialized in order to be able to consume it
        //
        orderProducer.sendOrderConfirmation(new OrderConfirmation(
                request.reference(),
                request.amount(),
                request.paymentMethod(),
                customer,
                purchasedProducts
        ));

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return orderRepository
                .findAll()
                .stream()
                .map(mapper::fromOrder)
                .toList();
    }

    public OrderResponse findById(Integer orderId) {
        return orderRepository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order was found with the provided ID: %d", orderId)));
    }
}
