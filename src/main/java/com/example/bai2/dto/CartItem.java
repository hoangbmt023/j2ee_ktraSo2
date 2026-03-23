package com.example.bai2.dto;

import lombok.*;
import java.io.Serializable;

/**
 * DTO lưu trong Session - đại diện 1 item trong giỏ hàng.
 * Implements Serializable để có thể serialize vào Session.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long productId;
    private String productName;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    private Integer maxStock; // quantity_in_stock từ DB để validate

    /**
     * Tính subtotal = price * quantity
     */
    public Double getSubtotal() {
        return price * quantity;
    }
}
