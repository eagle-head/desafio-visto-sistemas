package br.com.productmanagementsystem.mapper;

import br.com.productmanagementsystem.dto.ProductRequestDTO;
import br.com.productmanagementsystem.dto.ProductResponseDTO;
import br.com.productmanagementsystem.entity.Product;
import br.com.productmanagementsystem.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    
    private final MessageService messageService;

    public ProductResponseDTO toResponseDTO(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(messageService.getMessage("mapper.product.null"));
        }
        
        return new ProductResponseDTO(
                product.getPublicId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getQuantity()
        );
    }

    public Product toEntity(ProductRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException(messageService.getMessage("mapper.product.request.dto.null"));
        }
        
        Product product = new Product();
        product.setName(requestDTO.name());
        product.setPrice(requestDTO.price());
        product.setDescription(requestDTO.description());
        product.setQuantity(requestDTO.quantity());

        return product;
    }

    public void updateEntityFromDTO(Product product, ProductRequestDTO requestDTO) {
        if (product == null) {
            throw new IllegalArgumentException(messageService.getMessage("mapper.product.null"));
        }

        if (requestDTO == null) {
            throw new IllegalArgumentException(messageService.getMessage("mapper.product.request.dto.null"));
        }
        
        product.setName(requestDTO.name());
        product.setPrice(requestDTO.price());
        product.setDescription(requestDTO.description());
        product.setQuantity(requestDTO.quantity());
    }
}