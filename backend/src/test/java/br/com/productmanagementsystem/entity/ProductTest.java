package br.com.productmanagementsystem.entity;

import br.com.productmanagementsystem.util.TestConstants;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTest {

    @Test
    public void givenProductWithNullPublicId_whenGeneratingPublicId_thenShouldCreateNewUUID() {
        // Arrange
        Product product = new Product();
        
        // Act
        product.generatePublicId();
        
        // Assert
        assertThat(product.getPublicId()).isNotNull();
        assertThat(product.getPublicId()).hasSize(36);
        assertThat(product.getPublicId()).matches(TestConstants.UUID_REGEX);
    }

    @Test
    public void givenProductWithExistingPublicId_whenGeneratingPublicId_thenShouldNotOverwriteExistingId() {
        // Arrange
        Product product = new Product();
        product.setPublicId(TestConstants.DEFAULT_PUBLIC_ID);
        
        // Act
        product.generatePublicId();
        
        // Assert
        assertThat(product.getPublicId()).isEqualTo(TestConstants.DEFAULT_PUBLIC_ID);
    }
}