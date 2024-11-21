package com.sys.OrderSystem.Entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.util.Date;


public class  Payment {
    @Id
    private String paymentId;
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @Column(nullable = false)
    private int amount;
    @Column(nullable = false)
    private Date paymentDate;

}

