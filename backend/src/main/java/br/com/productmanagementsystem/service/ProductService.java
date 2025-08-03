package br.com.productmanagementsystem.service;

import br.com.productmanagementsystem.dto.ProductQueryDTO;
import br.com.productmanagementsystem.dto.ProductRequestDTO;
import br.com.productmanagementsystem.dto.ProductResponseDTO;
import br.com.productmanagementsystem.entity.Product;
import br.com.productmanagementsystem.exception.ProductAlreadyExistsException;
import br.com.productmanagementsystem.exception.ResourceNotFoundException;
import br.com.productmanagementsystem.mapper.ProductMapper;
import br.com.productmanagementsystem.repository.ProductRepository;
import br.com.productmanagementsystem.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final MessageService messageService;

    public Page<ProductResponseDTO> findAll(ProductQueryDTO queryParams, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.buildSpecification(queryParams);
        return this.productRepository.findAll(spec, pageable).map(productMapper::toResponseDTO);
    }

    public ProductResponseDTO findByPublicId(String publicId) {
        Product product = this.productRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        this.messageService.getMessage("resource.not.found.by.public.id", publicId))
                );

        return this.productMapper.toResponseDTO(product);
    }

    public ProductResponseDTO save(ProductRequestDTO requestDTO) {
        // Check if product with same name already exists
        if (this.productRepository.existsByName(requestDTO.name())) {
            throw new ProductAlreadyExistsException(requestDTO.name());
        }
        
        Product product = this.productMapper.toEntity(requestDTO);
        Product savedProduct = this.productRepository.save(product);
        return this.productMapper.toResponseDTO(savedProduct);
    }

    public ProductResponseDTO update(String publicId, ProductRequestDTO requestDTO) {
        Product existingProduct = this.productRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        this.messageService.getMessage("resource.not.found.by.public.id", publicId))
                );

        this.productMapper.updateEntityFromDTO(existingProduct, requestDTO);
        Product updatedProduct = this.productRepository.save(existingProduct);

        return this.productMapper.toResponseDTO(updatedProduct);
    }

    public void delete(String publicId) {
        this.productRepository.findByPublicId(publicId).ifPresent(productRepository::delete);
    }
}