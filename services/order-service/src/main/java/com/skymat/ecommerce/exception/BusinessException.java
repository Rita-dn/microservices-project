package com.skymat.ecommerce.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data // to have the constructor and so on
public class BusinessException extends RuntimeException {
    private final String msg;
}
