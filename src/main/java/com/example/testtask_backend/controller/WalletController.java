package com.example.testtask_backend.controller;

import com.example.testtask_backend.dto.WalletOperationRequest;
import com.example.testtask_backend.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/wallet")
    public ResponseEntity<Void> processOperation(@RequestBody @Valid WalletOperationRequest request) {
        walletService.processOperation(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/wallets/{id}")
    public ResponseEntity<Long> getBalance(@PathVariable UUID id) {
        return ResponseEntity.ok(walletService.getBalance(id));
    }
}

