package br.com.productmanagementsystem.specification;

import br.com.productmanagementsystem.dto.ProductQueryDTO;
import br.com.productmanagementsystem.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Utility class to create specifications for dynamic Product queries
 */
public final class ProductSpecification {

    private ProductSpecification() {
    }

    /**
     * Creates a specification for filtering product records based on query parameters
     */
    public static Specification<Product> buildSpecification(ProductQueryDTO query) {
        return Specification.allOf(
                nameContains(query.name()),
                priceBetween(query.minPrice(), query.maxPrice()),
                quantityBetween(query.minQuantity(), query.maxQuantity()),
                stockFilter(query.includeOutOfStock())
        );
    }

    /**
     * Filter by product name (case insensitive)
     */
    public static Specification<Product> nameContains(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) return null;
            
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + name.toLowerCase().trim() + "%"
            );
        };
    }

    /**
     * Filter by price range
     */
    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) return null;
            
            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            }
            
            if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            }
            
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }


    /**
     * Filter by quantity range
     */
    public static Specification<Product> quantityBetween(Integer minQuantity, Integer maxQuantity) {
        return (root, query, criteriaBuilder) -> {
            if (minQuantity == null && maxQuantity == null) return null;
            
            if (minQuantity != null && maxQuantity != null) {
                return criteriaBuilder.between(root.get("quantity"), minQuantity, maxQuantity);
            }
            
            if (minQuantity != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("quantity"), minQuantity);
            }
            
            return criteriaBuilder.lessThanOrEqualTo(root.get("quantity"), maxQuantity);
        };
    }

    /**
     * Filter products in stock (quantity > 0)
     */
    public static Specification<Product> inStock() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.greaterThan(root.get("quantity"), 0);
    }

    /**
     * Stock filter based on includeOutOfStock parameter
     */
    private static Specification<Product> stockFilter(Boolean includeOutOfStock) {
        if (includeOutOfStock) {
            return null;
        }

        return inStock();
    }
}