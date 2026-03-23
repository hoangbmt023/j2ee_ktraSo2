package com.example.bai2.controllers;

import com.example.bai2.models.Category;
import com.example.bai2.models.Product;
import com.example.bai2.services.CartService;
import com.example.bai2.services.CategoryService;
import com.example.bai2.services.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    /**
     * Trang chủ: Hiển thị danh sách sản phẩm dạng grid.
     * Kết hợp Search + Filter + Sort + Pagination.
     *
     * @param keyword    từ khóa tìm kiếm (optional)
     * @param categoryId ID danh mục để lọc (optional)
     * @param sort       "price_asc" hoặc "price_desc" (optional)
     * @param page       trang hiện tại (0-indexed, default = 0)
     */
    @GetMapping("/")
    public String home(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) String sort,
                       @RequestParam(defaultValue = "0") int page,
                       Model model,
                       HttpSession session) {

        int pageSize = 5; // Hiển thị 5 sản phẩm / trang

        Page<Product> productPage = productService.searchProducts(keyword, categoryId, sort, page, pageSize);
        List<Category> categories = categoryService.findAll();

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());

        // Giữ lại các filter params để hiển thị trên UI
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sort", sort);
        model.addAttribute("categories", categories);

        // Cart count cho badge trên header
        model.addAttribute("cartCount", cartService.getCartCount(session));

        return "index";
    }
}
