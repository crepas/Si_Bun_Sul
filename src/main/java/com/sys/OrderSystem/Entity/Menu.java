package com.sys.OrderSystem.Entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.util.Set;

public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int menuId;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int price;

    @OneToMany(mappedBy = "menu")
    private Set<OrderItem> orderItems;
}
