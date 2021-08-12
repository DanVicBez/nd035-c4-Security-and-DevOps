package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.apache.jasper.tagplugins.jstl.core.When;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

public class UserControllerTest {
  private UserController userController;
  private UserRepository userRepository = mock(UserRepository.class);
  private CartRepository cartRepository = mock(CartRepository.class);
  private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
  
  @Before
  public void setUp() {
    userController = new UserController();
    TestUtils.injectObjects(userController, "userRepository", userRepository);
    TestUtils.injectObjects(userController, "cartRepository", cartRepository);
    TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
  }
  
  @Test
  public void testCreateUser() {
    when(encoder.encode("password")).thenReturn("hashed");
    CreateUserRequest req = new CreateUserRequest();
    req.setUsername("danvicbez");
    req.setPassword("password");
    req.setConfirmPassword("password");
    
    ResponseEntity<User> response = userController.createUser(req);
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    
    User user = response.getBody();
    assertNotNull(user);
    assertEquals(0, user.getId());
    assertEquals("danvicbez", user.getUsername());
    assertEquals("hashed", user.getPassword());
  }
  
  @Test
  public void testCreateUserInvalidPassword() {
    CreateUserRequest req = new CreateUserRequest();
    req.setUsername("danvicbez");
    req.setPassword("short");
    req.setConfirmPassword("short");
    
    ResponseEntity<User> response = userController.createUser(req);
    assertNotNull(response);
    assertEquals(400, response.getStatusCodeValue());

    req.setUsername("danvicbez");
    req.setPassword("password1");
    req.setConfirmPassword("password2");
    
    response = userController.createUser(req);
    assertNotNull(response);
    assertEquals(400, response.getStatusCodeValue());
  }
  
  @Test
  public void testFindById() {
    User expected = new User();
    expected.setId(0L);
    expected.setUsername("danvicbez");
    expected.setPassword("hashed");
    when(userRepository.findById(0L)).thenReturn(Optional.of(expected));
    
    ResponseEntity<User> response = userController.findById(0L);
    assertNotNull(response);

    User user = response.getBody();
    assertNotNull(user);
    assertEquals(0, user.getId());
    assertEquals("danvicbez", user.getUsername());
    assertEquals("hashed", user.getPassword());
  }

  @Test
  public void testFindByUsername() {
    User expected = new User();
    expected.setId(0L);
    expected.setUsername("danvicbez");
    expected.setPassword("hashed");
    when(userRepository.findByUsername("danvicbez")).thenReturn(expected);
    
    ResponseEntity<User> response = userController.findByUserName("danvicbez");
    assertNotNull(response);

    User user = response.getBody();
    assertNotNull(user);
    assertEquals(0, user.getId());
    assertEquals("danvicbez", user.getUsername());
    assertEquals("hashed", user.getPassword());
  }
}
