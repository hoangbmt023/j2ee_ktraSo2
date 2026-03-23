package com.example.bai2.controllers;

import com.example.bai2.models.Category;
import com.example.bai2.models.Product;
import com.example.bai2.models.User;
import com.example.bai2.services.CartService;
import com.example.bai2.services.CategoryService;
import com.example.bai2.services.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller cho Admin: quản lý Sản phẩm và Danh mục.
 * Tất cả endpoint đều kiểm tra role ADMIN.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    // ===================== KIỂM TRA ADMIN =====================

    /**
     * Kiểm tra user hiện tại có phải ADMIN không.
     * Trả về true nếu KHÔNG phải admin (cần redirect).
     */
    private boolean isNotAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user == null || !user.isAdmin();
    }

    // ===================== QUẢN LÝ SẢN PHẨM =====================

    @GetMapping("/products")
    public String listProducts(@RequestParam(defaultValue = "0") int page,
                               HttpSession session, Model model) {
        if (isNotAdmin(session)) return "redirect:/";

        int pageSize = 10; // 1 sản phẩm / trang
        Page<Product> productPage = productService.findAllPaged(page, pageSize);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("cartCount", cartService.getCartCount(session));
        return "admin/products";
    }

    @GetMapping("/products/create")
    public String createProductForm(HttpSession session, Model model) {
        if (isNotAdmin(session)) return "redirect:/";

        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("cartCount", cartService.getCartCount(session));
        return "admin/product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, HttpSession session, Model model) {
        if (isNotAdmin(session)) return "redirect:/";

        Product product = productService.findById(id).orElse(null);
        if (product == null) return "redirect:/admin/products";

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("cartCount", cartService.getCartCount(session));
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@RequestParam(required = false) Long id,
                              @RequestParam String name,
                              @RequestParam Double price,
                              @RequestParam Integer quantityInStock,
                              @RequestParam Long categoryId,
                              @RequestParam(required = false) String imageUrl,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session)) return "redirect:/";

        // Validation
        if (name == null || name.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Tên sản phẩm không được để trống!");
            return "redirect:/admin/products/create";
        }
        if (price == null || price <= 0) {
            redirectAttributes.addFlashAttribute("error", "Giá phải lớn hơn 0!");
            return "redirect:/admin/products/create";
        }
        if (quantityInStock == null || quantityInStock < 0) {
            redirectAttributes.addFlashAttribute("error", "Số lượng tồn kho không hợp lệ!");
            return "redirect:/admin/products/create";
        }

        Product product;
        if (id != null) {
            // Edit
            product = productService.findById(id).orElse(new Product());
        } else {
            // Create
            product = new Product();
        }

        product.setName(name);
        product.setPrice(price);
        product.setQuantityInStock(quantityInStock);
        product.setImageUrl(imageUrl);

        Category category = categoryService.findById(categoryId).orElse(null);
        product.setCategory(category);

        productService.save(product);
        redirectAttributes.addFlashAttribute("success",
                id != null ? "Cập nhật sản phẩm thành công!" : "Thêm sản phẩm thành công!");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session)) return "redirect:/";

        try {
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa sản phẩm (có thể đang liên kết với đơn hàng)!");
        }
        return "redirect:/admin/products";
    }

    // ===================== QUẢN LÝ DANH MỤC =====================

    @GetMapping("/categories")
    public String listCategories(@RequestParam(defaultValue = "0") int page,
                                 HttpSession session, Model model) {
        if (isNotAdmin(session)) return "redirect:/";

        int pageSize = 10; // 1 danh mục / trang
        Page<Category> categoryPage = categoryService.findAllPaged(page, pageSize);

        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("cartCount", cartService.getCartCount(session));
        return "admin/categories";
    }

    @GetMapping("/categories/create")
    public String createCategoryForm(HttpSession session, Model model) {
        if (isNotAdmin(session)) return "redirect:/";

        model.addAttribute("category", new Category());
        model.addAttribute("cartCount", cartService.getCartCount(session));
        return "admin/category-form";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, HttpSession session, Model model) {
        if (isNotAdmin(session)) return "redirect:/";

        Category category = categoryService.findById(id).orElse(null);
        if (category == null) return "redirect:/admin/categories";

        model.addAttribute("category", category);
        model.addAttribute("cartCount", cartService.getCartCount(session));
        return "admin/category-form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@RequestParam(required = false) Long id,
                               @RequestParam String name,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session)) return "redirect:/";

        if (name == null || name.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Tên danh mục không được để trống!");
            return "redirect:/admin/categories/create";
        }

        Category category;
        if (id != null) {
            category = categoryService.findById(id).orElse(new Category());
        } else {
            category = new Category();
        }
        category.setName(name);
        categoryService.save(category);

        redirectAttributes.addFlashAttribute("success",
                id != null ? "Cập nhật danh mục thành công!" : "Thêm danh mục thành công!");
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session)) return "redirect:/";

        try {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa danh mục!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa danh mục (có sản phẩm đang thuộc danh mục này)!");
        }
        return "redirect:/admin/categories";
    }
}
