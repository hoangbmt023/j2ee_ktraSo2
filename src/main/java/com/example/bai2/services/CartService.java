package com.example.bai2.services;

import com.example.bai2.dto.CartItem;
import com.example.bai2.models.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý giỏ hàng 100% trong Session.
 * Key trong session: "cart" -> List<CartItem>
 */
@Service
public class CartService {

    private static final String CART_SESSION_KEY = "cart";

    /**
     * Lấy giỏ hàng từ session (tạo mới nếu chưa có).
     */
    @SuppressWarnings("unchecked")
    public List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    /**
     * Thêm sản phẩm vào giỏ hàng.
     * Nếu sản phẩm đã có trong giỏ -> tăng quantity (không vượt stock).
     * Nếu chưa có -> thêm mới.
     * Trả về thông báo lỗi nếu vượt stock, null nếu thành công.
     */
    public String addToCart(HttpSession session, Product product, int qty) {
        if (qty <= 0) return "Số lượng phải lớn hơn 0!";

        List<CartItem> cart = getCart(session);

        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        for (CartItem item : cart) {
            if (item.getProductId().equals(product.getId())) {
                int newQty = item.getQuantity() + qty;
                if (newQty > product.getQuantityInStock()) {
                    return "Số lượng vượt quá tồn kho! (Tối đa: " + product.getQuantityInStock()
                            + ", đã có trong giỏ: " + item.getQuantity() + ")";
                }
                item.setQuantity(newQty);
                item.setMaxStock(product.getQuantityInStock());
                session.setAttribute(CART_SESSION_KEY, cart);
                return null; // Thành công
            }
        }

        // Sản phẩm chưa có trong giỏ -> thêm mới
        if (qty > product.getQuantityInStock()) {
            return "Số lượng vượt quá tồn kho! (Tối đa: " + product.getQuantityInStock() + ")";
        }
        CartItem newItem = new CartItem();
        newItem.setProductId(product.getId());
        newItem.setProductName(product.getName());
        newItem.setImageUrl(product.getImageUrl());
        newItem.setPrice(product.getPrice());
        newItem.setQuantity(qty);
        newItem.setMaxStock(product.getQuantityInStock());
        cart.add(newItem);
        session.setAttribute(CART_SESSION_KEY, cart);
        return null; // Thành công
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ.
     * Trả về lỗi nếu quantity <= 0 hoặc vượt stock.
     */
    public String updateQuantity(HttpSession session, Long productId, int newQty) {
        if (newQty <= 0) return "Số lượng phải lớn hơn 0!";

        List<CartItem> cart = getCart(session);
        for (CartItem item : cart) {
            if (item.getProductId().equals(productId)) {
                if (newQty > item.getMaxStock()) {
                    return "Số lượng vượt quá tồn kho! (Tối đa: " + item.getMaxStock() + ")";
                }
                item.setQuantity(newQty);
                session.setAttribute(CART_SESSION_KEY, cart);
                return null;
            }
        }
        return "Sản phẩm không tồn tại trong giỏ hàng!";
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng.
     */
    public void removeFromCart(HttpSession session, Long productId) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProductId().equals(productId));
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    /**
     * Tổng số lượng item trong giỏ (để hiển thị badge trên header).
     */
    public int getCartCount(HttpSession session) {
        return getCart(session).stream().mapToInt(CartItem::getQuantity).sum();
    }

    /**
     * Tổng tiền toàn bộ giỏ hàng (Grand Total).
     */
    public double getGrandTotal(HttpSession session) {
        return getCart(session).stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    /**
     * Xóa trắng giỏ hàng trong Session.
     */
    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }
}
