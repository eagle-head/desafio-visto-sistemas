package br.com.productmanagementsystem.controller;

import br.com.productmanagementsystem.dto.ProductQueryDTO;
import br.com.productmanagementsystem.dto.ProductRequestDTO;
import br.com.productmanagementsystem.dto.ProductResponseDTO;
import br.com.productmanagementsystem.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Validated
@Tag(name = "Products", description = "CRUD operations for product management")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(
            summary = "List all products",
            description = "Returns a paginated list of products with advanced filtering options including name, price range, quantity range, and stock status."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Products list retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or filter parameters",
                    content = @Content
            )
    })
    public ResponseEntity<Page<ProductResponseDTO>> findAll(
            @Parameter(
                    description = "Pagination and sorting configuration",
                    example = "size=10&sort=name,asc"
            )
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @Valid @ModelAttribute ProductQueryDTO queryParams) {

        Page<ProductResponseDTO> products = productService.findAll(queryParams, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{publicId}")
    @Operation(
            summary = "Find product by ID",
            description = "Returns a specific product based on its public ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    public ResponseEntity<ProductResponseDTO> findByPublicId(
            @Parameter(
                    description = "Product public ID",
                    example = "abc123def456",
                    required = true
            )
            @PathVariable String publicId) {
        ProductResponseDTO product = productService.findByPublicId(publicId);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @Operation(
            summary = "Create new product",
            description = "Creates a new product in the system with the provided data."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data provided",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Product with the same name already exists",
                    content = @Content
            )
    })
    public ResponseEntity<ProductResponseDTO> create(
            @Parameter(
                    description = "Product data to be created",
                    required = true
            )
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO savedProduct = productService.save(productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PutMapping("/{publicId}")
    @Operation(
            summary = "Update product",
            description = "Updates an existing product with the provided new data."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data provided",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Product with the same name already exists",
                    content = @Content
            )
    })
    public ResponseEntity<ProductResponseDTO> update(
            @Parameter(
                    description = "Public ID of the product to be updated",
                    example = "abc123def456",
                    required = true
            )
            @PathVariable String publicId,
            @Parameter(
                    description = "New product data",
                    required = true
            )
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO updatedProduct = productService.update(publicId, productRequestDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{publicId}")
    @Operation(
            summary = "Delete product",
            description = "Removes a product from the system based on its public ID. Always returns 204 regardless of whether the product exists."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Request processed successfully (product deleted or did not exist)",
                    content = @Content
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "Public ID of the product to be deleted",
                    example = "abc123def456",
                    required = true
            )
            @PathVariable String publicId) {
        productService.delete(publicId);
        return ResponseEntity.noContent().build();
    }
}