package com.sys.OrderSystem.Repository;


import com.sys.OrderSystem.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrder_StatusOrderByOrder_OrderDateDesc(Boolean status);
    List<OrderItem> findByMenu_MenuId(Integer menuId);
}