package com.example.testtask_backend.controller;

import com.example.testtask_backend.dto.WalletOperationRequest;
import com.example.testtask_backend.exception.InsufficientFundsException;
import com.example.testtask_backend.exception.WalletNotFoundException;
import com.example.testtask_backend.model.OperationType;
import com.example.testtask_backend.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testProcessDepositOperation() throws Exception {
        WalletOperationRequest request = WalletOperationRequest.builder()
                .amount(1000L)
                .operationType(OperationType.DEPOSIT)
                .valletId(UUID.randomUUID())
                .build();

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBalance() throws Exception {
        UUID walletId = UUID.randomUUID();
        Mockito.when(walletService.getBalance(walletId)).thenReturn(5000L);

        mockMvc.perform(get("/api/v1/wallets/" + walletId))
                .andExpect(status().isOk())
                .andExpect(content().string("5000"));
    }

    @Test
    public void testWalletNotFound() throws Exception {
        UUID walletId = UUID.randomUUID();
        Mockito.doThrow(new WalletNotFoundException(walletId))
                .when(walletService).getBalance(walletId);

        mockMvc.perform(get("/api/v1/wallets/" + walletId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Wallet not found: " + walletId));
    }

    @Test
    public void testInsufficientFunds() throws Exception {
        WalletOperationRequest request = WalletOperationRequest.builder()
                .valletId(UUID.randomUUID())
                .operationType(OperationType.WITHDRAW)
                .amount(10000L)
                .build();

        Mockito.doThrow(new InsufficientFundsException(request.getValletId()))
                .when(walletService).processOperation(Mockito.any());

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Insufficient funds for wallet: " + request.getValletId()));
    }

    @Test
    public void testInvalidJson() throws Exception {
        String invalidJson = "{\"invalidField\": 123}";

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}

