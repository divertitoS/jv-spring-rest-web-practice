package mate.academy.spring.controller;

import mate.academy.spring.dao.OrderDao;
import mate.academy.spring.dao.UserDao;
import mate.academy.spring.model.Order;
import mate.academy.spring.model.ShoppingCart;
import mate.academy.spring.model.User;
import mate.academy.spring.model.dto.response.OrderResponseDto;
import mate.academy.spring.service.OrderService;
import mate.academy.spring.service.ShoppingCartService;
import mate.academy.spring.service.UserService;
import mate.academy.spring.service.dto.mapping.DtoResponseMapper;
import mate.academy.spring.service.dto.mapping.impl.response.OrderResponseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final DtoResponseMapper<OrderResponseDto, Order> dtoResponseMapper;
    private final ShoppingCartService shoppingCartService;

    public OrderController(OrderService orderService,
                           UserService userService,
                           ShoppingCartService shoppingCartService, DtoResponseMapper<OrderResponseDto, Order> dtoResponseMapper) {
        this.orderService = orderService;
        this.userService = userService;
        this.shoppingCartService = shoppingCartService;
        this.dtoResponseMapper = dtoResponseMapper;
    }

    @GetMapping
    public List<OrderResponseDto> getOrdersByUserId(@RequestParam Long userId) {
        User user = userService.getById(userId);
        List<Order> listOfOrders = orderService.getOrdersHistory(user);
        return listOfOrders.stream()
                .map(dtoResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/complete")
    public OrderResponseDto makeAnOrder(@RequestParam Long userId) {
        User currentUser = userService.getById(userId);
        ShoppingCart currentUserShoppingCart = shoppingCartService.getByUser(currentUser);
        Order currentUserOrder = orderService.completeOrder(currentUserShoppingCart);
        return dtoResponseMapper.toDto(currentUserOrder);
    }
}
