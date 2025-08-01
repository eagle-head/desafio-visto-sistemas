package br.com.productmanagementsystem.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank(message = "{validation.name.required}")
        @Size(min = 3, max = 100, message = "{validation.name.size}")
        String name,

        @NotNull(message = "{validation.price.required}")
        @DecimalMin(value = "0.01", message = "{validation.price.min}")
        @DecimalMax(value = "999999.99", message = "{validation.price.max}")
        BigDecimal price,

        @Size(max = 500, message = "{validation.description.size}")
        String description,

        @NotNull(message = "{validation.quantity.required}")
        @Min(value = 0, message = "{validation.quantity.min}")
        @Max(value = 999999, message = "{validation.quantity.max}")
        Integer quantity
) {
}