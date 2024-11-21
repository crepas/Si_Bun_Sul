package com.sys.OrderSystem.Entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.Set;

public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;
    @Column(nullable = false)
    private Date orderDate;
    @Column(nullable = false)
    private boolean status;
    private int tableId;

    @OneToMany(mappedBy = "order")
    private Set<OrderItem> orderItems;
    @OneToOne(mappedBy = "order")
    private Payment payment;
}

