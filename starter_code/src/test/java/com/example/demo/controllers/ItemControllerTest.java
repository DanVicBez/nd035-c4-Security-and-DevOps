package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

public class ItemControllerTest {
  private ItemController itemController;
  private ItemRepository itemRepository = mock(ItemRepository.class);

  @Before
  public void setUp() {
    Item item = new Item();
    item.setDescription("A widget that is round");
    item.setId(1L);
    item.setName("Round Widget");
    item.setPrice(BigDecimal.valueOf(2.99));
    when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(itemRepository.findByName("Round Widget")).thenReturn(Arrays.asList(item));

    Item item2 = new Item();
    item2.setDescription("A widget that is square");
    item2.setId(2L);
    item2.setName("Square Widget");
    item2.setPrice(BigDecimal.valueOf(1.99));
    when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));
    when(itemRepository.findByName("Square Widget")).thenReturn(Arrays.asList(item2));
    
    when(itemRepository.findAll()).thenReturn(Arrays.asList(item, item2));
    
    itemController = new ItemController();
    TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
  }
  
  @Test
  public void testGetAllItems() {
    ResponseEntity<List<Item>> response = itemController.getItems();
    assertNotNull(response);
    List<Item> items = response.getBody();
    assertNotNull(items);
    assertEquals(2, items.size());
  }
  
  @Test
  public void testFindById() {
    ResponseEntity<Item> response = itemController.getItemById(1L);
    assertNotNull(response);
    Item item = response.getBody();
    assertNotNull(item);
    assertEquals(1L, item.getId().longValue());
    assertEquals("A widget that is round", item.getDescription());
    assertEquals("Round Widget", item.getName());
    assertEquals(BigDecimal.valueOf(2.99), item.getPrice());
  }
  
  @Test
  public void testGetItemsByName() {
    ResponseEntity<List<Item>> response = itemController.getItemsByName("Square Widget");
    assertNotNull(response);
    List<Item> items = response.getBody();
    assertNotNull(items);
    assertEquals(1, items.size());
    Item item = items.get(0);
    assertEquals(2L, item.getId().longValue());
    assertEquals("A widget that is square", item.getDescription());
    assertEquals("Square Widget", item.getName());
    assertEquals(BigDecimal.valueOf(1.99), item.getPrice());
  }
}
