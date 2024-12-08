package com.sys.OrderSystem.Controller;

import com.sys.OrderSystem.Entity.Order;
import com.sys.OrderSystem.Entity.OrderItem;
import com.sys.OrderSystem.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public String getAllOrders(Model model) {
        List<OrderItem> orderItems = orderService.getCompletedOrderItems();
        model.addAttribute("orderItems", orderItems);
        return "ordermanagement";
    }
}

