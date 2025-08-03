package br.com.productmanagementsystem.controller;

import br.com.productmanagementsystem.dto.ProductQueryDTO;
import br.com.productmanagementsystem.dto.ProductRequestDTO;
import br.com.productmanagementsystem.dto.ProductResponseDTO;
import br.com.productmanagementsystem.exception.ProductAlreadyExistsException;
import br.com.productmanagementsystem.exception.ProductNotFoundException;
import br.com.productmanagementsystem.exception.ResourceNotFoundException;
import br.com.productmanagementsystem.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.dao.DataIntegrityViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static br.com.productmanagementsystem.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public final class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    public void givenDefaultPageable_whenFindingAll_thenShouldReturnPageOfProducts() throws Exception {
        // Arrange
        ProductResponseDTO responseDTO = createDefaultProductResponseDTO();
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductResponseDTO> productPage = new PageImpl<>(List.of(responseDTO), pageable, 1);

        when(this.productService.findAll(any(ProductQueryDTO.class), any(Pageable.class))).thenReturn(productPage);

        // Act & Assert
        mockMvc
                .perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].publicId").value(DEFAULT_PUBLIC_ID))
                .andExpect(jsonPath("$.content[0].name").value(SMARTPHONE_NAME))
                .andExpect(jsonPath("$.content[0].price").value(SMARTPHONE_PRICE))
                .andExpect(jsonPath("$.content[0].description").value(SMARTPHONE_DESCRIPTION))
                .andExpect(jsonPath("$.content[0].quantity").value(SMARTPHONE_QUANTITY))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    public void givenValidPublicId_whenFindingByPublicId_thenShouldReturnProduct() throws Exception {
        // Arrange
        ProductResponseDTO responseDTO = createDefaultProductResponseDTO();

        when(this.productService.findByPublicId(DEFAULT_PUBLIC_ID)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc
                .perform(get("/api/v1/products/{publicId}", DEFAULT_PUBLIC_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value(DEFAULT_PUBLIC_ID))
                .andExpect(jsonPath("$.name").value(SMARTPHONE_NAME))
                .andExpect(jsonPath("$.price").value(SMARTPHONE_PRICE))
                .andExpect(jsonPath("$.description").value(SMARTPHONE_DESCRIPTION))
                .andExpect(jsonPath("$.quantity").value(SMARTPHONE_QUANTITY));
    }

    @Test
    public void givenValidProductRequestDTO_whenCreating_thenShouldReturnCreatedProduct() throws Exception {
        // Arrange
        ProductRequestDTO requestDTO = createDefaultProductRequestDTO();
        ProductResponseDTO responseDTO = createDefaultProductResponseDTO();

        when(this.productService.save(any(ProductRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc
                .perform(post("/api/v1/products")
                        .content(this.objectMapper.writeValueAsString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.publicId").value(DEFAULT_PUBLIC_ID))
                .andExpect(jsonPath("$.name").value(SMARTPHONE_NAME))
                .andExpect(jsonPath("$.price").value(SMARTPHONE_PRICE))
                .andExpect(jsonPath("$.description").value(SMARTPHONE_DESCRIPTION))
                .andExpect(jsonPath("$.quantity").value(SMARTPHONE_QUANTITY));
    }

    @Test
    public void givenValidPublicIdAndProductRequestDTO_whenUpdating_thenShouldReturnUpdatedProduct() throws Exception {
        // Arrange
        ProductRequestDTO requestDTO = createUpdatedProductRequestDTO();
        ProductResponseDTO responseDTO = new ProductResponseDTO(
                DEFAULT_PUBLIC_ID,
                UPDATED_NAME,
                UPDATED_PRICE,
                UPDATED_DESCRIPTION,
                UPDATED_QUANTITY
        );

        when(this.productService.update(DEFAULT_PUBLIC_ID, requestDTO)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc
                .perform(put("/api/v1/products/{publicId}", DEFAULT_PUBLIC_ID)
                        .content(this.objectMapper.writeValueAsString(requestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicId").value(DEFAULT_PUBLIC_ID))
                .andExpect(jsonPath("$.name").value(UPDATED_NAME))
                .andExpect(jsonPath("$.price").value(UPDATED_PRICE))
                .andExpect(jsonPath("$.description").value(UPDATED_DESCRIPTION))
                .andExpect(jsonPath("$.quantity").value(UPDATED_QUANTITY));
    }

    @Test
    public void givenValidPublicId_whenDeleting_thenShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(this.productService).delete(DEFAULT_PUBLIC_ID);

        // Act & Assert
        mockMvc
                .perform(delete("/api/v1/products/{publicId}", DEFAULT_PUBLIC_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // Tests to indirectly validate GlobalExceptionHandler

    @Test
    public void givenInvalidProductRequestDTO_whenCreating_thenShouldReturnBadRequest() throws Exception {
        // Arrange
        ProductRequestDTO invalidRequestDTO = new ProductRequestDTO(
                null, // invalid name
                null, // invalid price
                "Valid description",
                null  // invalid quantity
        );

        // Act & Assert
        mockMvc
                .perform(post("/api/v1/products")
                        .content(this.objectMapper.writeValueAsString(invalidRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenProductViolatingBusinessRules_whenCreating_thenShouldReturnBadRequestWithObjectError() throws Exception {
        // Arrange - Product that violates class-level business rule
        // Low-value product with high quantity
        ProductRequestDTO requestViolatingBusinessRule = new ProductRequestDTO(
                "Cheap Product",
                new BigDecimal("5.00"),  // price < 10
                "Low-value product description",
                200  // quantity > 100, violates business rule
        );

        // Act & Assert
        mockMvc
                .perform(post("/api/v1/products")
                        .content(objectMapper.writeValueAsString(requestViolatingBusinessRule))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field == 'productRequestDTO')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field == 'productRequestDTO')].message")
                        .value(hasItem("Low-value products (price < 10) cannot have quantity greater than 100")));
    }

    @Test
    public void givenHighValueProductWithExcessiveQuantity_whenCreating_thenShouldReturnBadRequestWithObjectError() throws Exception {
        // Arrange - High-value product that violates business rule
        ProductRequestDTO luxuryProductWithHighQuantity = new ProductRequestDTO(
                "Luxury Watch",
                new BigDecimal("15000.00"),  // price > 10000
                "Exclusive luxury timepiece",
                20  // quantity > 10, violates business rule for luxury items
        );

        // Act & Assert
        mockMvc
                .perform(post("/api/v1/products")
                        .content(objectMapper.writeValueAsString(luxuryProductWithHighQuantity))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field == 'productRequestDTO')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field == 'productRequestDTO')].message")
                        .value(hasItem("High-value products (price > 10000) must have quantity less than or equal to 10")));
    }

    @Test
    public void givenInvalidProductWithBrazilianLocale_whenCreating_thenShouldReturnLocalizedErrorMessages() throws Exception {
        // Arrange - Invalid product with Brazilian Portuguese locale
        ProductRequestDTO invalidProduct = new ProductRequestDTO(
                null,  // name is null
                null,  // price is null
                "Description",
                null   // quantity is null
        );

        // Act & Assert - Test with pt-BR locale
        mockMvc
                .perform(post("/api/v1/products")
                        .content(objectMapper.writeValueAsString(invalidProduct))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "pt-BR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field == 'name')].message").exists())
                .andExpect(jsonPath("$.errors[?(@.field == 'price')].message").exists())
                .andExpect(jsonPath("$.errors[?(@.field == 'quantity')].message").exists());
    }

    @Test
    public void givenProductNotFound_whenGettingWithDefaultLocale_thenShouldReturnEnglishErrorMessage() throws Exception {
        // Arrange
        String nonExistentId = NON_EXISTENT_PUBLIC_ID;

        when(productService.findByPublicId(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Product not found with publicId: " + nonExistentId));

        // Act & Assert - Default locale (English) with Problem Details format
        mockMvc
                .perform(get("/api/v1/products/{publicId}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/resource-not-found"))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Product not found with publicId: " + nonExistentId))
                .andExpect(jsonPath("$.instance").value("/api/v1/products/" + nonExistentId));
    }

    @Test
    public void givenNonExistentProduct_whenFindingByPublicId_thenShouldReturnRFC7807ProblemDetails() throws Exception {
        // Arrange
        String nonExistentPublicId = NON_EXISTENT_PUBLIC_ID;
        Long productId = 123L;

        when(productService.findByPublicId(nonExistentPublicId))
                .thenThrow(new ProductNotFoundException(productId));

        // Act & Assert - RFC 7807 Problem Details format
        mockMvc
                .perform(get("/api/v1/products/{publicId}", nonExistentPublicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/product-not-found"))
                .andExpect(jsonPath("$.title").value("Product Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("The requested product could not be found"))
                .andExpect(jsonPath("$.instance").value("/api/v1/products/" + nonExistentPublicId))
                .andExpect(jsonPath("$.productId").value(productId));
    }

    @Test
    public void givenNonExistentProduct_whenUpdating_thenShouldReturnRFC7807ProblemDetails() throws Exception {
        // Arrange
        String nonExistentPublicId = NON_EXISTENT_PUBLIC_ID;
        Long productId = 456L;
        ProductRequestDTO updateRequest = createUpdatedProductRequestDTO();

        when(productService.update(eq(nonExistentPublicId), any(ProductRequestDTO.class)))
                .thenThrow(new ProductNotFoundException(productId));

        // Act & Assert - RFC 7807 Problem Details format
        mockMvc
                .perform(put("/api/v1/products/{publicId}", nonExistentPublicId)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/product-not-found"))
                .andExpect(jsonPath("$.title").value("Product Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("The requested product could not be found"))
                .andExpect(jsonPath("$.instance").value("/api/v1/products/" + nonExistentPublicId))
                .andExpect(jsonPath("$.productId").value(productId));
    }

    @Test
    public void givenMultipleNonExistentProducts_whenFindingByPublicId_thenEachShouldReturnCorrectProblemDetails() throws Exception {
        // Arrange - Test with different IDs to ensure proper parameter substitution
        String[] nonExistentIds = {"111e1111-e11b-11d4-a111-111111111111", "222e2222-e22b-22d4-a222-222222222222", "333e3333-e33b-33d4-a333-333333333333"};
        Long[] productIds = {123L, 456L, 999L};

        for (int i = 0; i < nonExistentIds.length; i++) {
            String publicId = nonExistentIds[i];
            Long productId = productIds[i];

            // Arrange
            when(productService.findByPublicId(publicId))
                    .thenThrow(new ProductNotFoundException(productId));

            // Act & Assert
            mockMvc
                    .perform(get("/api/v1/products/{publicId}", publicId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/problem+json"))
                    .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/product-not-found"))
                    .andExpect(jsonPath("$.title").value("Product Not Found"))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.detail").value("The requested product could not be found"))
                    .andExpect(jsonPath("$.instance").value("/api/v1/products/" + publicId))
                    .andExpect(jsonPath("$.productId").value(productId));
        }
    }

    @Test
    public void givenNonExistentProduct_whenDeletingWithProductNotFoundEnabled_thenShouldReturnRFC7807ProblemDetails() throws Exception {
        // Arrange - Test delete operation if it throws ProductNotFoundException
        String nonExistentPublicId = NON_EXISTENT_PUBLIC_ID;
        Long productId = 789L;

        // If your service throws exception on delete for non-existent products
        doThrow(new ProductNotFoundException(productId))
                .when(productService).delete(nonExistentPublicId);

        // Act & Assert
        mockMvc
                .perform(delete("/api/v1/products/{publicId}", nonExistentPublicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/product-not-found"))
                .andExpect(jsonPath("$.title").value("Product Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("The requested product could not be found"))
                .andExpect(jsonPath("$.instance").value("/api/v1/products/" + nonExistentPublicId))
                .andExpect(jsonPath("$.productId").value(productId));
    }

    @Test
    public void givenExistingProductName_whenCreating_thenShouldReturnConflictWithRFC7807ProblemDetails() throws Exception {
        // Arrange
        String existingProductName = "iPhone 15 Pro Max";
        ProductRequestDTO newProduct = new ProductRequestDTO(
                existingProductName,
                new BigDecimal("1299.99"),
                "Latest iPhone model",
                50
        );

        when(productService.save(any(ProductRequestDTO.class)))
                .thenThrow(new ProductAlreadyExistsException(existingProductName));

        // Act & Assert - RFC 7807 Problem Details format
        mockMvc
                .perform(post("/api/v1/products")
                        .content(objectMapper.writeValueAsString(newProduct))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/product-already-exists"))
                .andExpect(jsonPath("$.title").value("Product Already Exists"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("A product with this information already exists"))
                .andExpect(jsonPath("$.instance").value("/api/v1/products"));
    }

    @Test
    public void givenExistingProductName_whenUpdating_thenShouldReturnConflictWithRFC7807ProblemDetails() throws Exception {
        // Arrange
        String publicId = DEFAULT_PUBLIC_ID;
        String conflictingProductName = "Samsung Galaxy S24 Ultra";
        ProductRequestDTO updateRequest = new ProductRequestDTO(
                conflictingProductName,
                new BigDecimal("1499.99"),
                "Premium Android phone",
                30
        );

        when(productService.update(eq(publicId), any(ProductRequestDTO.class)))
                .thenThrow(new ProductAlreadyExistsException(conflictingProductName));

        // Act & Assert - RFC 7807 Problem Details format
        mockMvc
                .perform(put("/api/v1/products/{publicId}", publicId)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/product-already-exists"))
                .andExpect(jsonPath("$.title").value("Product Already Exists"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("A product with this information already exists"))
                .andExpect(jsonPath("$.instance").value("/api/v1/products/" + publicId));
    }

    @Test
    public void givenMultipleDuplicateProducts_whenCreating_thenEachShouldReturnCorrectConflictDetails() throws Exception {
        // Arrange - Test with different product names to ensure proper parameter handling
        String[] duplicateProductNames = {
                "MacBook Pro M3",
                "iPad Air 2024",
                "AirPods Pro 2nd Gen"
        };

        for (String productName : duplicateProductNames) {
            ProductRequestDTO duplicateProduct = new ProductRequestDTO(
                    productName,
                    new BigDecimal("999.99"),
                    "Product description",
                    10
            );

            when(productService.save(any(ProductRequestDTO.class)))
                    .thenThrow(new ProductAlreadyExistsException(productName));

            // Act & Assert
            mockMvc
                    .perform(post("/api/v1/products")
                            .content(objectMapper.writeValueAsString(duplicateProduct))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType("application/problem+json"))
                    .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/product-already-exists"))
                    .andExpect(jsonPath("$.title").value("Product Already Exists"))
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.detail").value("A product with this information already exists"))
                    .andExpect(jsonPath("$.instance").value("/api/v1/products"));
        }
    }

    @Test
    public void givenProductWithSpecialCharactersInName_whenAlreadyExists_thenShouldHandleCorrectly() throws Exception {
        // Arrange - Test with special characters in product name
        String specialProductName = "Product @#$% & Special (2024) - Edition!";
        ProductRequestDTO specialProduct = new ProductRequestDTO(
                specialProductName,
                new BigDecimal("299.99"),
                "Special edition product",
                5
        );

        when(productService.save(any(ProductRequestDTO.class)))
                .thenThrow(ProductAlreadyExistsException.class);

        // Act & Assert
        mockMvc
                .perform(post("/api/v1/products")
                        .content(objectMapper.writeValueAsString(specialProduct))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/product-already-exists"))
                .andExpect(jsonPath("$.title").value("Product Already Exists"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("A product with this information already exists"))
                .andExpect(jsonPath("$.instance").value("/api/v1/products"));
    }

    @Test
    public void givenInvalidArgumentInService_whenCreatingProduct_thenShouldReturnBadRequestWithRFC7807() throws Exception {
        // Arrange
        ProductRequestDTO invalidProduct = createDefaultProductRequestDTO();
        String errorMessage = "Price cannot be negative in business logic";

        when(productService.save(any(ProductRequestDTO.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        // Act & Assert - RFC 7807 Problem Details format
        mockMvc
                .perform(post("/api/v1/products")
                        .content(objectMapper.writeValueAsString(invalidProduct))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/invalid-argument"))
                .andExpect(jsonPath("$.title").value("Invalid Argument"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Invalid argument provided"))
                .andExpect(jsonPath("$.instance").value("/api/v1/products"));
    }

    @Test
    public void givenInvalidArgumentInService_whenUpdatingProduct_thenShouldReturnBadRequestWithRFC7807() throws Exception {
        // Arrange
        String publicId = DEFAULT_PUBLIC_ID;
        ProductRequestDTO updateRequest = createUpdatedProductRequestDTO();
        String errorMessage = "Quantity cannot exceed maximum stock limit";

        when(productService.update(eq(publicId), any(ProductRequestDTO.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        // Act & Assert
        mockMvc
                .perform(put("/api/v1/products/{publicId}", publicId)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/invalid-argument"))
                .andExpect(jsonPath("$.title").value("Invalid Argument"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Invalid argument provided"))
                .andExpect(jsonPath("$.instance").value("/api/v1/products/" + publicId));
    }

    @Test
    public void givenInvalidPublicIdFormat_whenFindingProduct_thenShouldReturnBadRequestWithRFC7807() throws Exception {
        // Arrange
        String invalidPublicId = INVALID_UUID_FORMAT; // Invalid format that should be caught by validation

        // Note: Service won't be called because validation fails at controller level

        // Act & Assert
        mockMvc
                .perform(get("/api/v1/products/{publicId}", invalidPublicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/constraint-violation"))
                .andExpect(jsonPath("$.title").value("Constraint Violation"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Request parameters validation failed"))
                .andExpect(jsonPath("$.instance").value("/api/v1/products/" + invalidPublicId));
    }

    @Test
    public void givenIllegalArgumentInDelete_whenDeletingProduct_thenShouldReturnBadRequestWithRFC7807() throws Exception {
        // Arrange
        String invalidPublicId = INVALID_UUID_FORMAT;

        // Note: Service won't be called because validation fails at controller level

        // Act & Assert
        mockMvc
                .perform(delete("/api/v1/products/{publicId}", invalidPublicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/constraint-violation"))
                .andExpect(jsonPath("$.title").value("Constraint Violation"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Request parameters validation failed"))
                .andExpect(jsonPath("$.instance").value("/api/v1/products/" + invalidPublicId));
    }

    @Test
    public void givenMultipleIllegalArgumentScenarios_whenProcessing_thenAllShouldReturnConsistentErrorFormat() throws Exception {
        // Test various IllegalArgumentException scenarios to ensure consistent handling

        // Scenario 1: Null argument in service layer
        ProductRequestDTO productWithNullHandling = createDefaultProductRequestDTO();
        when(productService.save(any(ProductRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Null value not allowed"));

        mockMvc
                .perform(post("/api/v1/products")
                        .content(objectMapper.writeValueAsString(productWithNullHandling))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/invalid-argument"))
                .andExpect(jsonPath("$.title").value("Invalid Argument"))
                .andExpect(jsonPath("$.detail").value("Invalid argument provided"));

        // Scenario 2: Business rule violation
        when(productService.save(any(ProductRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Business rule violated"));

        mockMvc
                .perform(post("/api/v1/products")
                        .content(objectMapper.writeValueAsString(productWithNullHandling))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/invalid-argument"))
                .andExpect(jsonPath("$.title").value("Invalid Argument"))
                .andExpect(jsonPath("$.detail").value("Invalid argument provided"));
    }

    @Test
    public void givenConstraintViolation_whenProcessingRequest_thenShouldReturnBadRequestWithRFC7807() throws Exception {
        // This test simulates a ConstraintViolationException that might occur at the service layer
        // For example, when a method parameter validation fails

        // Arrange
        String publicId = DEFAULT_PUBLIC_ID;

        // Create a mock ConstraintViolation
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        Path propertyPath1 = mock(Path.class);
        when(propertyPath1.toString()).thenReturn("name");
        when(violation1.getPropertyPath()).thenReturn(propertyPath1);
        when(violation1.getMessage()).thenReturn("must not be blank");
        when(violation1.getInvalidValue()).thenReturn("");

        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        Path propertyPath2 = mock(Path.class);
        when(propertyPath2.toString()).thenReturn("price");
        when(violation2.getPropertyPath()).thenReturn(propertyPath2);
        when(violation2.getMessage()).thenReturn("must be greater than 0");
        when(violation2.getInvalidValue()).thenReturn(BigDecimal.ZERO);

        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation1);
        violations.add(violation2);

        ConstraintViolationException exception = new ConstraintViolationException(
                "Validation failed", violations
        );

        when(productService.findByPublicId(publicId))
                .thenThrow(exception);

        // Act & Assert
        mockMvc
                .perform(get("/api/v1/products/{publicId}", publicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/constraint-violation"))
                .andExpect(jsonPath("$.title").value("Constraint Violation"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Request parameters validation failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field == 'name')].message").value(hasItem("must not be blank")))
                .andExpect(jsonPath("$.errors[?(@.field == 'name')].invalidValue").value(hasItem("")))
                .andExpect(jsonPath("$.errors[?(@.field == 'price')].message").value(hasItem("must be greater than 0")))
                .andExpect(jsonPath("$.errors[?(@.field == 'price')].invalidValue").value(hasItem("0")));
    }

    @Test
    public void givenConstraintViolationWithNullInvalidValue_whenProcessing_thenShouldHandleCorrectly() throws Exception {
        // Test the case where invalidValue is null (line 15-17 in the method)

        // Arrange
        String publicId = DEFAULT_PUBLIC_ID;

        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path propertyPath = mock(Path.class);
        when(propertyPath.toString()).thenReturn("quantity");
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(violation.getMessage()).thenReturn("must not be null");
        when(violation.getInvalidValue()).thenReturn(null); // null invalid value

        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation);

        ConstraintViolationException exception = new ConstraintViolationException(
                "Validation failed", violations
        );

        when(productService.findByPublicId(publicId))
                .thenThrow(exception);

        // Act & Assert
        mockMvc
                .perform(get("/api/v1/products/{publicId}", publicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].field").value("quantity"))
                .andExpect(jsonPath("$.errors[0].message").value("must not be null"))
                .andExpect(jsonPath("$.errors[0].invalidValue").doesNotExist()); // Should not include invalidValue when null
    }

    @Test
    public void givenMultipleConstraintViolations_whenCreatingProduct_thenShouldReturnAllErrors() throws Exception {
        // Test with multiple violations to ensure all are processed correctly

        // Arrange
        ProductRequestDTO productRequest = createDefaultProductRequestDTO();

        // Create multiple violations
        Set<ConstraintViolation<?>> violations = new HashSet<>();

        for (int i = 1; i <= 3; i++) {
            ConstraintViolation<?> violation = mock(ConstraintViolation.class);
            Path propertyPath = mock(Path.class);
            when(propertyPath.toString()).thenReturn("field" + i);
            when(violation.getPropertyPath()).thenReturn(propertyPath);
            when(violation.getMessage()).thenReturn("validation error " + i);
            when(violation.getInvalidValue()).thenReturn("invalid value " + i);
            violations.add(violation);
        }

        ConstraintViolationException exception = new ConstraintViolationException(
                "Multiple validation errors", violations
        );

        when(productService.save(any(ProductRequestDTO.class)))
                .thenThrow(exception);

        // Act & Assert
        mockMvc
                .perform(post("/api/v1/products")
                        .content(objectMapper.writeValueAsString(productRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/constraint-violation"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(3)));
    }

    @Test
    public void givenConstraintViolationWithComplexPath_whenProcessing_thenShouldExtractFieldNameCorrectly() throws Exception {
        // Test complex property paths to ensure extractFieldName works correctly

        // Arrange
        String publicId = DEFAULT_PUBLIC_ID;

        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path propertyPath = mock(Path.class);
        // Simulate a complex path like "productDetails.specifications.weight"
        when(propertyPath.toString()).thenReturn("productDetails.specifications.weight");
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(violation.getMessage()).thenReturn("must be positive");
        when(violation.getInvalidValue()).thenReturn(-1.5);

        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        when(productService.findByPublicId(publicId))
                .thenThrow(exception);

        // Act & Assert
        mockMvc
                .perform(get("/api/v1/products/{publicId}", publicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").exists()) // Field should be extracted
                .andExpect(jsonPath("$.errors[0].message").value("must be positive"))
                .andExpect(jsonPath("$.errors[0].invalidValue").value("-1.5"));
    }

    @Test
    public void givenDataIntegrityViolation_whenCreatingProduct_thenShouldReturnConflictWithRFC7807() throws Exception {
        // This test simulates a scenario where DataIntegrityViolationException is thrown
        // (e.g., duplicate unique constraint violation, NOT NULL constraint violation)
        // and verifies that the GlobalExceptionHandler properly converts it to HTTP 409 CONFLICT

        // Arrange
        ProductRequestDTO duplicateProduct = createDefaultProductRequestDTO();
        
        // Mock the service to throw DataIntegrityViolationException
        // This simulates what happens when repository encounters a constraint violation
        when(productService.save(any(ProductRequestDTO.class)))
                .thenThrow(new DataIntegrityViolationException("Unique index or primary key violation"));

        // Act & Assert
        mockMvc
                .perform(post("/api/v1/products")
                        .content(objectMapper.writeValueAsString(duplicateProduct))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/database-constraint-violation"))
                .andExpect(jsonPath("$.title").value("Database Constraint Violation"))
                .andExpect(jsonPath("$.detail").value("Database constraint violation occurred"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    public void givenDataIntegrityViolation_whenUpdatingProduct_thenShouldReturnConflictWithRFC7807() throws Exception {
        // Test the same scenario but for update operation

        // Arrange
        String publicId = DEFAULT_PUBLIC_ID;
        ProductRequestDTO updateRequest = createUpdatedProductRequestDTO();
        
        // Mock the service to throw DataIntegrityViolationException
        when(productService.update(eq(publicId), any(ProductRequestDTO.class)))
                .thenThrow(new DataIntegrityViolationException("Column length exceeded"));

        // Act & Assert
        mockMvc
                .perform(put("/api/v1/products/{publicId}", publicId)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("https://api.productmanagement.com.br/database-constraint-violation"))
                .andExpect(jsonPath("$.title").value("Database Constraint Violation"))
                .andExpect(jsonPath("$.detail").value("Database constraint violation occurred"))
                .andExpect(jsonPath("$.status").value(409));
    }


}