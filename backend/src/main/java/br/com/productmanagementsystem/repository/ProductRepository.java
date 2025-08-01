package br.com.productmanagementsystem.repository;

import br.com.productmanagementsystem.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    Optional<Product> findByPublicId(String publicId);
    
    boolean existsByPublicId(String publicId);
}