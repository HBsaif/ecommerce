package com.ecommerce.product.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import com.ecommerce.entities.Product;
import com.ecommerce.product.dtos.ProductResponse;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    @Query(value = "SELECT p FROM Product p ORDER BY p.createdAt DESC LIMIT :n")
    List<Product> findTopNProducts(@Param("n")int n);
}
