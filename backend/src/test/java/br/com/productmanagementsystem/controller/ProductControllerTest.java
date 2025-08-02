package br.com.productmanagementsystem.controller;

import br.com.productmanagementsystem.dto.ProductQueryDTO;
import br.com.productmanagementsystem.dto.ProductRequestDTO;
import br.com.productmanagementsystem.dto.ProductResponseDTO;
import br.com.productmanagementsystem.service.ProductService;
import br.com.productmanagementsystem.util.TestConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static br.com.productmanagementsystem.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
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
}