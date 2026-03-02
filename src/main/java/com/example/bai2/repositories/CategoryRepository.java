package com.example.bai2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bai2.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
}
