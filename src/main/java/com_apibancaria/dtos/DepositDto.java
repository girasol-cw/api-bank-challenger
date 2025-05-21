package com_apibancaria.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;

public record DepositDto(
        @NotNull @Positive double amount,
        @NotBlank String currencyCode
) {
}