package com.sys.OrderSystem.Controller;

import com.sys.OrderSystem.DTO.BasketDTO;
import com.sys.OrderSystem.Entity.Order;
import com.sys.OrderSystem.Entity.OrderItem;
import com.sys.OrderSystem.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderApiController {
    private final OrderService orderService;

    @Autowired
    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/basket/{tableId}")
    public ResponseEntity<Order> getBasket(@PathVariable Integer tableId) {
        try {
            Order basket = orderService.getOrCreateBasket(tableId);
            return ResponseEntity.ok(basket);
        } catch (Exception e) {
            e.printStackTrace();  // 로그 확인용
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/basket/add")
    public ResponseEntity<Order> addToBasket(@RequestParam Integer tableId,
                                             @RequestParam Integer menuId,
                                             @RequestParam Integer quantity) {
        try {
            Order basket = orderService.addToBasket(tableId, menuId, quantity);
            return ResponseEntity.ok(basket);
        } catch (Exception e) {
            e.printStackTrace();  // 로그 확인용
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/basket/update")
    public ResponseEntity<Order> updateBasketItem(
            @RequestParam Integer tableId,
            @RequestParam Integer menuId,
            @RequestParam Integer quantity) {
        try {
            if (quantity <= 0) {
                orderService.removeFromBasket(tableId, menuId);
                return ResponseEntity.ok(orderService.getOrCreateBasket(tableId));
            } else {
                Order basket = orderService.updateBasketItemQuantity(tableId, menuId, quantity);
                return ResponseEntity.ok(basket);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/basket/remove")
    public ResponseEntity<Order> removeFromBasket(
            @RequestParam Integer tableId,
            @RequestParam Integer menuId) {
        try {
            // 삭제 작업 수행
            orderService.removeFromBasket(tableId, menuId);

            // 삭제 후의 장바구니 상태 조회
            Order updatedBasket = orderService.getOrCreateBasket(tableId);

            return ResponseEntity.ok(updatedBasket);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/basket/clear")
    public ResponseEntity<?> clearBasket(@RequestParam Integer tableId) {
        try {
            orderService.clearBasket(tableId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/orders/confirm")
    public ResponseEntity<?> confirmOrder(@RequestParam Integer tableId) {
        try {
            Order confirmedOrder = orderService.confirmOrder(tableId);
            return ResponseEntity.ok(Map.of(
                    "message", "주문이 완료되었습니다.",
                    "orderId", confirmedOrder.getOrderId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "주문 처리 중 오류가 발생했습니다."
            ));
        }
    }

    @GetMapping("/orders/list")
    public ResponseEntity<List<OrderItem>> getOrderList() {
        try {
            List<OrderItem> orderItems = orderService.getCompletedOrderItems();
            // 로그 추가
            System.out.println("Returning order items: " + orderItems);
            return ResponseEntity.ok(orderItems);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
