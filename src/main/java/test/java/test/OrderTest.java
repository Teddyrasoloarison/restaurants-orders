package test.java.test;

import Entity.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


import static org.junit.Assert.*;

public class OrderTest {
    @Test
    public void testAddDishOrders_AllDishesAvailable() {
        // Création de l'ordre
        Order order = new Order();


        DishOrder pizza = new DishOrder(null, "Pizza", 2.0, 8000.0, null);
        DishOrder pasta = new DishOrder(null, "Pasta", 1.0, 5000.0, null);

        List<DishOrder> dishOrders = Arrays.asList(pizza, pasta);

        List<DishOrder> addedDishes = order.addDishOrders(dishOrders);


        assertEquals(2, addedDishes.size());
    }

    @Test
    public void testAddDishOrders_DishNotAvailable() {
        Order order = new Order();

        DishOrder sushi = new DishOrder(null, "Sushi", 1.0, 12000.0, null);

        List<DishOrder> dishOrders = Arrays.asList(sushi);


        List<DishOrder> addedDishes = order.addDishOrders(dishOrders);

        assertTrue(addedDishes.isEmpty());
    }

    @Test
    public void testGetPaymentStatus_Paid() {
        Order order = new Order();
        order.setAmountPaid(15000.0);
        order.setAmountDue(15000.0);

        assertEquals(OrderPaymentStatus.PAID, order.getPaymentStatus());
    }

    @Test
    public void testGetPaymentStatus_Unpaid() {
        Order order = new Order();
        order.setAmountPaid(10000.0);
        order.setAmountDue(20000.0);

        assertEquals(OrderPaymentStatus.UNPAID, order.getPaymentStatus());
    }

    @Test
    public void testGetTotalPrice() {
        Order order = new Order();

        DishOrder pizza = new DishOrder(null, "Pizza", 2.0, 8000.0, null);
        DishOrder pasta = new DishOrder(null, "Pasta", 1.0, 5000.0, null);

        order.setDishOrderList(Arrays.asList(pizza, pasta));

        assertEquals(21000.0, order.getTotalPrice(), 0.01);
    }




}
