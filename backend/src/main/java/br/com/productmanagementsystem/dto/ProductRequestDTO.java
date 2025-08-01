package br.com.productmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Data for creating or updating a product")
public record ProductRequestDTO(
        @Schema(
            description = "Product name",
            example = "Smartphone Samsung Galaxy",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 3,
            maxLength = 100
        )
        @NotBlank(message = "{validation.name.required}")
        @Size(min = 3, max = 100, message = "{validation.name.size}")
        String name,

        @Schema(
            description = "Product price in currency",
            example = "1299.99",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "0.01",
            maximum = "999999.99"
        )
        @NotNull(message = "{validation.price.required}")
        @DecimalMin(value = "0.01", message = "{validation.price.min}")
        @DecimalMax(value = "999999.99", message = "{validation.price.max}")
        BigDecimal price,

        @Schema(
            description = "Detailed product description",
            example = "Smartphone with 6.1-inch display, 128GB storage and 64MP camera",
            maxLength = 500
        )
        @Size(max = 500, message = "{validation.description.size}")
        String description,

        @Schema(
            description = "Stock quantity",
            example = "50",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "0",
            maximum = "999999"
        )
        @NotNull(message = "{validation.quantity.required}")
        @Min(value = 0, message = "{validation.quantity.min}")
        @Max(value = 999999, message = "{validation.quantity.max}")
        Integer quantity
) {
}