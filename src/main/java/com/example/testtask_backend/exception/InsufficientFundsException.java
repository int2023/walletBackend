package com.example.testtask_backend.exception;

import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(UUID id) {
        super("Insufficient funds for wallet: " + id);
    }
}
