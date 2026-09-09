package com.technova.shopai.repository;

import com.technova.shopai.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"user", "orderItems", "orderItems.product"})
    List<Order> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "orderItems", "orderItems.product"})
    Optional<Order> findById(Long id);

    @EntityGraph(attributePaths = {"user", "orderItems", "orderItems.product"})
    List<Order> findAll();

    // Thống kê doanh thu từ các đơn hàng đã thanh toán (PAID)
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = 'PAID'")
    BigDecimal calculateTotalRevenue();
}
