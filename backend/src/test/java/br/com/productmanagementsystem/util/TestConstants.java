package br.com.productmanagementsystem.util;

import br.com.productmanagementsystem.dto.ProductQueryDTO;
import br.com.productmanagementsystem.dto.ProductRequestDTO;
import br.com.productmanagementsystem.dto.ProductResponseDTO;
import br.com.productmanagementsystem.entity.Product;

import java.math.BigDecimal;

public final class TestConstants {

    private TestConstants() {
        // Private constructor to prevent instantiation
        throw new AssertionError("TestConstants should not be instantiated");
    }

    // IDs
    public static final String DEFAULT_PUBLIC_ID = "550e8400-e29b-41d4-a716-446655440000";
    public static final String ALTERNATIVE_PUBLIC_ID = "123e4567-e89b-12d3-a456-426614174000";
    public static final String NON_EXISTENT_PUBLIC_ID = "999e9999-e99b-99d4-a999-999999999999";
    public static final String INVALID_UUID_FORMAT = "invalid-uuid-format";
    public static final Long DEFAULT_ID = 1L;
    public static final Long ALTERNATIVE_ID = 2L;

    // Regex Patterns
    public static final String UUID_REGEX = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

    // Product Data - Smartphone
    public static final String SMARTPHONE_NAME = "Smartphone Samsung Galaxy";
    public static final BigDecimal SMARTPHONE_PRICE = new BigDecimal("1299.99");
    public static final String SMARTPHONE_DESCRIPTION = "Smartphone with 6.1-inch display, 128GB storage and 64MP camera";
    public static final Integer SMARTPHONE_QUANTITY = 50;

    // Product Data - Notebook
    public static final String NOTEBOOK_NAME = "Notebook Dell Inspiron";
    public static final BigDecimal NOTEBOOK_PRICE = new BigDecimal("3499.90");
    public static final String NOTEBOOK_DESCRIPTION = "Notebook with Intel i7, 16GB RAM, 512GB SSD";
    public static final Integer NOTEBOOK_QUANTITY = 20;

    // Product Data - Mouse
    public static final String MOUSE_NAME = "Mouse Logitech MX Master";
    public static final BigDecimal MOUSE_PRICE = new BigDecimal("299.00");
    public static final String MOUSE_DESCRIPTION = "Wireless mouse with ergonomic design";
    public static final Integer MOUSE_QUANTITY = 100;

    // Product Data - Minimal
    public static final String MINIMAL_NAME = "Test Product";
    public static final BigDecimal MINIMAL_PRICE = new BigDecimal("0.01");
    public static final Integer MINIMAL_QUANTITY = 0;

    // Product Data - Maximum
    public static final String MAXIMUM_NAME = "Premium Product with Very Long Name That Tests The Maximum Length";
    public static final BigDecimal MAXIMUM_PRICE = new BigDecimal("999999.99");
    public static final Integer MAXIMUM_QUANTITY = 999999;

    // Product Data - Updated
    public static final String UPDATED_NAME = "Updated Smartphone Samsung Galaxy S24";
    public static final BigDecimal UPDATED_PRICE = new BigDecimal("1599.99");
    public static final String UPDATED_DESCRIPTION = "Updated smartphone with 6.5-inch display, 256GB storage and 108MP camera";
    public static final Integer UPDATED_QUANTITY = 75;

    // Invalid Data
    public static final String EMPTY_NAME = "";
    public static final String NULL_NAME = null;
    public static final BigDecimal ZERO_PRICE = BigDecimal.ZERO;
    public static final BigDecimal NEGATIVE_PRICE = new BigDecimal("-10.00");
    public static final Integer NEGATIVE_QUANTITY = -1;

    // Query Filter Data
    public static final BigDecimal MIN_PRICE_FILTER = new BigDecimal("100.00");
    public static final BigDecimal MAX_PRICE_FILTER = new BigDecimal("2000.00");
    public static final Integer MIN_QUANTITY_FILTER = 1;
    public static final Integer MAX_QUANTITY_FILTER = 100;

    // Factory Methods for Entities
    public static Product createDefaultProduct() {
        return buildProduct()
                .withId(DEFAULT_ID)
                .withPublicId(DEFAULT_PUBLIC_ID)
                .withName(SMARTPHONE_NAME)
                .withPrice(SMARTPHONE_PRICE)
                .withDescription(SMARTPHONE_DESCRIPTION)
                .withQuantity(SMARTPHONE_QUANTITY)
                .build();
    }

    public static Product createProductWithoutDescription() {
        return buildProduct()
                .withId(DEFAULT_ID)
                .withPublicId(DEFAULT_PUBLIC_ID)
                .withName(MINIMAL_NAME)
                .withPrice(MINIMAL_PRICE)
                .withDescription(null)
                .withQuantity(MINIMAL_QUANTITY)
                .build();
    }

    public static Product createNotebookProduct() {
        return buildProduct()
                .withId(ALTERNATIVE_ID)
                .withPublicId(ALTERNATIVE_PUBLIC_ID)
                .withName(NOTEBOOK_NAME)
                .withPrice(NOTEBOOK_PRICE)
                .withDescription(NOTEBOOK_DESCRIPTION)
                .withQuantity(NOTEBOOK_QUANTITY)
                .build();
    }

    // Factory Methods for DTOs
    public static ProductRequestDTO createDefaultProductRequestDTO() {
        return new ProductRequestDTO(
                SMARTPHONE_NAME,
                SMARTPHONE_PRICE,
                SMARTPHONE_DESCRIPTION,
                SMARTPHONE_QUANTITY
        );
    }

    public static ProductRequestDTO createMinimalProductRequestDTO() {
        return new ProductRequestDTO(
                MINIMAL_NAME,
                MINIMAL_PRICE,
                null,
                MINIMAL_QUANTITY
        );
    }

    public static ProductRequestDTO createNotebookProductRequestDTO() {
        return new ProductRequestDTO(
                NOTEBOOK_NAME,
                NOTEBOOK_PRICE,
                NOTEBOOK_DESCRIPTION,
                NOTEBOOK_QUANTITY
        );
    }

    public static ProductRequestDTO createUpdatedProductRequestDTO() {
        return new ProductRequestDTO(
                UPDATED_NAME,
                UPDATED_PRICE,
                UPDATED_DESCRIPTION,
                UPDATED_QUANTITY
        );
    }

    public static ProductResponseDTO createDefaultProductResponseDTO() {
        return new ProductResponseDTO(
                DEFAULT_PUBLIC_ID,
                SMARTPHONE_NAME,
                SMARTPHONE_PRICE,
                SMARTPHONE_DESCRIPTION,
                SMARTPHONE_QUANTITY
        );
    }

    public static ProductResponseDTO createMinimalProductResponseDTO() {
        return new ProductResponseDTO(
                DEFAULT_PUBLIC_ID,
                MINIMAL_NAME,
                MINIMAL_PRICE,
                null,
                MINIMAL_QUANTITY
        );
    }

    public static ProductQueryDTO createDefaultProductQueryDTO() {
        return new ProductQueryDTO(
                SMARTPHONE_NAME,
                MIN_PRICE_FILTER,
                MAX_PRICE_FILTER,
                MIN_QUANTITY_FILTER,
                MAX_QUANTITY_FILTER,
                false
        );
    }

    public static ProductQueryDTO createEmptyProductQueryDTO() {
        return new ProductQueryDTO(null, null, null, null, null, null);
    }

    // Private Product Builder - for internal use only
    private static class ProductBuilder {
        private Long id = DEFAULT_ID;
        private String publicId = DEFAULT_PUBLIC_ID;
        private String name = SMARTPHONE_NAME;
        private BigDecimal price = SMARTPHONE_PRICE;
        private String description = SMARTPHONE_DESCRIPTION;
        private Integer quantity = SMARTPHONE_QUANTITY;

        private ProductBuilder() {
            // Private constructor
        }

        public ProductBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ProductBuilder withPublicId(String publicId) {
            this.publicId = publicId;
            return this;
        }

        public ProductBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public ProductBuilder withPrice(String price) {
            this.price = new BigDecimal(price);
            return this;
        }

        public ProductBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder withQuantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public ProductBuilder withoutDescription() {
            this.description = null;
            return this;
        }

        public ProductBuilder withMinimalData() {
            this.name = MINIMAL_NAME;
            this.price = MINIMAL_PRICE;
            this.description = null;
            this.quantity = MINIMAL_QUANTITY;
            return this;
        }

        public ProductBuilder withNotebookData() {
            this.id = ALTERNATIVE_ID;
            this.publicId = ALTERNATIVE_PUBLIC_ID;
            this.name = NOTEBOOK_NAME;
            this.price = NOTEBOOK_PRICE;
            this.description = NOTEBOOK_DESCRIPTION;
            this.quantity = NOTEBOOK_QUANTITY;
            return this;
        }

        public Product build() {
            Product product = new Product();
            product.setId(id);
            product.setPublicId(publicId);
            product.setName(name);
            product.setPrice(price);
            product.setDescription(description);
            product.setQuantity(quantity);
            return product;
        }
    }

    private static ProductBuilder buildProduct() {
        return new ProductBuilder();
    }
}