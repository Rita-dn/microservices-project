package com.skymat.ecommerce.customer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(
        name="customer-service", //name of the client
        url="${application.config.customer-url}" //fetch the property from properties file
)
public interface CustomerClient {

    @GetMapping("/{customer-id}") //this end point is in the Customer ms
    Optional<CustomerResponse> findCustomerById(@PathVariable("customer-id") String customerId);
}
