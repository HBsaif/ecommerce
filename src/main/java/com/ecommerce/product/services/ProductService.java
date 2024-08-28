package com.ecommerce.product.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.entities.Category;
import com.ecommerce.entities.Product;
import com.ecommerce.entities.User;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.product.dtos.ProductResponse;
import com.ecommerce.product.repositories.CategoryRepository;
import com.ecommerce.product.repositories.ProductRepository;
import com.ecommerce.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    public ProductResponse createProduct(Product product, String email) throws Exception {
    	User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));
    	
    	// Fetch the category from the database
        categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + product.getCategory().getId()));
    	
        product.setCreatedAt(new Date());
        product.setCreatedBy(user);
        
        log.info("Product : {}", product.toString());
        
        Product p = productRepository.save(product);
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setDescription(product.getDescription());
        productResponse.setPrice(product.getPrice());
        productResponse.setStockQuantity(product.getStockQuantity());
        productResponse.setImageUrl(product.getImageUrl());
        productResponse.setStatus(product.getStatus().toString());
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setCategoryId(product.getCategory().getId());
        
        return productResponse;
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());
        existingProduct.setStatus(updatedProduct.getStatus());
        existingProduct.setUpdatedAt(new Date());
        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}

