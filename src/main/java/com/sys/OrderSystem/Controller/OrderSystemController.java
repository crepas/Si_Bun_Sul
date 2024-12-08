package com.sys.OrderSystem.Controller;

import com.sys.OrderSystem.Entity.Category;
import com.sys.OrderSystem.Entity.Menu;
import com.sys.OrderSystem.Entity.Order;
import com.sys.OrderSystem.Service.MenuService;
import com.sys.OrderSystem.Service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Map;

@Controller
public class OrderSystemController {
    private final MenuService menuService;
    private final OrderService orderService;
    private static final Logger log = LoggerFactory.getLogger(OrderSystemController.class);

    @Autowired
    public OrderSystemController(MenuService menuService, OrderService orderService) {
        this.menuService = menuService;
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String home() {
        return "index";  // index.html을 보여줌
    }

    @GetMapping("/login")
    public String login() {
        return "login";  // login.html로 이동
    }

    @GetMapping("/menu")
    public String menu(Model model, @RequestParam(required = false) Integer categoryId) {
        try {
            List<Category> categories = menuService.getAllCategories();
            if (categories.isEmpty()) {
                return "error";
            }

            Category selectedCategory = null;
            if (categoryId != null) {
                selectedCategory = categories.stream()
                        .filter(c -> c.getCategoryId().equals(categoryId))
                        .findFirst()
                        .orElse(categories.get(0));
            } else {
                selectedCategory = categories.get(0);
            }

            Map<Category, List<Menu>> menusByCategory = menuService.getMenusByCategory();

            model.addAttribute("categories", categories);
            model.addAttribute("menusByCategory", menusByCategory);
            model.addAttribute("selectedCategory", selectedCategory);

            return "menu";
        } catch (Exception e) {
            log.error("Error loading menu page", e);
            return "error";
        }
    }
}

