package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static void injectObjects(Object target, String fieldName, Object toInject) {

        boolean wasPrivate = false;

        try {
            Field declaredField = target.getClass().getDeclaredField(fieldName);
            if(!declaredField.isAccessible()){
                declaredField.setAccessible(true);
                wasPrivate = true;
            }

            declaredField.set(target, toInject);
            if(wasPrivate){
                declaredField.setAccessible(false);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("fymo");
        user.setPassword("password");
        user.setCart(createCart(user));

        return user;
    }

    public static Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setId(1L);
        List<Item> items = getItems();
        cart.setItems(getItems());
        cart.setTotal(items.stream().map(item -> item.getPrice()).reduce(BigDecimal::add).get());
        cart.setUser(user);

        return cart;
    }

    public static List<Item> getItems() {

        List<Item> items = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            items.add(getItem(i));
        }

        return items;
    }

    public static Item getItem(long id){
        Item item = new Item();
        item.setId(id);

        item.setPrice(BigDecimal.valueOf(id * 1.2));

        item.setName("Item " + item.getId());

        item.setDescription("Description ");
        return item;
    }

}
