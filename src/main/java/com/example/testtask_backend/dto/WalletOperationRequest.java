package com.example.testtask_backend.dto;

import com.example.testtask_backend.model.OperationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletOperationRequest {
    @NotNull
    private UUID valletId;
    @NotNull
    private OperationType operationType;
    @NotNull
    @Positive
    private Long amount;
}

