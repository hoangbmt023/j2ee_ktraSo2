package com.example.bai2.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bai2.models.Category;
import com.example.bai2.repositories.CategoryRepository;
import com.example.bai2.repositories.ProductRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;
    // Lấy tất cả danh mục (đổ dropdown)
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    // Lấy danh mục theo id
    public Category get(int id) {
        return categoryRepository.findById(id).orElse(null);
    }

    // Lưu mới hoặc cập nhật danh mục
    public void save(Category category) {
        categoryRepository.save(category);
    }

    // Xóa danh mục
    public void delete(int id) {
        boolean hasProduct = productRepository.existsByCategory_Id(id);

        if (hasProduct) {
            throw new RuntimeException("Không thể xóa danh mục vì còn sản phẩm!");
        }

        Category find = get(id);
        if (find != null) {
            categoryRepository.delete(find);
        }
    }
}
