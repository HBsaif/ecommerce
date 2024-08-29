package com.ecommerce.product.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.constants.StatusMessage;
import com.ecommerce.dtos.ApiResponse;
import com.ecommerce.entities.Category;
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
		
		ApiResponse<List<Category>> response = new ApiResponse<List<Category>>(
				StatusMessage.SUCCESS.toString(), "Successfully fetched all categories.", categoris);
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

}
