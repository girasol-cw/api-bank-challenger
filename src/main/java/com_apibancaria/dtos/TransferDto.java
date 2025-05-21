package com_apibancaria.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record TransferDto(
        @NotNull UUID accountReceiver,
        @NotNull @Positive double amount,
        @NotBlank String currencyCode
) {
}