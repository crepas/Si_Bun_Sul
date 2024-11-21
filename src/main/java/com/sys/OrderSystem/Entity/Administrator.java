package com.sys.OrderSystem.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;

public class Administrator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int adminId;
    @Column(nullable = false)
    private String  username;
    @Column(nullable = false)
    private String  password;
    @Column(nullable = false)
    private String  name;
    @Column(nullable = false)
    private String  email;
    @Column(nullable = false)
    private String  phoneNumber;

}
