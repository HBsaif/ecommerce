package com.ecommerce.product.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.entities.Category;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.product.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;

	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	public Category getCategoryById(Long id) {
		return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
	}

	public Category createProduct(Category category) {
		return categoryRepository.save(category);
	}

	public Category updateCategory(Long id, Category category) {
		Category existingCategory = getCategoryById(id);
		
		existingCategory.setName(category.getName());
		existingCategory.setDescription(category.getDescription());
		
		return categoryRepository.save(existingCategory);
	}

	public void deleteCategory(Long id) {
		Category category = getCategoryById(id);
		categoryRepository.delete(category);
		
	}

}
