package com.sys.OrderSystem.Service;

import com.sys.OrderSystem.Entity.Menu;
import com.sys.OrderSystem.Entity.Order;
import com.sys.OrderSystem.Entity.OrderItem;
import com.sys.OrderSystem.Repository.MenuRepository;
import com.sys.OrderSystem.Repository.OrderItemRepository;
import com.sys.OrderSystem.Repository.OrderRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final OrderItemRepository orderItemRepository;
    private MenuService menuService;

    @Autowired
    public OrderService(OrderRepository orderRepository, MenuRepository menuRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.menuRepository = menuRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional(readOnly = true)
    public Order getOrCreateBasket(Integer tableId) {
        return orderRepository.findByTableIdAndStatus(tableId, false)
                .orElseGet(() -> {
                    Order order = new Order();
                    order.setTableId(tableId);
                    order.setStatus(false);
                    order.setOrderDate(new Date());
                    return orderRepository.save(order);
                });
    }

    @Transactional
    public Order addToBasket(Integer tableId, Integer menuId, Integer quantity) {
        Order basket = getOrCreateBasket(tableId);
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found: " + menuId));

        Optional<OrderItem> existingItem = basket.getOrderItems().stream()
                .filter(item -> item.getMenu().getMenuId().equals(menuId))
                .findFirst();

        if (existingItem.isPresent()) {
            OrderItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setTotalPrice(menu.getPrice() * item.getQuantity());
        } else {
            OrderItem newItem = new OrderItem();
            newItem.setOrder(basket);
            newItem.setMenu(menu);
            newItem.setQuantity(quantity);
            newItem.setTotalPrice(menu.getPrice() * quantity);
            basket.getOrderItems().add(newItem);
        }

        return orderRepository.save(basket);
    }

    @Transactional
    public Order confirmOrder(Integer tableId) {
        Order currentBasket = orderRepository.findByTableIdAndStatus(tableId, false)
                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));

        if (currentBasket.getOrderItems().isEmpty()) {
            throw new RuntimeException("장바구니가 비어있습니다.");
        }

        // 주문 생성
        Order confirmedOrder = new Order();
        confirmedOrder.setTableId(tableId);
        confirmedOrder.setStatus(true);
        // orderDate는 생성자에서 자동 설정됨

        // OrderItem 복사
        List<OrderItem> newItems = currentBasket.getOrderItems().stream()
                .map(item -> {
                    OrderItem newItem = new OrderItem();
                    newItem.setMenu(item.getMenu());
                    newItem.setQuantity(item.getQuantity());
                    newItem.setTotalPrice(item.getTotalPrice());
                    newItem.setOrder(confirmedOrder);
                    return newItem;
                })
                .collect(Collectors.toList());

        confirmedOrder.setOrderItems(newItems);

        // 주문 저장
        Order savedOrder = orderRepository.save(confirmedOrder);

        // 장바구니 비우기
        currentBasket.getOrderItems().clear();
        orderRepository.save(currentBasket);

        return savedOrder;
    }

    @Transactional
    public void removeFromBasket(Integer tableId, Integer menuId) {
        Order basket = getOrCreateBasket(tableId);
        List<OrderItem> items = basket.getOrderItems();

        // OrderItem을 직접 찾아서 제거
        items.removeIf(item -> {
            if (item.getMenu() != null) {
                return item.getMenu().getMenuId().equals(menuId);
            }
            return false;
        });

        // 변경사항 저장
        orderRepository.save(basket);
    }

    public void clearBasket(Integer tableId) {
        Order basket = getOrCreateBasket(tableId);
        basket.getOrderItems().clear();
        orderRepository.save(basket);
    }

    public List<Order> getConfirmedOrders(Integer tableId) {
        return orderRepository.findByTableIdAndStatusOrderByOrderDateDesc(tableId, true);
    }

    @Transactional
    public Order updateBasketItemQuantity(Integer tableId, Integer menuId, Integer quantity) {
        Order basket = getOrCreateBasket(tableId);

        basket.getOrderItems().stream()
                .filter(item -> item.getMenu().getMenuId().equals(menuId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    item.setTotalPrice(item.getMenu().getPrice() * quantity);
                });

        return orderRepository.save(basket);
    }

    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    // 특정 상태(true/완료)의 주문 항목만 조회
    public List<OrderItem> getCompletedOrderItems() {
        return orderItemRepository.findByOrder_StatusOrderByOrder_OrderDateDesc(true);
    }

    @Transactional
    public void deleteMenu(Integer menuId) {
        // 해당 메뉴를 참조하는 모든 주문 내역 삭제
        List<OrderItem> relatedOrderItems = orderItemRepository.findByMenu_MenuId(menuId);
        orderItemRepository.deleteAll(relatedOrderItems);

        // 메뉴 삭제
        menuService.deleteMenu(menuId);
    }
}