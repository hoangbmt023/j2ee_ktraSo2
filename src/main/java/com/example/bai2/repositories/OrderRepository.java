package com.example.bai2.repositories;

import com.example.bai2.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Lấy danh sách đơn hàng của user, sắp xếp theo ngày mới nhất.
     */
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
}
