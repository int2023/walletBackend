package com.example.testtask_backend.controller.load;

import com.example.testtask_backend.dto.WalletOperationRequest;
import com.example.testtask_backend.model.OperationType;
import com.example.testtask_backend.model.Wallet;
import com.example.testtask_backend.repository.WalletRepository;
import com.example.testtask_backend.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
public class LoadTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    private final UUID walletId = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @BeforeTestClass
    public void setup() {
        if (!walletRepository.existsById(walletId)) {
            walletRepository.save(Wallet.builder()
                    .id(walletId)
                    .balance(0L)
                    .build());
        }
    }

    @RepeatedTest(1000)
    public void stressTestSingleWallet() {
        WalletOperationRequest request = WalletOperationRequest.builder()
                .valletId(walletId)
                .operationType(OperationType.DEPOSIT)
                .amount(1L)
                .build();

        walletService.processOperation(request);
    }
}