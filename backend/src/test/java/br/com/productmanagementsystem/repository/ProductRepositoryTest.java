package br.com.productmanagementsystem.repository;

import br.com.productmanagementsystem.entity.Product;
import br.com.productmanagementsystem.util.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        // Clean database before each test
        entityManager.getEntityManager().createQuery("DELETE FROM Product").executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void givenProductsWithDifferentNames_whenFindingByNameContainingIgnoreCase_thenShouldReturnMatchingProducts() {
        // Arrange
        Product smartphone = TestConstants.createDefaultProduct();
        smartphone.setPublicId("550e8400-e29b-41d4-a716-446655440001");
        smartphone.setId(null); // Let DB generate ID
        
        Product notebook = TestConstants.createNotebookProduct();
        notebook.setPublicId("550e8400-e29b-41d4-a716-446655440002");
        notebook.setId(null); // Let DB generate ID
        
        entityManager.persistAndFlush(smartphone);
        entityManager.persistAndFlush(notebook);
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Product> result = productRepository.findByNameContainingIgnoreCase("Samsung", pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo(TestConstants.SMARTPHONE_NAME);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    public void givenProductsWithDifferentNames_whenFindingByNameContainingIgnoreCaseWithLowercase_thenShouldReturnMatchingProducts() {
        // Arrange
        Product smartphone = TestConstants.createDefaultProduct();
        smartphone.setPublicId("550e8400-e29b-41d4-a716-446655440001");
        smartphone.setId(null);
        
        entityManager.persistAndFlush(smartphone);
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Product> result = productRepository.findByNameContainingIgnoreCase("samsung", pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo(TestConstants.SMARTPHONE_NAME);
    }

    @Test
    public void givenProducts_whenFindingByNameContainingIgnoreCaseWithNonExistentName_thenShouldReturnEmptyPage() {
        // Arrange
        Product smartphone = TestConstants.createDefaultProduct();
        smartphone.setPublicId("550e8400-e29b-41d4-a716-446655440001");
        smartphone.setId(null);
        
        entityManager.persistAndFlush(smartphone);
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Product> result = productRepository.findByNameContainingIgnoreCase("iPhone", pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    public void givenProductWithPublicId_whenFindingByPublicId_thenShouldReturnProduct() {
        // Arrange
        String publicId = "550e8400-e29b-41d4-a716-446655440001";
        Product product = TestConstants.createDefaultProduct();
        product.setPublicId(publicId);
        product.setId(null);
        
        entityManager.persistAndFlush(product);
        entityManager.clear();

        // Act
        Optional<Product> result = productRepository.findByPublicId(publicId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getPublicId()).isEqualTo(publicId);
        assertThat(result.get().getName()).isEqualTo(TestConstants.SMARTPHONE_NAME);
        assertThat(result.get().getPrice()).isEqualTo(TestConstants.SMARTPHONE_PRICE);
        assertThat(result.get().getDescription()).isEqualTo(TestConstants.SMARTPHONE_DESCRIPTION);
        assertThat(result.get().getQuantity()).isEqualTo(TestConstants.SMARTPHONE_QUANTITY);
    }

    @Test
    public void givenNoProduct_whenFindingByPublicId_thenShouldReturnEmpty() {
        // Arrange
        String nonExistentPublicId = TestConstants.NON_EXISTENT_PUBLIC_ID;

        // Act
        Optional<Product> result = productRepository.findByPublicId(nonExistentPublicId);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    public void givenProducts_whenFindingAllWithSpecification_thenShouldReturnAllProducts() {
        // Arrange
        Product smartphone = TestConstants.createDefaultProduct();
        smartphone.setPublicId("550e8400-e29b-41d4-a716-446655440001");
        smartphone.setId(null);
        
        Product notebook = TestConstants.createNotebookProduct();
        notebook.setPublicId("550e8400-e29b-41d4-a716-446655440002");
        notebook.setId(null);
        
        entityManager.persistAndFlush(smartphone);
        entityManager.persistAndFlush(notebook);
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);
        Specification<Product> spec = (root, query, criteriaBuilder) -> null;

        // Act
        Page<Product> result = productRepository.findAll(spec, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    public void givenProducts_whenFindingAllWithNameSpecification_thenShouldReturnFilteredProducts() {
        // Arrange
        Product smartphone = TestConstants.createDefaultProduct();
        smartphone.setPublicId("550e8400-e29b-41d4-a716-446655440001");
        smartphone.setId(null);
        
        Product notebook = TestConstants.createNotebookProduct();
        notebook.setPublicId("550e8400-e29b-41d4-a716-446655440002");
        notebook.setId(null);
        
        entityManager.persistAndFlush(smartphone);
        entityManager.persistAndFlush(notebook);
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);
        Specification<Product> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%samsung%");

        // Act
        Page<Product> result = productRepository.findAll(spec, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).contains("Samsung");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void givenProducts_whenFindingAllWithPriceRangeSpecification_thenShouldReturnFilteredProducts() {
        // Arrange
        Product smartphone = TestConstants.createDefaultProduct();
        smartphone.setPublicId("550e8400-e29b-41d4-a716-446655440001");
        smartphone.setId(null);
        
        Product notebook = TestConstants.createNotebookProduct();
        notebook.setPublicId("550e8400-e29b-41d4-a716-446655440002");
        notebook.setId(null);
        
        entityManager.persistAndFlush(smartphone);
        entityManager.persistAndFlush(notebook);
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);
        BigDecimal minPrice = new BigDecimal("1000.00");
        BigDecimal maxPrice = new BigDecimal("2000.00");
        
        Specification<Product> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice),
                        criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice)
                );

        // Act
        Page<Product> result = productRepository.findAll(spec, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getPrice()).isBetween(minPrice, maxPrice);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void givenProduct_whenSaving_thenShouldGeneratePublicIdAutomatically() {
        // Arrange
        Product product = new Product();
        product.setName(TestConstants.SMARTPHONE_NAME);
        product.setPrice(TestConstants.SMARTPHONE_PRICE);
        product.setDescription(TestConstants.SMARTPHONE_DESCRIPTION);
        product.setQuantity(TestConstants.SMARTPHONE_QUANTITY);

        // Act
        Product savedProduct = productRepository.save(product);

        // Assert
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getPublicId()).isNotNull();
        assertThat(savedProduct.getPublicId()).matches(TestConstants.UUID_REGEX);
        assertThat(savedProduct.getName()).isEqualTo(TestConstants.SMARTPHONE_NAME);
        assertThat(savedProduct.getPrice()).isEqualTo(TestConstants.SMARTPHONE_PRICE);
        assertThat(savedProduct.getDescription()).isEqualTo(TestConstants.SMARTPHONE_DESCRIPTION);
        assertThat(savedProduct.getQuantity()).isEqualTo(TestConstants.SMARTPHONE_QUANTITY);
    }

    @Test
    public void givenProductWithPublicId_whenSaving_thenShouldNotOverrideExistingPublicId() {
        // Arrange
        String existingPublicId = "550e8400-e29b-41d4-a716-446655440001";
        Product product = new Product();
        product.setPublicId(existingPublicId);
        product.setName(TestConstants.SMARTPHONE_NAME);
        product.setPrice(TestConstants.SMARTPHONE_PRICE);
        product.setDescription(TestConstants.SMARTPHONE_DESCRIPTION);
        product.setQuantity(TestConstants.SMARTPHONE_QUANTITY);

        // Act
        Product savedProduct = productRepository.save(product);

        // Assert
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getPublicId()).isEqualTo(existingPublicId);
    }

    @Test
    public void givenMultipleProducts_whenFindingAllWithPagination_thenShouldReturnPagedResults() {
        // Arrange
        List<Product> products = List.of(
                createProductWithName("Product A"),
                createProductWithName("Product B"), 
                createProductWithName("Product C"),
                createProductWithName("Product D"),
                createProductWithName("Product E")
        );
        
        products.forEach(product -> {
            product.setId(null);
            entityManager.persistAndFlush(product);
        });
        entityManager.clear();

        Pageable firstPage = PageRequest.of(0, 2);
        Pageable secondPage = PageRequest.of(1, 2);

        // Act
        Page<Product> firstPageResult = productRepository.findAll(firstPage);
        Page<Product> secondPageResult = productRepository.findAll(secondPage);

        // Assert
        assertThat(firstPageResult.getContent()).hasSize(2);
        assertThat(firstPageResult.getTotalElements()).isEqualTo(5);
        assertThat(firstPageResult.getTotalPages()).isEqualTo(3);
        assertThat(firstPageResult.isFirst()).isTrue();
        assertThat(firstPageResult.hasNext()).isTrue();

        assertThat(secondPageResult.getContent()).hasSize(2);
        assertThat(secondPageResult.getTotalElements()).isEqualTo(5);
        assertThat(secondPageResult.getTotalPages()).isEqualTo(3);
        assertThat(secondPageResult.isFirst()).isFalse();
        assertThat(secondPageResult.hasNext()).isTrue();
    }

    @Test
    public void givenProductWithDuplicatePublicId_whenSaving_thenShouldThrowDataIntegrityViolationException() {
        // Arrange
        String duplicatePublicId = "550e8400-e29b-41d4-a716-446655440001";
        
        Product firstProduct = createProductWithName("First Product");
        firstProduct.setPublicId(duplicatePublicId);
        entityManager.persistAndFlush(firstProduct);
        entityManager.clear();

        Product secondProduct = createProductWithName("Second Product");
        secondProduct.setPublicId(duplicatePublicId); // Same publicId

        // Act & Assert
        assertThatThrownBy(() -> {
            productRepository.save(secondProduct);
            entityManager.flush(); // Force database constraint check
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void givenProductWithNullName_whenSaving_thenShouldThrowDataIntegrityViolationException() {
        // Arrange
        Product product = new Product();
        product.setName(null); // Violates NOT NULL constraint
        product.setPrice(TestConstants.SMARTPHONE_PRICE);
        product.setDescription(TestConstants.SMARTPHONE_DESCRIPTION);
        product.setQuantity(TestConstants.SMARTPHONE_QUANTITY);

        // Act & Assert
        assertThatThrownBy(() -> {
            productRepository.save(product);
            entityManager.flush(); // Force database constraint check
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void givenProductWithNullPrice_whenSaving_thenShouldThrowDataIntegrityViolationException() {
        // Arrange
        Product product = new Product();
        product.setName(TestConstants.SMARTPHONE_NAME);
        product.setPrice(null); // Violates NOT NULL constraint
        product.setDescription(TestConstants.SMARTPHONE_DESCRIPTION);
        product.setQuantity(TestConstants.SMARTPHONE_QUANTITY);

        // Act & Assert
        assertThatThrownBy(() -> {
            productRepository.save(product);
            entityManager.flush(); // Force database constraint check
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void givenProductWithNullQuantity_whenSaving_thenShouldThrowDataIntegrityViolationException() {
        // Arrange
        Product product = new Product();
        product.setName(TestConstants.SMARTPHONE_NAME);
        product.setPrice(TestConstants.SMARTPHONE_PRICE);
        product.setDescription(TestConstants.SMARTPHONE_DESCRIPTION);
        product.setQuantity(null); // Violates NOT NULL constraint

        // Act & Assert
        assertThatThrownBy(() -> {
            productRepository.save(product);
            entityManager.flush(); // Force database constraint check
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void givenProductWithTooLongName_whenSaving_thenShouldThrowDataIntegrityViolationException() {
        // Arrange
        String tooLongName = "A".repeat(101); // Exceeds VARCHAR(100) limit
        Product product = new Product();
        product.setName(tooLongName);
        product.setPrice(TestConstants.SMARTPHONE_PRICE);
        product.setDescription(TestConstants.SMARTPHONE_DESCRIPTION);
        product.setQuantity(TestConstants.SMARTPHONE_QUANTITY);

        // Act & Assert
        assertThatThrownBy(() -> {
            productRepository.save(product);
            entityManager.flush(); // Force database constraint check
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void givenProductWithTooLongDescription_whenSaving_thenShouldThrowDataIntegrityViolationException() {
        // Arrange
        String tooLongDescription = "A".repeat(501); // Exceeds VARCHAR(500) limit
        Product product = new Product();
        product.setName(TestConstants.SMARTPHONE_NAME);
        product.setPrice(TestConstants.SMARTPHONE_PRICE);
        product.setDescription(tooLongDescription);
        product.setQuantity(TestConstants.SMARTPHONE_QUANTITY);

        // Act & Assert
        assertThatThrownBy(() -> {
            productRepository.save(product);
            entityManager.flush(); // Force database constraint check
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void givenProductWithTooLongPublicId_whenSaving_thenShouldThrowDataIntegrityViolationException() {
        // Arrange
        String tooLongPublicId = "A".repeat(37); // Exceeds VARCHAR(36) limit
        Product product = new Product();
        product.setPublicId(tooLongPublicId);
        product.setName(TestConstants.SMARTPHONE_NAME);
        product.setPrice(TestConstants.SMARTPHONE_PRICE);
        product.setDescription(TestConstants.SMARTPHONE_DESCRIPTION);
        product.setQuantity(TestConstants.SMARTPHONE_QUANTITY);

        // Act & Assert
        assertThatThrownBy(() -> {
            productRepository.save(product);
            entityManager.flush(); // Force database constraint check
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    private Product createProductWithName(String name) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(TestConstants.SMARTPHONE_PRICE);
        product.setDescription(TestConstants.SMARTPHONE_DESCRIPTION);
        product.setQuantity(TestConstants.SMARTPHONE_QUANTITY);
        return product;
    }
}