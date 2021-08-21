package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

public class OrderControllerTest {
  private OrderController orderController;
  private UserRepository userRepository = mock(UserRepository.class);
  private OrderRepository orderRepository = mock(OrderRepository.class);
  
  @Before
  public void setUp() {
    orderController = new OrderController();
    TestUtils.injectObjects(orderController, "userRepository", userRepository);
    TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
  }

  @Test
  public void testSubmitAndGetOrder() {
    User user = new User();
    user.setUsername("danvicbez");
    Cart cart = new Cart();

    Item item = new Item();
    item.setDescription("A widget that is round");
    item.setId(1L);
    item.setName("Round Widget");
    item.setPrice(BigDecimal.valueOf(2.99));

    Item item2 = new Item();
    item2.setDescription("A widget that is square");
    item2.setId(2L);
    item2.setName("Square Widget");
    item2.setPrice(BigDecimal.valueOf(1.99));

    cart.setItems(Arrays.asList(item, item2));
    cart.setUser(user);
    cart.setTotal(item.getPrice().add(item2.getPrice()));
    user.setCart(cart);
    when(userRepository.findByUsername("danvicbez")).thenReturn(user);
    
    ResponseEntity<UserOrder> response = orderController.submit("unknown");
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    
    response = orderController.submit("danvicbez");
    assertNotNull(response);
    UserOrder order = response.getBody();
    assertNotNull(order);
    assertEquals(2, order.getItems().size());
    assertEquals(BigDecimal.valueOf(4.98), order.getTotal());
    
    when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(order));
    
    ResponseEntity<List<UserOrder>> response2 = orderController.getOrdersForUser("danvicbez");
    assertNotNull(response2);
    List<UserOrder> orders = response2.getBody();
    assertNotNull(orders);
    assertEquals(1, orders.size());
    order = orders.get(0);
    assertEquals(2, order.getItems().size());
    assertEquals(BigDecimal.valueOf(4.98), order.getTotal());
  }
}
