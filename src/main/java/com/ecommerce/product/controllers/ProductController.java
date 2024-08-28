package com.ecommerce.product.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.constants.StatusMessage;
import com.ecommerce.dtos.ApiResponse;
import com.ecommerce.entities.Product;
import com.ecommerce.product.dtos.ProductResponse;
import com.ecommerce.product.services.ProductService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${version}/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    
    Gson gson = new Gson();

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping("/add-product")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody Product product, Authentication authentication) throws Exception {
    	
    	log.info("Request for adding product : {}", gson.toJson(product));
    	
    	if (authentication == null) {
			log.error("Authentication required to change password.");
			throw new Exception("Authentication required to change password.");
		} else if (!authentication.isAuthenticated()) {
			log.error("User is not authenticated.");
			throw new Exception("User is not authenticated.");
		}
    	
    	ProductResponse createdProduct = productService.createProduct(product, authentication.getName());
        ApiResponse<ProductResponse> response = new ApiResponse<ProductResponse>(StatusMessage.SUCCESS.toString(), "Product added successfully.", createdProduct);
        
        log.info("Response : {}", gson.toJson(response));
        return new ResponseEntity<ApiResponse<ProductResponse>>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

