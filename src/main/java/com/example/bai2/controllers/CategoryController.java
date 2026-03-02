package com.example.bai2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bai2.models.Category;
import com.example.bai2.services.CategoryService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // LIST
    @GetMapping
    public String index(Model model) {
        model.addAttribute("listcategory", categoryService.getAll());
        return "category/categories";
    }

    // CREATE - FORM
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("category", new Category());
        return "category/create";
    }

    // CREATE - SUBMIT
    @PostMapping("/create")
    public String create(@Valid Category category,
            BindingResult result) {

        if (result.hasErrors()) {
            return "category/create";
        }

        categoryService.save(category);
        return "redirect:/categories";
    }

    // EDIT - FORM
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        Category category = categoryService.get(id);
        model.addAttribute("category", category);
        return "category/edit";
    }

    // EDIT - SUBMIT
    @PostMapping("/edit")
    public String edit(@Valid Category category,
            BindingResult result) {

        if (result.hasErrors()) {
            return "category/edit";
        }

        categoryService.save(category);
        return "redirect:/categories";
    }

    // DELETE
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id, Model model) {
        try {
            categoryService.delete(id);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("listcategory", categoryService.getAll());
            return "category/categories";
        }

        return "redirect:/categories";
    }
}
