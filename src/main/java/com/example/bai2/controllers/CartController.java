package com.example.bai2.controllers;

import com.example.bai2.models.Product;
import com.example.bai2.models.User;
import com.example.bai2.services.CartService;
import com.example.bai2.services.OrderService;
import com.example.bai2.services.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    /**
     * Thêm sản phẩm vào giỏ hàng (Session).
     */
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                           @RequestParam(defaultValue = "1") int quantity,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        Product product = productService.findById(productId).orElse(null);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại!");
            return "redirect:/";
        }
        String error = cartService.addToCart(session, product, quantity);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "Đã thêm '" + product.getName() + "' vào giỏ hàng!");
        }
        return "redirect:/";
    }

    /**
     * Xem trang giỏ hàng.
     */
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        model.addAttribute("cartItems", cartService.getCart(session));
        model.addAttribute("grandTotal", cartService.getGrandTotal(session));
        model.addAttribute("cartCount", cartService.getCartCount(session));
        return "cart";
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ (nút +/-).
     */
    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long productId,
                                 @RequestParam int quantity,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        String error = cartService.updateQuantity(session, productId, quantity);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        }
        return "redirect:/cart";
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng.
     */
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId,
                                HttpSession session) {
        cartService.removeFromCart(session, productId);
        return "redirect:/cart";
    }

    /**
     * Checkout - Đặt hàng.
     * Người dùng PHẢI đăng nhập mới được đặt hàng.
     */
    @PostMapping("/checkout")
    public String checkout(HttpSession session, RedirectAttributes redirectAttributes) {
        // Kiểm tra đăng nhập
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn phải đăng nhập để đặt hàng!");
            return "redirect:/login";
        }

        String error = orderService.checkout(session, user.getId());
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/cart";
        }

        return "redirect:/cart/success";
    }

    /**
     * Trang đặt hàng thành công.
     */
    @GetMapping("/success")
    public String checkoutSuccess(HttpSession session, Model model) {
        model.addAttribute("cartCount", cartService.getCartCount(session));
        return "checkout-success";
    }
}
