package com.example.bai2.services;

import com.example.bai2.models.Product;
import com.example.bai2.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Tìm kiếm sản phẩm kết hợp: keyword + categoryId + sort + phân trang.
     *
     * @param keyword    từ khóa tìm theo tên (null/rỗng = tất cả)
     * @param categoryId ID danh mục lọc (null = tất cả)
     * @param sortBy     "price_asc" hoặc "price_desc" (null = mặc định theo id)
     * @param page       trang hiện tại (0-indexed)
     * @param size       số sản phẩm mỗi trang
     */
    public Page<Product> searchProducts(String keyword, Long categoryId, String sortBy, int page, int size) {
        Sort sort;
        if ("price_asc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "price");
        } else if ("price_desc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "price");
        } else {
            sort = Sort.by(Sort.Direction.ASC, "id");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.searchProducts(keyword, categoryId, pageable);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }
}