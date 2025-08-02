package br.com.productmanagementsystem.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductQueryDTOTest {

    @Test
    public void givenNullIncludeOutOfStock_whenCreatingDTO_thenShouldConvertToTrue() {
        // Arrange
        
        // Act
        ProductQueryDTO dto = new ProductQueryDTO(
                "Product",
                new BigDecimal("10.00"),
                new BigDecimal("50.00"),
                5,
                10,
                null
        );

        // Assert
        assertThat(dto.includeOutOfStock()).isTrue();
    }

    @Test
    public void givenTrueIncludeOutOfStock_whenCreatingDTO_thenShouldKeepTrue() {
        // Arrange
        
        // Act
        ProductQueryDTO dto = new ProductQueryDTO(
                "Product",
                new BigDecimal("10.00"),
                new BigDecimal("50.00"),
                5,
                10,
                true
        );

        // Assert
        assertThat(dto.includeOutOfStock()).isTrue();
    }

    @Test
    public void givenFalseIncludeOutOfStock_whenCreatingDTO_thenShouldKeepFalse() {
        // Arrange
        
        // Act
        ProductQueryDTO dto = new ProductQueryDTO(
                "Product",
                new BigDecimal("10.00"),
                new BigDecimal("50.00"),
                5,
                10,
                false
        );

        // Assert
        assertThat(dto.includeOutOfStock()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("providePriceRangeTestCases")
    public void givenPriceRange_whenValidatingPriceRange_thenShouldReturnExpectedResult(BigDecimal minPrice, BigDecimal maxPrice, boolean expectedValid) {
        // Arrange
        ProductQueryDTO dto = new ProductQueryDTO(
                "Product",
                minPrice,
                maxPrice,
                null,
                null,
                true
        );

        // Act
        boolean isValid = dto.isPriceRangeValid();

        // Assert
        assertThat(isValid).isEqualTo(expectedValid);
    }

    private static Stream<Arguments> providePriceRangeTestCases() {
        return Stream.of(
                Arguments.of(null, null, true), // ambos null
                Arguments.of(new BigDecimal("10.00"), null, true), // apenas max null
                Arguments.of(null, new BigDecimal("50.00"), true), // apenas min null
                Arguments.of(new BigDecimal("10.00"), new BigDecimal("50.00"), true), // min < max
                Arguments.of(new BigDecimal("30.00"), new BigDecimal("30.00"), true), // min = max
                Arguments.of(new BigDecimal("50.00"), new BigDecimal("10.00"), false) // min > max
        );
    }

    @ParameterizedTest
    @MethodSource("provideQuantityRangeTestCases")
    public void givenQuantityRange_whenValidatingQuantityRange_thenShouldReturnExpectedResult(Integer minQuantity, Integer maxQuantity, boolean expectedValid) {
        // Arrange
        ProductQueryDTO dto = new ProductQueryDTO(
                "Product",
                null,
                null,
                minQuantity,
                maxQuantity,
                true
        );

        // Act
        boolean isValid = dto.isQuantityRangeValid();

        // Assert
        assertThat(isValid).isEqualTo(expectedValid);
    }

    private static Stream<Arguments> provideQuantityRangeTestCases() {
        return Stream.of(
                Arguments.of(null, null, true), // ambos null
                Arguments.of(5, null, true), // apenas max null
                Arguments.of(null, 20, true), // apenas min null
                Arguments.of(5, 20, true), // min < max
                Arguments.of(15, 15, true), // min = max
                Arguments.of(20, 5, false) // min > max
        );
    }

    @Test
    public void givenValidValues_whenCreatingDTO_thenShouldAssignAllFieldsCorrectly() {
        // Arrange
        String name = "Test Product";
        BigDecimal minPrice = new BigDecimal("25.50");
        BigDecimal maxPrice = new BigDecimal("75.99");
        Integer minQuantity = 10;
        Integer maxQuantity = 50;
        Boolean includeOutOfStock = false;

        // Act
        ProductQueryDTO dto = new ProductQueryDTO(
                name,
                minPrice,
                maxPrice,
                minQuantity,
                maxQuantity,
                includeOutOfStock
        );

        // Assert
        assertThat(dto.name()).isEqualTo(name);
        assertThat(dto.minPrice()).isEqualTo(minPrice);
        assertThat(dto.maxPrice()).isEqualTo(maxPrice);
        assertThat(dto.minQuantity()).isEqualTo(minQuantity);
        assertThat(dto.maxQuantity()).isEqualTo(maxQuantity);
        assertThat(dto.includeOutOfStock()).isEqualTo(includeOutOfStock);
        assertThat(dto.isPriceRangeValid()).isTrue();
        assertThat(dto.isQuantityRangeValid()).isTrue();
    }

    @Test
    public void givenAllNullValues_whenCreatingDTO_thenShouldHandleNullsCorrectly() {
        // Arrange
        
        // Act
        ProductQueryDTO dto = new ProductQueryDTO(null, null, null, null, null, null);

        // Assert
        assertThat(dto.name()).isNull();
        assertThat(dto.minPrice()).isNull();
        assertThat(dto.maxPrice()).isNull();
        assertThat(dto.minQuantity()).isNull();
        assertThat(dto.maxQuantity()).isNull();
        assertThat(dto.includeOutOfStock()).isTrue();
        assertThat(dto.isPriceRangeValid()).isTrue();
        assertThat(dto.isQuantityRangeValid()).isTrue();
    }
}