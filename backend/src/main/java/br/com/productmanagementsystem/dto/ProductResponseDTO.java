package br.com.productmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Product response data")
public record ProductResponseDTO(
        @Schema(
            description = "Unique public ID of the product",
            example = "abc123def456"
        )
        String publicId,
        
        @Schema(
            description = "Product name",
            example = "Smartphone Samsung Galaxy"
        )
        String name,
        
        @Schema(
            description = "Product price in currency",
            example = "1299.99"
        )
        BigDecimal price,
        
        @Schema(
            description = "Detailed product description",
            example = "Smartphone with 6.1-inch display, 128GB storage and 64MP camera"
        )
        String description,
        
        @Schema(
            description = "Stock quantity",
            example = "50"
        )
        Integer quantity
) {
}