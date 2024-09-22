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

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RestController
@RequestMapping("${version}/api/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	Gson gson = new Gson();

	// Implemented
//	@GetMapping
//	public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
//		List<ProductResponse> products = productService.getAllProducts();
//		ApiResponse<List<ProductResponse>> response = new ApiResponse<List<ProductResponse>>(
//				StatusMessage.SUCCESS.toString(), "Successfully fetched all products.", products);
//		log.info("Response : {}", gson.toJson(response));
//		return new ResponseEntity<>(response, HttpStatus.OK);
//	}
	
	// Get paginated products
    @GetMapping
    public Page<ProductResponse> getAllProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        return productService.getAllProducts(page, size);
    }
    
    @GetMapping("/new-arrivals")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getNewArrivals(@RequestParam(defaultValue = "4") int n) {
        List<ProductResponse> products = productService.getLatestProducts(n);
        ApiResponse<List<ProductResponse>> response = new ApiResponse<List<ProductResponse>>(
				StatusMessage.SUCCESS.toString(), "Successfully fetched new products.", products);
		log.info("Response : {}", gson.toJson(response));
		return new ResponseEntity<>(response, HttpStatus.OK);
    }

	// Implemented
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
		ProductResponse product = productService.getProductResponseById(id);
		ApiResponse<ProductResponse> response = new ApiResponse<ProductResponse>(StatusMessage.SUCCESS.toString(),
				"Successfully fetched product with id : " + id, product);
		log.info("Response : {}", gson.toJson(response));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Implemented
	@PostMapping("/add-product")
	@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
	public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody Product product,
			Authentication authentication) throws Exception {

		ProductResponse createdProduct = productService.createProduct(product, authentication.getName());
		ApiResponse<ProductResponse> response = new ApiResponse<ProductResponse>(StatusMessage.SUCCESS.toString(),
				"Product added successfully.", createdProduct);

		log.info("Response : {}", gson.toJson(response));
		return new ResponseEntity<ApiResponse<ProductResponse>>(response, HttpStatus.OK);
	}

	// Implemented
	@PutMapping("/update/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
	public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long id,
			@RequestBody Product product, Authentication authentication) throws Exception {
		ProductResponse updatedProduct = productService.updateProduct(id, product, authentication.getName());
		ApiResponse<ProductResponse> response = new ApiResponse<ProductResponse>(StatusMessage.SUCCESS.toString(),
				"Product updated successfully.", updatedProduct);
		log.info("Response : {}", gson.toJson(response));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Implemented
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
	public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		ApiResponse<?> response = new ApiResponse<>(StatusMessage.SUCCESS.toString(), "Product deleted successfully.");
		log.info("Response : {}", gson.toJson(response));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
