package com.ecommerce.product.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.entities.CartItem;
import com.ecommerce.entities.Product;
import com.ecommerce.entities.ShoppingCart;
import com.ecommerce.entities.User;
import com.ecommerce.product.dtos.CartItemDTO;
import com.ecommerce.product.dtos.ShoppingCartDTO;
import com.ecommerce.product.repositories.ShoppingCartRepository;
import com.ecommerce.repositories.UserRepository;
import com.ecommerce.util.CommonServiceHelper;
import com.ecommerce.util.SPName;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ShoppingCartService {

	@Autowired
	private CommonServiceHelper helper;
	
	@Autowired
    private ShoppingCartRepository shoppingCartRepository;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserRepository userRepository;

	public ShoppingCart addItemToCart(Long userId, Long productId, int quantity) throws Exception {
        Map<String, Object> params = new LinkedHashMap<>();
        
        // Set input parameters for the stored procedure
        params.put("p_user_id", userId);
        params.put("p_product_id", productId);
        params.put("p_quantity", quantity);
        
        // Set output parameters (ensure to use null for OUT params initially)
        params.put("p_out", Map.of("type", Integer.class)); // To capture success/failure
        params.put("p_err_msg", Map.of("type", String.class)); // To capture error message
        
        try {
			// Execute the stored procedure
			Map<String, Object> result = helper.executeStoredProcedure(SPName.SP_ADD_TO_CART.toString(), params);

			// Check the output parameters to determine the result
			Integer outResult = (Integer) result.get("p_out");
			String errMsg = (String) result.get("p_err_msg");
			


			if (outResult == 0) {
			    // Success case
			    return getShoppingCartForUser(userId); // Method to retrieve the updated cart
			} else {
			    // Failure case, handle as needed, such as throwing an exception
			    throw new RuntimeException("Error adding item to cart: " + errMsg);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
    }

	// Method to retrieve the shopping cart for a user
    public ShoppingCart getShoppingCartForUser(Long userId) {
        // SQL query to retrieve the shopping cart for the user
        String sql = "SELECT c.id as cart_id , u.id as user_id   \r\n"
        		+ "FROM shopping_carts c \r\n"
        		+ "JOIN users u ON c.user_id = u.id \r\n"
        		+ "WHERE c.user_id = :userId";

        // Create a map for parameters
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("userId", userId);

        // Execute the query
        List<Object[]> results = helper.executeQuery(sql, params);

        // Check if a cart was found
        if (!results.isEmpty()) {
            Object[] row = results.get(0);
            ShoppingCart cart = new ShoppingCart();
            cart.setId((Long) row[0]); // ID of the cart

            // Set the user
            User user = new User();
            user.setId((Integer) row[1]); // Assuming you have a User class with setId method
            cart.setUser(user);
            
            // Optionally, retrieve cart items if needed
            cart.setItems(getCartItemsForCart(cart.getId()));

            return cart;
        }

        // Return null or throw an exception if no cart is found
        return null; // Or consider throwing a custom exception
    }

    // Method to retrieve cart items based on cart ID
    private List<CartItem> getCartItemsForCart(Long cartId) {
        String sql = "SELECT ci.id, ci.product_id, ci.quantity, ci.price_at_time " +
                     "FROM cart_items ci " +
                     "WHERE ci.cart_id = :cartId";

        Map<String, Object> params = new HashMap<>();
        params.put("cartId", cartId);

        List<Object[]> results = helper.executeQuery(sql, params);
        List<CartItem> items = new ArrayList<>();

        for (Object[] row : results) {
        	Product product = new Product();
        	product.setId((Long) row[1]); // Set the product ID

            CartItem item = new CartItem();
            item.setId((Long) row[0]);
            item.setProduct(product); // Assuming you have a Product class with a constructor
            item.setQuantity((Integer) row[2]);
            item.setPriceAtTime((BigDecimal) row[3]);
            items.add(item);
        }
        
        return items;
    }

    public ShoppingCartDTO getCartByUser(int userId) {
        // Fetch the shopping cart associated with the user ID
        Optional<ShoppingCart> optionalCart = shoppingCartRepository.findByUserId(userId);
        
        if (optionalCart.isPresent()) {
            ShoppingCart cart = optionalCart.get();
            ShoppingCartDTO dto = new ShoppingCartDTO();
            dto.setId(cart.getId());
            dto.setUserId(cart.getUser().getId());
            
            // Fetch the cart items and map them to DTOs
            dto.setCartItems(cart.getItems().stream()
                .map(item -> {
                    CartItemDTO itemDTO = new CartItemDTO();
                    itemDTO.setId(item.getId());
                    itemDTO.setPriceAtTime(item.getPriceAtTime());
                    itemDTO.setQuantity(item.getQuantity());
                    
                    // Access product details directly from the CartItem
                    Product product = item.getProduct();
                    if (product != null) {
                        itemDTO.setName(product.getName()); // Set product name
                        itemDTO.setImageUrl(product.getImageUrl()); // Set product image URL
                    }
                    
                    return itemDTO;
                })
                .collect(Collectors.toList()));
            return dto;
        }
        
        // Check if the cart exists and return it, otherwise return null
        return null; // Return null if no cart is found
    }

    public int getCartItemsNo(String email) {
        // Fetch the user based on the email
        Optional<User> optionalUser = userRepository.findByEmail(email);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // Fetch the shopping cart associated with the user
            Optional<ShoppingCart> optionalCart = shoppingCartRepository.findByUserId(user.getId());
            
            if (optionalCart.isPresent()) {
                ShoppingCart cart = optionalCart.get();
                
                // Sum up the quantities of all items in the cart
                return cart.getItems().stream()
                           .mapToInt(CartItem::getQuantity)
                           .sum();
            }
        }
        
        // If no user or cart is found, return 0
        return 0;
    }




//    public void removeItemFromCart(Long userId, Long productId) {
//        ShoppingCart cart = getCartByUser(userId);
//        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, productId)
//                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));
//        cart.getItems().remove(cartItem);
//        cartItemRepository.delete(cartItem);
//        cart.setUpdatedAt(new Date());
//        shoppingCartRepository.save(cart);
//    }

//    public void clearCart(Long userId) {
//        ShoppingCart cart = getCartByUser(userId);
//        cart.getItems().clear();
//        shoppingCartRepository.save(cart);
//    }
}
