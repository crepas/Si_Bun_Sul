package com.sys.OrderSystem.Repository;

import com.sys.OrderSystem.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByTableIdAndStatus(Integer tableId, Boolean status);
    List<Order> findByTableIdAndStatusOrderByOrderDateDesc(Integer tableId, Boolean status);
}