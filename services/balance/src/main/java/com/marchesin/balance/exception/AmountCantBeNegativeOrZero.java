package com.marchesin.balance.exception;

public class AmountCantBeNegativeOrZero extends RuntimeException {
    public AmountCantBeNegativeOrZero(String message) {
        super(message);
    }
}
