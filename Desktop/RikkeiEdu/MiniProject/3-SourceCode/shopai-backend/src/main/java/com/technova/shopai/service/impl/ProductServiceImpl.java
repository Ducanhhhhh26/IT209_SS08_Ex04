package com.technova.shopai.service.impl;

import com.technova.shopai.dto.ProductDto;
import com.technova.shopai.entity.Product;
import com.technova.shopai.exception.ResourceNotFoundException;
import com.technova.shopai.repository.ProductRepository;
import com.technova.shopai.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Product product = mapToEntity(productDto);
        if (product.getStockQuantity() > 0) {
            product.setStatus("ACTIVE");
        } else {
            product.setStatus("OUT_OF_STOCK");
        }
        Product savedProduct = productRepository.save(product);
        return mapToDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setCategory(productDto.getCategory().toUpperCase());

        if (product.getStockQuantity() > 0) {
            product.setStatus("ACTIVE");
        } else {
            product.setStatus("OUT_OF_STOCK");
        }

        Product updatedProduct = productRepository.save(product);
        return mapToDto(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (category != null && !category.trim().isEmpty()) {
            return productRepository.findByCategoryIgnoreCase(category, pageable)
                    .stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        } else {
            return productRepository.findAll(pageable)
                    .stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        }
    }

    private Product mapToEntity(ProductDto dto) {
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .category(dto.getCategory().toUpperCase())
                .status(dto.getStatus())
                .build();
    }

    private ProductDto mapToDto(Product entity) {
        return ProductDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stockQuantity(entity.getStockQuantity())
                .category(entity.getCategory())
                .status(entity.getStatus())
                .build();
    }
}
