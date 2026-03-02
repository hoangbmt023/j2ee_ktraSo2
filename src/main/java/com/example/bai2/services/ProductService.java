package com.example.bai2.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.bai2.models.Product;
import com.example.bai2.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product get(int id) {
        return productRepository.findById(id).orElse(null);
    }

    public void add(Product newProduct) {
        productRepository.save(newProduct);
    }

    public void update(Product editProduct) {
        Product find = get(editProduct.getId());
        if (find != null) {
            find.setName(editProduct.getName());
            find.setPrice(editProduct.getPrice());
            find.setCategory(editProduct.getCategory());

            if (editProduct.getImage() != null) {
                find.setImage(editProduct.getImage());
            }

            productRepository.save(find); // THÊM DÒNG NÀY
        }
    }

    public void updateImage(Product product, MultipartFile imageProduct) {

        // 1️⃣ Check null
        if (imageProduct == null || imageProduct.isEmpty()) {
            return; // không upload thì thôi
        }

        // 2️⃣ Check content type (chỉ cho phép image)
        String contentType = imageProduct.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Tệp tải lên không phải là hình ảnh!");
        }

        try {
            // 3️⃣ Thư mục lưu ảnh (tạm dùng cho học)
            Path uploadDir = Paths.get("uploads/images");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 4️⃣ Tạo tên file mới (tránh trùng)
            String fileName = UUID.randomUUID() + "_" + imageProduct.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);

            // 5️⃣ Copy file
            Files.copy(
                    imageProduct.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING);

            // 6️⃣ Lưu tên ảnh vào product
            product.setImage(fileName);

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload hình ảnh", e);
        }
    }

    public void delete(int id) {
        Product find = get(id);
        if (find != null) {
            productRepository.delete(find);
        }
    }

}