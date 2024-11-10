package com.ecommerce.product.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.constants.StatusMessage;
import com.ecommerce.dtos.ApiResponse;
import com.ecommerce.entities.CartItem;
import com.ecommerce.entities.ShoppingCart;
import com.ecommerce.product.dtos.AddCartItemRequest;
import com.ecommerce.product.dtos.CartItemDTO;
import com.ecommerce.product.dtos.ShoppingCartDTO;
import com.ecommerce.product.dtos.UpdateItemQuantityRequest;
import com.ecommerce.product.services.ShoppingCartService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("${version}/api/cart")
@Slf4j
public class CartController {
	@Autowired
	private Gson gson;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add-item")
    public ResponseEntity<ApiResponse<ShoppingCart>> addItemToCart(@RequestBody AddCartItemRequest request) throws Exception {
    	ShoppingCart cart = shoppingCartService.addItemToCart(request.getUserId(), request.getProductId(), request.getQuantity());
        
        ApiResponse<ShoppingCart> response = new ApiResponse<>(
                StatusMessage.SUCCESS.toString(), 
                "Item added to cart successfully.", 
                cart
        );
        
        log.info("Response: {}", gson.toJson(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/update-item")
    public ResponseEntity<ApiResponse<CartItem>> updateCartItem(@RequestBody UpdateItemQuantityRequest request) throws Exception {
		
		CartItem cart = shoppingCartService.updateCartItem(request.getUserId(), request.getItemId(), request.getQuantity());
		return ResponseEntity.ok(new ApiResponse<CartItem>(StatusMessage.SUCCESS.toString(), "Item quantity updated", cart));
        
    }
//
//    @DeleteMapping("/items/{productId}")
//    public ResponseEntity<?> removeItemFromCart(
//            @RequestParam Long userId,
//            @PathVariable Long productId) {
//
//        shoppingCartService.removeItemFromCart(userId, productId);
//        return ResponseEntity.ok().body(Map.of("status", "SUCCESS", "message", "Item removed from cart"));
//    }
//
    @GetMapping
    public ResponseEntity<ApiResponse<ShoppingCartDTO>> getCart(@RequestParam int userId) {
    	ShoppingCartDTO cart = shoppingCartService.getCartByUser(userId);
        ApiResponse<ShoppingCartDTO> response = new ApiResponse<>("SUCCESS", "Cart items fetched", cart);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
//
//    @DeleteMapping("/clear")
//    public ResponseEntity<?> clearCart(@RequestParam Long userId) {
//        shoppingCartService.clearCart(userId);
//        return ResponseEntity.ok().body(Map.of("status", "SUCCESS", "message", "Cart cleared"));
//    }
}
