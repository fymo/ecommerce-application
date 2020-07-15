package com.example.demo.controller;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @Before
    public void setup(){

        when(userRepository.findByUsername("fymo")).thenReturn(createUser());
        when(itemRepository.findById(any())).thenReturn(Optional.of(getItem(1)));

    }



    @Test
    public void verify_addToCart(){
        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(3);
        request.setItemId(1);
        request.setUsername("fymo");

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart actualCart = response.getBody();

        Cart generatedCart = createCart(createUser());

        assertNotNull(actualCart);

        Item item = getItem(request.getItemId());
        BigDecimal itemPrice = item.getPrice();

        BigDecimal expectedTotal = itemPrice.multiply(BigDecimal.valueOf(request.getQuantity())).add(generatedCart.getTotal());

        assertEquals("fymo", actualCart.getUser().getUsername());
        assertEquals(generatedCart.getItems().size() + request.getQuantity(), actualCart.getItems().size());
        assertEquals(getItem(1), actualCart.getItems().get(0));
        assertEquals(expectedTotal, actualCart.getTotal());

        verify(cartRepository, times(1)).save(actualCart);

    }

    @Test
    public void verify_removeFromCart(){

        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(1);
        request.setItemId(1);
        request.setUsername("fymo");

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart actualCart = response.getBody();

        Cart generatedCart = createCart(createUser());

        assertNotNull(actualCart);

        Item item = getItem(request.getItemId());
        BigDecimal itemPrice = item.getPrice();

        BigDecimal expectedTotal = generatedCart.getTotal().subtract(itemPrice.multiply(BigDecimal.valueOf(request.getQuantity())));

        assertEquals("fymo", actualCart.getUser().getUsername());
        assertEquals(generatedCart.getItems().size() - request.getQuantity(), actualCart.getItems().size());
        assertEquals(getItem(2), actualCart.getItems().get(0));
        assertEquals(expectedTotal, actualCart.getTotal());

        verify(cartRepository, times(1)).save(actualCart);

    }

    @Test
    public void verify_InvalidUsername(){

        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(1);
        request.setItemId(1);
        request.setUsername("invalidUser");

        ResponseEntity<Cart> removeResponse = cartController.removeFromcart(request);
        assertNotNull(removeResponse);
        assertEquals(404, removeResponse.getStatusCodeValue());

        ResponseEntity<Cart> addResponse = cartController.addTocart(request);
        assertNotNull(addResponse);
        assertEquals(404, addResponse.getStatusCodeValue());

        verify(userRepository, times(2)).findByUsername("invalidUser");

    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("fymo");
        user.setPassword("password");
        user.setCart(createCart(user));

        return user;
    }

    private Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setId(1L);
        List<Item> items = getItems();
        cart.setItems(getItems());
        cart.setTotal(items.stream().map(item -> item.getPrice()).reduce(BigDecimal::add).get());
        cart.setUser(user);

        return cart;
    }

    private List<Item> getItems() {

        List<Item> items = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            items.add(getItem(i));
        }

        return items;
    }

    private Item getItem(long id){
        Item item = new Item();
        item.setId(id);

        item.setPrice(BigDecimal.valueOf(id * 1.2));

        item.setName("Item " + item.getId());

        item.setDescription("Description ");
        return item;
    }

}
