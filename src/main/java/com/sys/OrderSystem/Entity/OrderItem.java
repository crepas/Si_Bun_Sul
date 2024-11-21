package com.sys.OrderSystem.Entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;


public class  OrderItem {
    @Id
    private String orderItemId;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private int totalPrice;

}

