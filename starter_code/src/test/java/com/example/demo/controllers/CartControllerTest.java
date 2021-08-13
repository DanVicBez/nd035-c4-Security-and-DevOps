package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

public class CartControllerTest {
  private CartController cartController;
  private UserRepository userRepository = mock(UserRepository.class);
  private CartRepository cartRepository = mock(CartRepository.class);
  private ItemRepository itemRepository = mock(ItemRepository.class);
  
  @Before
  public void setUp() {
    cartController = new CartController();
    TestUtils.injectObjects(cartController, "userRepository", userRepository);
    TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
    TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
  }
  
  @Test
  public void testAddToCart() {
    User mockUser = new User();
    mockUser.setCart(new Cart());
    when(userRepository.findByUsername("danvicbez")).thenReturn(mockUser);
    
    Item mockItem = new Item();
    mockItem.setPrice(BigDecimal.valueOf(1.5));
    when(itemRepository.findById(0L)).thenReturn(Optional.of(mockItem));
    
    ModifyCartRequest req = new ModifyCartRequest();
    req.setUsername("danvicbez");
    req.setItemId(0L);
    req.setQuantity(2);
    
    ResponseEntity<Cart> response = cartController.addToCart(req);
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    
    Cart cart = response.getBody();
    assertNotNull(cart);
    assertEquals(BigDecimal.valueOf(3.0), cart.getTotal());
    assertEquals(2, cart.getItems().size());
  }
  
  @Test
  public void testAddToCartInvalidUser() {
    ModifyCartRequest req = new ModifyCartRequest();
    req.setUsername("danvicbez");
    req.setItemId(0L);
    req.setQuantity(2);
    
    ResponseEntity<Cart> response = cartController.addToCart(req);
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }
  
  @Test
  public void testAddToCartInvalidItem() {
    User mockUser = new User();
    mockUser.setCart(new Cart());
    when(userRepository.findByUsername("danvicbez")).thenReturn(mockUser);
    
    ModifyCartRequest req = new ModifyCartRequest();
    req.setUsername("danvicbez");
    req.setItemId(0L);
    req.setQuantity(2);
    
    ResponseEntity<Cart> response = cartController.addToCart(req);
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }
  
  @Test
  public void testRemoveFromCart() {
    User mockUser = new User();
    Cart mockCart = new Cart();
    Item mockItem = new Item();
    mockItem.setId(0L);
    mockItem.setPrice(BigDecimal.valueOf(1.5));
    mockCart.addItem(mockItem);
    mockCart.addItem(mockItem);
    mockUser.setCart(mockCart);
    when(userRepository.findByUsername("danvicbez")).thenReturn(mockUser);
    when(itemRepository.findById(0L)).thenReturn(Optional.of(mockItem));

    ModifyCartRequest req = new ModifyCartRequest();
    req.setUsername("danvicbez");
    req.setItemId(0L);
    req.setQuantity(2);

    ResponseEntity<Cart> response = cartController.removeFromCart(req);
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    
    Cart cart = response.getBody();
    assertNotNull(cart);
    assertEquals(0, cart.getItems().size());
    assertEquals(BigDecimal.valueOf(0.0), cart.getTotal());
  }
  
  @Test
  public void testRemoveFromCartInvalidUser() {
    ModifyCartRequest req = new ModifyCartRequest();
    req.setUsername("danvicbez");
    req.setItemId(0);
    req.setQuantity(2);

    ResponseEntity<Cart> response = cartController.removeFromCart(req);
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }
  
  @Test
  public void testRemoveFromCartInvalidItem() {
    User mockUser = new User();
    mockUser.setCart(new Cart());
    when(userRepository.findByUsername("danvicbez")).thenReturn(mockUser);

    ModifyCartRequest req = new ModifyCartRequest();
    req.setUsername("danvicbez");
    req.setItemId(0);
    req.setQuantity(2);

    ResponseEntity<Cart> response = cartController.removeFromCart(req);
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }
}