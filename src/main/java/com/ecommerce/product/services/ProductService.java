package com.ecommerce.product.services;

import java.util.Date;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.entities.Product;
import com.ecommerce.entities.User;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.product.dtos.ProductResponse;
import com.ecommerce.product.repositories.CategoryRepository;
import com.ecommerce.product.repositories.ProductRepository;
import com.ecommerce.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@Slf4j
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

//    public List<ProductResponse> getAllProducts() {
//        List<Product> products =  productRepository.findAll();
//        // Convert List<Product> to List<ProductResponseDTO>
//        List<ProductResponse> productResponses = products.stream()
//            .map(this::convertToDTO)
//            .collect(Collectors.toList());
//        
//        return productResponses;
//    }

	public Page<ProductResponse> getAllProducts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		// Create a Pageable object using the page number and size
		Pageable pageable = PageRequest.of(page, size);

		// Fetch the paginated products from the repository
		Page<Product> productsPage = productRepository.findAll(pageable);

		// Convert Page<Product> to Page<ProductResponseDTO>
		Page<ProductResponse> productResponses = productsPage.map(this::convertToDTO);

		return productResponses;
	}

	public Product getProductById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
	}

	public ProductResponse createProduct(Product product, String email) throws Exception {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));

		// Fetch the category from the database
		categoryRepository.findById(product.getCategory().getId()).orElseThrow(
				() -> new ResourceNotFoundException("Category not found with id " + product.getCategory().getId()));

		product.setCreatedAt(new Date());
		product.setCreatedBy(user);

		return convertToDTO(productRepository.save(product));
	}

	public ProductResponse updateProduct(Long id, Product updatedProduct, String email) throws Exception {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));

		Product existingProduct = getProductById(id);
		existingProduct.setName(updatedProduct.getName());
		existingProduct.setDescription(updatedProduct.getDescription());
		existingProduct.setPrice(updatedProduct.getPrice());
		existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
		existingProduct.setCategory(updatedProduct.getCategory());
		existingProduct.setImageUrl(updatedProduct.getImageUrl());
		existingProduct.setStatus(updatedProduct.getStatus());
		existingProduct.setUpdatedAt(new Date());
		existingProduct.setUpdatedBy(user);

		return convertToDTO(productRepository.save(existingProduct));
	}

	public void deleteProduct(Long id) {
		Product product = getProductById(id);
		productRepository.delete(product);
	}

	private ProductResponse convertToDTO(Product product) {
		ProductResponse responseDTO = new ProductResponse();
		responseDTO.setId(product.getId());
		responseDTO.setName(product.getName());
		responseDTO.setDescription(product.getDescription());
		responseDTO.setPrice(product.getPrice());
		responseDTO.setStockQuantity(product.getStockQuantity());
		responseDTO.setImageUrl(product.getImageUrl());
		responseDTO.setStatus(product.getStatus().toString());
		responseDTO.setCreatedAt(product.getCreatedAt());
		responseDTO.setUpdatedAt(product.getUpdatedAt());
		responseDTO.setCategoryId(product.getCategory().getId());

		return responseDTO;
	}

	public ProductResponse getProductResponseById(Long id) {

		return convertToDTO(getProductById(id));
	}

	public List<ProductResponse> getLatestProducts(int n) {
        return productRepository.findTopNProducts(n).stream()
                .map(this::convertToDTO) // Ensure you have a method to convert Product to ProductResponse
                .collect(Collectors.toList());
    }

	public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                       .map(this::convertToDTO)
                       .collect(Collectors.toList());
	}
}
