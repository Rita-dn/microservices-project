package com.skymat.ecommerce.product;

import com.skymat.ecommerce.exception.ProductPurchaseException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repo;
    private final ProductMapper mapper;

    public Integer createProduct(@RequestBody @Valid ProductRequest request) {
        Product dbProduct = repo.save(mapper.toProduct(request));
        return dbProduct.getId();
    }

    public List<ProductPurchaseResponse> purchaseProduct(@Valid List<ProductPurchaseRequest> request) {
        var productIds = request.stream()
                .map(ProductPurchaseRequest::productId)
                .toList();

        var storedProducts = repo.findAllByIdInOrderById(productIds);
        if (productIds.size() != storedProducts.size()) {
            throw new ProductPurchaseException("One or more products does not exist");
        }

        var storedRequest = request.stream().sorted(Comparator.comparing(ProductPurchaseRequest::productId)).toList();

        var purchasedProducts = new ArrayList<ProductPurchaseResponse>();
        for (int i = 0; i < storedRequest.size(); i++) {
            var product = storedProducts.get(i);
            var productRequest = storedRequest.get(i);

            if (product.getAvailableQuantity() < productRequest.quantity()) {
                throw new ProductPurchaseException("Product " + productIds.get(i) + " is out of stock");
            }
            var newAvailableQuantity = product.getAvailableQuantity() - productRequest.quantity();
            product.setAvailableQuantity(newAvailableQuantity);
            repo.save(product);
            purchasedProducts.add(mapper.toProductPurchaseResponse(product, productRequest.quantity()));
        }

        return purchasedProducts;
    }

    public List<ProductResponse> findAll() {
        return repo.findAll()
                .stream()
                .map(mapper::toProductResponse)
                .toList();
    }

    public ProductResponse findById(Integer productId) {
        return repo.findById(productId)
                .map(mapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
    }
}
