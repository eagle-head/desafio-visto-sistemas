package br.com.productmanagementsystem.controller;

import br.com.productmanagementsystem.dto.ProductRequestDTO;
import br.com.productmanagementsystem.dto.ProductResponseDTO;
import br.com.productmanagementsystem.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> findAll(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String name) {
        
        Page<ProductResponseDTO> products = name != null && !name.isEmpty() 
            ? productService.findByName(name, pageable)
            : productService.findAll(pageable);
            
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{publicId}")
    public ResponseEntity<ProductResponseDTO> findByPublicId(@PathVariable String publicId) {
        ProductResponseDTO product = productService.findByPublicId(publicId);
        return ResponseEntity.ok(product);
    }
    
    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO savedProduct = productService.save(productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }
    
    @PutMapping("/{publicId}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable String publicId, @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO updatedProduct = productService.update(publicId, productRequestDTO);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> delete(@PathVariable String publicId) {
        productService.delete(publicId);
        return ResponseEntity.noContent().build();
    }
}