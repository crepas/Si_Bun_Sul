package com.sys.OrderSystem.Controller;

import com.sys.OrderSystem.Entity.Category;
import com.sys.OrderSystem.Entity.Menu;
import com.sys.OrderSystem.Service.MenuService;
import com.sys.OrderSystem.Service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import com.sys.OrderSystem.Repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/menu")
public class MenuManagementController {

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MenuService menuService;

    // 메뉴 관리 페이지를 렌더링
    @GetMapping("/menumanagement")  // /menu/management로 접근
    public String showMenuManagementPage(Model model) {
        try {
            List<Category> categories = menuService.getAllCategories();
            Map<Category, List<Menu>> menusByCategory = menuService.getMenusByCategory();

            model.addAttribute("categories", categories);
            model.addAttribute("menusByCategory", menusByCategory);

            return "menumanagement";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
