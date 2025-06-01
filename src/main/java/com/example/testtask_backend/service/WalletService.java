package com.example.testtask_backend.service;

import com.example.testtask_backend.dto.WalletOperationRequest;
import com.example.testtask_backend.exception.InsufficientFundsException;
import com.example.testtask_backend.exception.WalletNotFoundException;
import com.example.testtask_backend.model.OperationType;
import com.example.testtask_backend.model.Wallet;
import com.example.testtask_backend.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional
    public void processOperation(WalletOperationRequest request) {
        Wallet wallet = walletRepository.findByIdForUpdate(request.getValletId())
                .orElseThrow(() -> new WalletNotFoundException(request.getValletId()));

        Long currentBalance = wallet.getBalance();
        Long amount = request.getAmount();

        if (request.getOperationType() == OperationType.DEPOSIT) {
            wallet.setBalance(currentBalance + amount);
        } else if (request.getOperationType() == OperationType.WITHDRAW) {
            if (currentBalance < amount) {
                throw new InsufficientFundsException(request.getValletId());
            }
            wallet.setBalance(currentBalance - amount);
        }

        walletRepository.save(wallet);
    }

    public Long getBalance(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
        return wallet.getBalance();
    }
}
