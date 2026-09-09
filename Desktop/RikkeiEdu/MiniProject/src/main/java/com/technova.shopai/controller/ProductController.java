package com.technova.shopai.controller;

import com.technova.shopai.dto.ProductDto;
import com.technova.shopai.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Public API: Xem danh sách sản phẩm (có lọc theo category, phân trang)
    @GetMapping("/public/products")
    public ResponseEntity<List<ProductDto>> getAllProducts(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        List<ProductDto> products = productService.getAllProducts(category, page, size);
        return ResponseEntity.ok(products);
    }

    // Public API: Xem chi tiết sản phẩm
    @GetMapping("/public/products/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto productDto = productService.getProductById(id);
        return ResponseEntity.ok(productDto);
    }

    // Admin API: Thêm sản phẩm (STAFF hoặc MANAGER)
    @PostMapping("/admin/products")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        ProductDto createdProduct = productService.createProduct(productDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // Admin API: Cập nhật sản phẩm (STAFF hoặc MANAGER)
    @PutMapping("/admin/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDto) {
        ProductDto updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    // Admin API: Xóa sản phẩm (STAFF hoặc MANAGER)
    @DeleteMapping("/admin/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully.");
    }
}
