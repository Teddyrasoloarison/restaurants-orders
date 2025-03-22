package DAO;

import DB.DataSource;
import Entity.DishOrder;
import Entity.Order;
import Entity.OrderPaymentStatus;
import Entity.TableNumber;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OrderDao {
    private DataSource dataSource;
    private DishOrderDao dishOrderDao;

    public OrderDao(DataSource dataSource, DishOrderDao dishOrderDao) {
        this.dataSource = dataSource;
        this.dishOrderDao = dishOrderDao;
    }

    public OrderDao() {
    }

    public OrderDao(Connection connection) {
    }

    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setTableNumber(TableNumber.valueOf(rs.getString("tableNumber")));
                order.setAmountPaid(rs.getDouble("amountPaid"));
                order.setAmountDue(rs.getDouble("amountDue"));
                order.setCustomerArrivalDateTime(rs.getTimestamp("customerArrivalDateTime").toInstant());
                order.setStatus(OrderPaymentStatus.valueOf(rs.getString("order_status")));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public Order findById(Long idOrder) {
        String sql = "SELECT * FROM orders WHERE idOrder = ?";
        Order order = null;

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, idOrder);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                order = new Order();
                order.setId(rs.getLong("idOrder"));
                order.setTableNumber(TableNumber.valueOf(rs.getString("tableNumber")));
                order.setAmountPaid(rs.getDouble("amountPaid"));
                order.setAmountDue(rs.getDouble("amountDue"));
                order.setCustomerArrivalDateTime(rs.getTimestamp("customerArrivalDateTime").toInstant());
                order.setStatus(OrderPaymentStatus.valueOf(rs.getString("order_status")));

                // Récupérer les plats associés à cette commande
                order.setDishOrderList(getDishOrdersByOrderId(idOrder));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    public List<DishOrder> getDishOrdersByOrderId(Long idOrder) {
        List<DishOrder> dishOrders = new ArrayList<>();
        String sql = "SELECT * FROM dish_order WHERE idOrder = ?";

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, idOrder);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DishOrder dishOrder = new DishOrder();
                dishOrder.setId(rs.getLong("id"));
                dishOrder.setName(rs.getString("name"));
                dishOrder.setQuantityToOrder(rs.getDouble("quantityToOrder"));
                dishOrder.setPrice(rs.getDouble("price"));

                dishOrders.add(dishOrder);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dishOrders;
    }

    public List<Order> saveAll(List<Order> entities) {
        List<Order> savedOrders = new ArrayList<>();

        String sql = "INSERT INTO orders (tableNumber, customerArrivalDateTime, amountPaid, amountDue, order_status) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "amountPaid = EXCLUDED.amountPaid, " +
                "amountDue = EXCLUDED.amountDue, " +
                "order_status = EXCLUDED.order_status " +
                "RETURNING id, tableNumber, customerArrivalDateTime, amountPaid, amountDue, order_status";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (Order order : entities) {
                statement.setString(1, order.getTableNumber().name()); // Convertir Enum en String
                statement.setTimestamp(2, Timestamp.from(order.getCustomerArrivalDateTime()));
                statement.setDouble(3, order.getAmountPaid());
                statement.setDouble(4, order.getAmountDue());
                statement.setString(5, order.getStatus().name()); // Convertir Enum en String
                statement.addBatch();
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    savedOrders.add(mapFromResultSet(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saving orders", e);
        }

        return savedOrders;
    }

    private Order mapFromResultSet(ResultSet resultSet) throws SQLException {
        return new Order(
                resultSet.getLong("id"),
                TableNumber.valueOf(resultSet.getString("tableNumber")),
                resultSet.getDouble("amountPaid"),
                resultSet.getDouble("amountDue"),
                resultSet.getTimestamp("customerArrivalDateTime").toInstant(),
                OrderPaymentStatus.valueOf(resultSet.getString("order_status")),
                new ArrayList<>()
        );
    }



}
