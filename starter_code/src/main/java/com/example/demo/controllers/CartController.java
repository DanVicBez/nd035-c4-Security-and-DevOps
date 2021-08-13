package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {
  private static final Logger logger = LoggerFactory.getLogger(CartController.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CartRepository cartRepository;

  @Autowired
  private ItemRepository itemRepository;

  @PostMapping("/addToCart")
  public ResponseEntity<Cart> addToCart(@RequestBody ModifyCartRequest request) {
    logger.info("Adding to cart for user {}", request.getUsername());
    User user = userRepository.findByUsername(request.getUsername());
    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    Optional<Item> item = itemRepository.findById(request.getItemId());
    if (!item.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    Cart cart = user.getCart();
    logger.debug("Adding {} of item #{} to cart", request.getQuantity(), request.getItemId());
    IntStream.range(0, request.getQuantity()).forEach(i -> cart.addItem(item.get()));

    cartRepository.save(cart);
    logger.info("Successfully added to cart");
    return ResponseEntity.ok(cart);
  }

  @PostMapping("/removeFromCart")
  public ResponseEntity<Cart> removeFromCart(@RequestBody ModifyCartRequest request) {
    logger.info("Removing from cart for user {}", request.getUsername());
    User user = userRepository.findByUsername(request.getUsername());
    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    Optional<Item> item = itemRepository.findById(request.getItemId());
    if (!item.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    Cart cart = user.getCart();
    logger.debug("Removing {} of item #{} from cart", request.getQuantity(), request.getItemId());
    IntStream.range(0, request.getQuantity()).forEach(i -> cart.removeItem(item.get()));

    cartRepository.save(cart);
    logger.info("Successfully removed from cart");
    return ResponseEntity.ok(cart);
  }
}
