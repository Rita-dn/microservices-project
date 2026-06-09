package com.skymat.ecommerce.product;

import com.skymat.ecommerce.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductClient {
    @Value("${application.config.product-url}")
    private String productUrl;
    private final RestTemplate restTemplate;


    public List<PurchaseResponse> purchaseProducts(List<PurchaseRequest> requestBody) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);//"application/json"

        HttpEntity<List<PurchaseRequest>> requestEntity = new HttpEntity<>(requestBody, httpHeaders); // the type we are going to pass

        ParameterizedTypeReference<List<PurchaseResponse>> responseType = new ParameterizedTypeReference<>() {
        }; //The return type

        ResponseEntity<List<PurchaseResponse>> responseEntity = restTemplate.exchange(productUrl + "/purchase", HttpMethod.POST, requestEntity, responseType);

        if (responseEntity.getStatusCode().isError()) { //of type 5xx or 4xx
            throw new BusinessException("An error occurred while processing the products' purchase: " + responseEntity.getStatusCode());
        }
        return responseEntity.getBody();
    }

}
