package com.example.bai2.controllers;

import com.example.bai2.models.Order;
import com.example.bai2.models.User;
import com.example.bai2.services.CartService;
import com.example.bai2.services.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller hiển thị lịch sử đơn hàng của user.
 * User phải đăng nhập mới xem được.
 */
@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    /**
     * Danh sách đơn hàng của user hiện tại.
     */
    @GetMapping
    public String listOrders(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn phải đăng nhập để xem đơn hàng!");
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByUserId(user.getId());
        model.addAttribute("orders", orders);
        model.addAttribute("cartCount", cartService.getCartCount(session));
        return "orders";
    }

    /**
     * Chi tiết 1 đơn hàng.
     * Kiểm tra ownership: user chỉ xem được đơn hàng của chính mình.
     */
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn phải đăng nhập để xem đơn hàng!");
            return "redirect:/login";
        }

        Order order = orderService.findOrderById(id).orElse(null);
        if (order == null) {
            redirectAttributes.addFlashAttribute("error", "Đơn hàng không tồn tại!");
            return "redirect:/orders";
        }

        // Kiểm tra ownership: user chỉ xem đơn hàng của mình
        if (!order.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền xem đơn hàng này!");
            return "redirect:/orders";
        }

        model.addAttribute("order", order);
        model.addAttribute("cartCount", cartService.getCartCount(session));
        return "order-detail";
    }
}
