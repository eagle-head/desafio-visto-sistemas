package br.com.productmanagementsystem.dto;

import java.math.BigDecimal;

public record ProductResponseDTO(
        String publicId,
        String name,
        BigDecimal price,
        String description,
        Integer quantity
) {
}