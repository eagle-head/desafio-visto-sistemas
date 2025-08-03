package br.com.productmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Parameters for product search and filtering")
public record ProductQueryDTO(
        @Schema(
                description = "Filter by product name (case insensitive)",
                example = "Samsung"
        )
        @Size(min = 1, max = 50, message = "{productquery.name.size}")
        String name,

        @Schema(
                description = "Filter by minimum price",
                example = "10.00"
        )
        @DecimalMin(value = "0.01", message = "{productquery.price.min}")
        @DecimalMax(value = "999999.99", message = "{productquery.price.max}")
        BigDecimal minPrice,

        @Schema(
                description = "Filter by maximum price",
                example = "1000.00"
        )
        @DecimalMin(value = "0.01", message = "{productquery.price.min}")
        @DecimalMax(value = "999999.99", message = "{productquery.price.max}")
        BigDecimal maxPrice,

        @Schema(
                description = "Filter by minimum quantity",
                example = "1"
        )
        @Min(value = 0, message = "{productquery.quantity.min}")
        @Max(value = 999999, message = "{productquery.quantity.max}")
        Integer minQuantity,

        @Schema(
                description = "Filter by maximum quantity",
                example = "100"
        )
        @Min(value = 0, message = "{productquery.quantity.min}")
        @Max(value = 999999, message = "{productquery.quantity.max}")
        Integer maxQuantity,

        @Schema(
                description = "Include products with zero quantity",
                example = "false"
        )
        Boolean includeOutOfStock
) {

    public ProductQueryDTO {
        includeOutOfStock = includeOutOfStock != null ? includeOutOfStock : true;
    }

    @AssertTrue(message = "{productquery.price.range.invalid}")
    public boolean isPriceRangeValid() {
        if (minPrice == null || maxPrice == null) return true;
        return maxPrice.compareTo(minPrice) >= 0;
    }

    @AssertTrue(message = "{productquery.quantity.range.invalid}")
    public boolean isQuantityRangeValid() {
        if (minQuantity == null || maxQuantity == null) return true;
        return maxQuantity >= minQuantity;
    }
}