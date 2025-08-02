package br.com.productmanagementsystem.specification;

import br.com.productmanagementsystem.dto.ProductQueryDTO;
import br.com.productmanagementsystem.entity.Product;
import br.com.productmanagementsystem.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductSpecificationTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        productRepository.deleteAll();

        // Create test data
        Product product1 = new Product(null, null, "Spring Boot Book", new BigDecimal("45.99"), "Programming book", 10);
        Product product2 = new Product(null, null, "Java Performance", new BigDecimal("54.99"), "Performance guide", 5);
        Product product3 = new Product(null, null, "Spring Data Guide", new BigDecimal("39.99"), "Data access book", 0);
        Product product4 = new Product(null, null, "Microservices Book", new BigDecimal("59.99"), "Architecture book", 15);

        productRepository.saveAll(Arrays.asList(product1, product2, product3, product4));
    }


    @ParameterizedTest
    @MethodSource("provideQueryParameters")
    public void givenVariousQueryParameters_whenBuildingSpecification_thenShouldReturnExpectedResults(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minQuantity,
            Integer maxQuantity,
            Boolean includeOutOfStock,
            int expectedResults) {
        // Given
        ProductQueryDTO queryDTO = new ProductQueryDTO(name, minPrice, maxPrice, minQuantity, maxQuantity, includeOutOfStock);

        // When
        Specification<Product> specification = ProductSpecification.buildSpecification(queryDTO);
        Page<Product> results = productRepository.findAll(specification, PageRequest.of(0, 10));

        // Then
        assertThat(results.getContent()).hasSize(expectedResults);
    }

    public static Stream<Arguments> provideQueryParameters() {
        return Stream.of(
                Arguments.of("Spring", new BigDecimal("40.00"), new BigDecimal("50.00"), 1, 20, Boolean.FALSE, 1),
                Arguments.of("Spring", null, null, null, null, Boolean.TRUE, 2),
                Arguments.of("   ", null, null, null, null, Boolean.TRUE, 4),
                Arguments.of(null, new BigDecimal("50.00"), null, null, null, Boolean.TRUE, 2),
                Arguments.of(null, null, new BigDecimal("50.00"), null, null, Boolean.TRUE, 2),
                Arguments.of(null, null, null, 10, null, Boolean.TRUE, 2),
                Arguments.of(null, null, null, null, 10, Boolean.TRUE, 3)
        );
    }
}