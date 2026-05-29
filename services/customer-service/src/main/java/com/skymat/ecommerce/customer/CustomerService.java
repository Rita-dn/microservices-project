package com.skymat.ecommerce.customer;

import com.skymat.ecommerce.exception.CustomerNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository repo;
    private final CustomerMapper mapper;

    public String createCustomer(@Valid CustomerRequest request) {
        var customer = repo.save(mapper.toCustomer(request));
        return customer.getId();
    }

    public void updateCustomer(@Valid CustomerRequest request) {
        var customer = repo.findById(request.id())
                .orElseThrow(() -> new CustomerNotFoundException(String.
                        format("Cannot update customer:: No customer was found with the provided ID %s ", request.id()))
                );
        mergeCustomer(customer, request); //to make sure that we do not overwrite an existing info
        repo.save(customer);
    }

    private void mergeCustomer(Customer customer, @Valid CustomerRequest request) {
        if (StringUtils.isNotBlank(request.firstName())) {
            customer.setFirstName(request.firstName());
        }

        if (StringUtils.isNotBlank(request.lastName())) {
            customer.setLastName(request.lastName());
        }

        if (StringUtils.isNotBlank(request.email())) {
            customer.setEmail(request.email());
        }

        if (request.address() != null) {
            customer.setAddress(request.address());
        }
    }

    public List<CustomerResponse> getAll() {
        return repo.findAll()
                .stream().map(mapper::toCustomerResponse)
                .toList();
    }

    public Boolean existsById(String customerId) {
        return repo.existsById(customerId);
    }

    public CustomerResponse getById(String customerId) {
        return repo.findById(customerId)
                .map(mapper::toCustomerResponse)
                .orElseThrow(() -> new CustomerNotFoundException("No customer was found with the provided ID " + customerId));
    }

    public void deleteById(String customerId) {
        repo.deleteById(customerId);
    }

}
