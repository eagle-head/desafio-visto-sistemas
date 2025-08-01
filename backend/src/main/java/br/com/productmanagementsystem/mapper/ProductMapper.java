package br.com.productmanagementsystem.mapper;

import br.com.productmanagementsystem.dto.ProductRequestDTO;
import br.com.productmanagementsystem.dto.ProductResponseDTO;
import br.com.productmanagementsystem.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponseDTO toResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getPublicId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getQuantity()
        );
    }

    public Product toEntity(ProductRequestDTO requestDTO) {
        Product product = new Product();
        product.setName(requestDTO.name());
        product.setPrice(requestDTO.price());
        product.setDescription(requestDTO.description());
        product.setQuantity(requestDTO.quantity());

        return product;
    }

    public void updateEntityFromDTO(Product product, ProductRequestDTO requestDTO) {
        product.setName(requestDTO.name());
        product.setPrice(requestDTO.price());
        product.setDescription(requestDTO.description());
        product.setQuantity(requestDTO.quantity());
    }
}