package com.technova.shopai.service;

import com.technova.shopai.dto.ProductDto;
import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(Long id, ProductDto productDto);
    void deleteProduct(Long id);
    ProductDto getProductById(Long id);
    List<ProductDto> getAllProducts(String category, int page, int size);
}
