package com.ecommerce.product.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.entities.CartItem;
import com.ecommerce.entities.Product;
import com.ecommerce.entities.ShoppingCart;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	Optional<Product> findByCartAndProduct(ShoppingCart cart, Product product);

	Optional<CartItem> findByIdAndCartId(int itemId, Long id);

}
