package br.com.productmanagementsystem.service;

import br.com.productmanagementsystem.dto.ProductRequestDTO;
import br.com.productmanagementsystem.dto.ProductResponseDTO;
import br.com.productmanagementsystem.entity.Product;
import br.com.productmanagementsystem.exception.ResourceNotFoundException;
import br.com.productmanagementsystem.mapper.ProductMapper;
import br.com.productmanagementsystem.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final MessageService messageService;
    
    public Page<ProductResponseDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toResponseDTO);
    }
    
    public ProductResponseDTO findByPublicId(String publicId) {
        Product product = productRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("resource.not.found.by.public.id", publicId)));
        return productMapper.toResponseDTO(product);
    }
    
    public ProductResponseDTO save(ProductRequestDTO requestDTO) {
        Product product = productMapper.toEntity(requestDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }
    
    public ProductResponseDTO update(String publicId, ProductRequestDTO requestDTO) {
        Product existingProduct = productRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("resource.not.found.by.public.id", publicId)));
        
        productMapper.updateEntityFromDTO(existingProduct, requestDTO);
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponseDTO(updatedProduct);
    }
    
    public void delete(String publicId) {
        productRepository.findByPublicId(publicId)
                .ifPresent(productRepository::delete);
    }
    
    public Page<ProductResponseDTO> findByName(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable).map(productMapper::toResponseDTO);
    }
}