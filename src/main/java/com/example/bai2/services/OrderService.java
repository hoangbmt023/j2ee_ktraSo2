package com.example.bai2.services;

import com.example.bai2.dto.CartItem;
import com.example.bai2.models.Order;
import com.example.bai2.models.OrderDetail;
import com.example.bai2.models.Product;
import com.example.bai2.models.User;
import com.example.bai2.repositories.OrderRepository;
import com.example.bai2.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    /**
     * Checkout: Tạo Order + OrderDetails từ giỏ hàng trong Session.
     * - Tính tổng tiền
     * - Trừ tồn kho (quantity_in_stock) trong DB
     * - Xóa giỏ hàng trong Session
     *
     * @return thông báo lỗi nếu có, null nếu thành công.
     */
    @Transactional
    public String checkout(HttpSession session, Long userId) {
        // Kiểm tra giỏ hàng không rỗng
        List<CartItem> cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            return "Giỏ hàng trống!";
        }

        // Tìm user
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "Không tìm thấy người dùng!";
        }

        // Tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        double totalPrice = 0;
        List<OrderDetail> details = new ArrayList<>();

        for (CartItem cartItem : cart) {
            // Lấy product từ DB để check tồn kho real-time
            Product product = productService.findById(cartItem.getProductId()).orElse(null);
            if (product == null) {
                return "Sản phẩm '" + cartItem.getProductName() + "' không tồn tại!";
            }

            // Kiểm tra tồn kho
            if (cartItem.getQuantity() > product.getQuantityInStock()) {
                return "Sản phẩm '" + product.getName() + "' chỉ còn "
                        + product.getQuantityInStock() + " trong kho!";
            }

            // Trừ tồn kho
            product.setQuantityInStock(product.getQuantityInStock() - cartItem.getQuantity());
            productService.save(product);

            // Tạo OrderDetail
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(cartItem.getQuantity());
            detail.setUnitPrice(cartItem.getPrice());
            details.add(detail);

            totalPrice += cartItem.getSubtotal();
        }

        order.setTotalPrice(totalPrice);
        order.setOrderDetails(details);
        orderRepository.save(order);

        // Xóa giỏ hàng trong Session sau khi đặt hàng thành công
        cartService.clearCart(session);

        return null; // Thành công
    }
}
