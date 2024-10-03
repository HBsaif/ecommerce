package com.ecommerce.product.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.entities.Product;
import com.ecommerce.entities.ShoppingCart;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

	Optional<ShoppingCart> findByUserId(int userId);

}
