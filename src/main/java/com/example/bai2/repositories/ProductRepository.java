package com.example.bai2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bai2.models.Product;

public interface  ProductRepository extends JpaRepository<Product, Integer> {
    boolean existsByCategory_Id(int categoryId);
}
