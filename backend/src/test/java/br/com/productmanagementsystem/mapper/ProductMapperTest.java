package br.com.productmanagementsystem.mapper;

import br.com.productmanagementsystem.dto.ProductRequestDTO;
import br.com.productmanagementsystem.dto.ProductResponseDTO;
import br.com.productmanagementsystem.entity.Product;
import br.com.productmanagementsystem.service.MessageService;
import br.com.productmanagementsystem.util.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductMapperTest {

    private ProductMapper productMapper;
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        this.messageService = mock(MessageService.class);
        this.productMapper = new ProductMapper(messageService);
    }

    @Test
    public void givenProduct_whenConvertingToResponseDTO_thenShouldReturnMappedDTO() {
        // Arrange
        Product product = TestConstants.createDefaultProduct();

        // Act
        ProductResponseDTO responseDTO = this.productMapper.toResponseDTO(product);

        // Assert
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.publicId()).isEqualTo(TestConstants.DEFAULT_PUBLIC_ID);
        assertThat(responseDTO.name()).isEqualTo(TestConstants.SMARTPHONE_NAME);
        assertThat(responseDTO.price()).isEqualTo(TestConstants.SMARTPHONE_PRICE);
        assertThat(responseDTO.description()).isEqualTo(TestConstants.SMARTPHONE_DESCRIPTION);
        assertThat(responseDTO.quantity()).isEqualTo(TestConstants.SMARTPHONE_QUANTITY);
    }

    @Test
    public void givenProductWithNullDescription_whenConvertingToResponseDTO_thenShouldHandleNullDescription() {
        // Arrange
        Product product = TestConstants.createProductWithoutDescription();

        // Act
        ProductResponseDTO responseDTO = this.productMapper.toResponseDTO(product);

        // Assert
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.publicId()).isEqualTo(TestConstants.DEFAULT_PUBLIC_ID);
        assertThat(responseDTO.name()).isEqualTo(TestConstants.MINIMAL_NAME);
        assertThat(responseDTO.price()).isEqualTo(TestConstants.MINIMAL_PRICE);
        assertThat(responseDTO.description()).isNull();
        assertThat(responseDTO.quantity()).isEqualTo(TestConstants.MINIMAL_QUANTITY);
    }

    @Test
    public void givenNullProduct_whenConvertingToResponseDTO_thenShouldThrowException() {
        // Arrange
        when(messageService.getMessage("mapper.product.null")).thenReturn("Product cannot be null");

        // Act & Assert
        assertThatThrownBy(() -> this.productMapper.toResponseDTO(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product cannot be null");
    }

    @Test
    public void givenProductRequestDTO_whenConvertingToEntity_thenShouldReturnMappedEntity() {
        // Arrange
        ProductRequestDTO requestDTO = TestConstants.createDefaultProductRequestDTO();

        // Act
        Product product = this.productMapper.toEntity(requestDTO);

        // Assert
        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo(TestConstants.SMARTPHONE_NAME);
        assertThat(product.getPrice()).isEqualTo(TestConstants.SMARTPHONE_PRICE);
        assertThat(product.getDescription()).isEqualTo(TestConstants.SMARTPHONE_DESCRIPTION);
        assertThat(product.getQuantity()).isEqualTo(TestConstants.SMARTPHONE_QUANTITY);
        assertThat(product.getId()).isNull();
        assertThat(product.getPublicId()).isNull();
    }

    @Test
    public void givenNullProductRequestDTO_whenConvertingToEntity_thenShouldThrowException() {
        // Arrange
        when(messageService.getMessage("mapper.product.request.dto.null")).thenReturn("ProductRequestDTO cannot be null");

        // Act & Assert
        assertThatThrownBy(() -> this.productMapper.toEntity(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ProductRequestDTO cannot be null");
    }

    @Test
    public void givenProductAndRequestDTO_whenUpdatingEntityFromDTO_thenShouldUpdateAllFields() {
        // Arrange
        Product product = TestConstants.createDefaultProduct();
        ProductRequestDTO requestDTO = TestConstants.createUpdatedProductRequestDTO();

        // Act
        this.productMapper.updateEntityFromDTO(product, requestDTO);

        // Assert
        assertThat(product.getName()).isEqualTo(TestConstants.UPDATED_NAME);
        assertThat(product.getPrice()).isEqualTo(TestConstants.UPDATED_PRICE);
        assertThat(product.getDescription()).isEqualTo(TestConstants.UPDATED_DESCRIPTION);
        assertThat(product.getQuantity()).isEqualTo(TestConstants.UPDATED_QUANTITY);
        assertThat(product.getId()).isEqualTo(TestConstants.DEFAULT_ID);
        assertThat(product.getPublicId()).isEqualTo(TestConstants.DEFAULT_PUBLIC_ID);
    }

    @Test
    public void givenNullProduct_whenUpdatingEntityFromDTO_thenShouldThrowException() {
        // Arrange
        ProductRequestDTO requestDTO = TestConstants.createUpdatedProductRequestDTO();
        when(messageService.getMessage("mapper.product.null")).thenReturn("Product cannot be null");

        // Act & Assert
        assertThatThrownBy(() -> this.productMapper.updateEntityFromDTO(null, requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product cannot be null");
    }

    @Test
    public void givenNullRequestDTO_whenUpdatingEntityFromDTO_thenShouldThrowException() {
        // Arrange
        Product product = TestConstants.createDefaultProduct();
        when(messageService.getMessage("mapper.product.request.dto.null")).thenReturn("ProductRequestDTO cannot be null");

        // Act & Assert
        assertThatThrownBy(() -> this.productMapper.updateEntityFromDTO(product, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ProductRequestDTO cannot be null");
    }
}