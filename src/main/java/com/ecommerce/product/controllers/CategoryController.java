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
import com.ecommerce.entities.Category;
import com.ecommerce.entities.Product;
import com.ecommerce.product.dtos.ProductResponse;
import com.ecommerce.product.services.CategoryService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${version}/api/categories")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	Gson gson = new Gson();

	// Implemented
	@GetMapping
	public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
		List<Category> categoris = categoryService.getAllCategories();

		ApiResponse<List<Category>> response = new ApiResponse<List<Category>>(StatusMessage.SUCCESS.toString(),
				"Successfully fetched all categories.", categoris);
		log.info("Response : {}", gson.toJson(response));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
		Category category = categoryService.getCategoryById(id);
		ApiResponse<Category> response = new ApiResponse<Category>(StatusMessage.SUCCESS.toString(),
				"Successfully fetched category with id : " + id, category);
		log.info("Response : {}", gson.toJson(response));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/add-category")
	@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
	public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody Category category) throws Exception {

		Category cat = categoryService.createProduct(category);
		ApiResponse<Category> response = new ApiResponse<Category>(StatusMessage.SUCCESS.toString(),
				"Product added successfully.", cat);

		log.info("Response : {}", gson.toJson(response));
		return new ResponseEntity<ApiResponse<Category>>(response, HttpStatus.OK);
	}

	// Implemented
	@PutMapping("/update/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
	public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long id,
			@RequestBody Category category) throws Exception {
		Category updatedCategory = categoryService.updateCategory(id, category);
		ApiResponse<Category> response = new ApiResponse<Category>(StatusMessage.SUCCESS.toString(),
				"Category updated successfully.", updatedCategory);
		log.info("Response : {}", gson.toJson(response));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
	public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable Long id) {
		categoryService.deleteCategory(id);
		ApiResponse<?> response = new ApiResponse<>(StatusMessage.SUCCESS.toString(), "Category deleted successfully.");
		log.info("Response : {}", gson.toJson(response));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
