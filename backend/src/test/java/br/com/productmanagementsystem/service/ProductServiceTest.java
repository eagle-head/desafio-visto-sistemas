package br.com.productmanagementsystem.service;

import br.com.productmanagementsystem.dto.ProductQueryDTO;
import br.com.productmanagementsystem.dto.ProductRequestDTO;
import br.com.productmanagementsystem.dto.ProductResponseDTO;
import br.com.productmanagementsystem.entity.Product;
import br.com.productmanagementsystem.exception.ResourceNotFoundException;
import br.com.productmanagementsystem.mapper.ProductMapper;
import br.com.productmanagementsystem.repository.ProductRepository;
import br.com.productmanagementsystem.util.TestConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private ProductMapper productMapper;
    
    @Mock
    private MessageService messageService;

    @InjectMocks
    private ProductService productService;

    @Test
    public void givenPageable_whenFindingAll_thenShouldReturnPageOfProductResponseDTO() {
        // Arrange
        Product product = TestConstants.createDefaultProduct();
        ProductResponseDTO responseDTO = new ProductResponseDTO(
                TestConstants.DEFAULT_PUBLIC_ID,
                TestConstants.SMARTPHONE_NAME,
                TestConstants.SMARTPHONE_PRICE,
                TestConstants.SMARTPHONE_DESCRIPTION,
                TestConstants.SMARTPHONE_QUANTITY
        );
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);
        
        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);
        when(productMapper.toResponseDTO(product)).thenReturn(responseDTO);

        // Act
        Page<ProductResponseDTO> result = productService.findAll(new ProductQueryDTO(null, null, null, null, null, null), pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(responseDTO);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    public void givenValidPublicId_whenFindingByPublicId_thenShouldReturnProductResponseDTO() {
        // Arrange
        Product product = TestConstants.createDefaultProduct();
        ProductResponseDTO responseDTO = new ProductResponseDTO(
                TestConstants.DEFAULT_PUBLIC_ID,
                TestConstants.SMARTPHONE_NAME,
                TestConstants.SMARTPHONE_PRICE,
                TestConstants.SMARTPHONE_DESCRIPTION,
                TestConstants.SMARTPHONE_QUANTITY
        );
        
        when(productRepository.findByPublicId(TestConstants.DEFAULT_PUBLIC_ID)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(responseDTO);

        // Act
        ProductResponseDTO result = productService.findByPublicId(TestConstants.DEFAULT_PUBLIC_ID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(responseDTO);
    }

    @Test
    public void givenInvalidPublicId_whenFindingByPublicId_thenShouldThrowResourceNotFoundException() {
        // Arrange
        String invalidPublicId = TestConstants.NON_EXISTENT_PUBLIC_ID;
        String errorMessage = "Resource not found with public ID: " + invalidPublicId;
        
        when(productRepository.findByPublicId(invalidPublicId)).thenReturn(Optional.empty());
        when(messageService.getMessage("resource.not.found.by.public.id", invalidPublicId)).thenReturn(errorMessage);

        // Act & Assert
        assertThatThrownBy(() -> productService.findByPublicId(invalidPublicId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(errorMessage);
    }

    @Test
    public void givenProductQueryDTOAndPageable_whenFindingAll_thenShouldReturnFilteredPageOfProductResponseDTO() {
        // Arrange
        ProductQueryDTO queryParams = new ProductQueryDTO("Samsung", null, null, null, null, true);
        Product product = TestConstants.createDefaultProduct();
        ProductResponseDTO responseDTO = new ProductResponseDTO(
                TestConstants.DEFAULT_PUBLIC_ID,
                TestConstants.SMARTPHONE_NAME,
                TestConstants.SMARTPHONE_PRICE,
                TestConstants.SMARTPHONE_DESCRIPTION,
                TestConstants.SMARTPHONE_QUANTITY
        );
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);
        
        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);
        when(productMapper.toResponseDTO(product)).thenReturn(responseDTO);

        // Act
        Page<ProductResponseDTO> result = productService.findAll(queryParams, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(responseDTO);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    public void givenValidProductRequestDTO_whenSaving_thenShouldReturnProductResponseDTO() {
        // Arrange
        ProductRequestDTO requestDTO = TestConstants.createDefaultProductRequestDTO();
        Product newProduct = TestConstants.createDefaultProduct();
        Product savedProduct = TestConstants.createDefaultProduct();
        ProductResponseDTO responseDTO = TestConstants.createDefaultProductResponseDTO();

        when(productMapper.toEntity(requestDTO)).thenReturn(newProduct);
        when(productRepository.save(newProduct)).thenReturn(savedProduct);
        when(productMapper.toResponseDTO(savedProduct)).thenReturn(responseDTO);

        // Act
        ProductResponseDTO result = productService.save(requestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(responseDTO);
    }

    @Test
    public void givenValidPublicIdAndProductRequestDTO_whenUpdating_thenShouldReturnUpdatedProductResponseDTO() {
        // Arrange
        String publicId = TestConstants.DEFAULT_PUBLIC_ID;
        ProductRequestDTO requestDTO = TestConstants.createUpdatedProductRequestDTO();
        Product existingProduct = TestConstants.createDefaultProduct();
        Product updatedProduct = TestConstants.createDefaultProduct();
        ProductResponseDTO responseDTO = new ProductResponseDTO(
                TestConstants.DEFAULT_PUBLIC_ID,
                TestConstants.UPDATED_NAME,
                TestConstants.UPDATED_PRICE,
                TestConstants.UPDATED_DESCRIPTION,
                TestConstants.UPDATED_QUANTITY
        );

        when(productRepository.findByPublicId(publicId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
        when(productMapper.toResponseDTO(updatedProduct)).thenReturn(responseDTO);

        // Act
        ProductResponseDTO result = productService.update(publicId, requestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(responseDTO);
    }

    @Test
    public void givenInvalidPublicIdAndProductRequestDTO_whenUpdating_thenShouldThrowResourceNotFoundException() {
        // Arrange
        String invalidPublicId = TestConstants.NON_EXISTENT_PUBLIC_ID;
        ProductRequestDTO requestDTO = TestConstants.createUpdatedProductRequestDTO();
        String errorMessage = "Resource not found with public ID: " + invalidPublicId;

        when(productRepository.findByPublicId(invalidPublicId)).thenReturn(Optional.empty());
        when(messageService.getMessage("resource.not.found.by.public.id", invalidPublicId)).thenReturn(errorMessage);

        // Act & Assert
        assertThatThrownBy(() -> productService.update(invalidPublicId, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(errorMessage);
    }

    @Test
    public void givenValidPublicId_whenDeleting_thenShouldDeleteProduct() {
        // Arrange
        String publicId = TestConstants.DEFAULT_PUBLIC_ID;
        Product existingProduct = TestConstants.createDefaultProduct();

        when(productRepository.findByPublicId(publicId)).thenReturn(Optional.of(existingProduct));

        // Act
        productService.delete(publicId);

        // Assert
        verify(productRepository).delete(existingProduct);
    }
}