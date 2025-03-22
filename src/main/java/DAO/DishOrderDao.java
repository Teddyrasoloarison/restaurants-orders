package DAO;

import DB.DataSource;
import Entity.DishOrder;
import Entity.Order;
import Entity.OrderPaymentStatus;
import Entity.TableNumber;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DishOrderDao {
    private DataSource dataSource;

    public DishOrderDao(DataSource dataSource) {
        this.dataSource = this.dataSource;
    }

    public List<DishOrder> findAll() {
        List<DishOrder> dishOrders = new ArrayList<>();
        String sql = "SELECT * FROM dish_order";

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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

    public DishOrder findById(Long id) {
        String sql = "SELECT * FROM dish_order WHERE id = ?";
        DishOrder dishOrder = null;

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                dishOrder = new DishOrder();
                dishOrder.setId(rs.getLong("id"));
                dishOrder.setName(rs.getString("name"));
                dishOrder.setQuantityToOrder(rs.getDouble("quantityToOrder"));
                dishOrder.setPrice(rs.getDouble("price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dishOrder;
    }

    public List<DishOrder> saveAll(List<DishOrder> entities) {
        List<DishOrder> savedDishOrders = new ArrayList<>();

        String sql = "INSERT INTO dish_order (name, quantityToOrder, price, idOrder) " +
                "VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (name, idOrder) DO UPDATE SET " +
                "quantityToOrder = EXCLUDED.quantityToOrder, " +
                "price = EXCLUDED.price " +
                "RETURNING id, name, quantityToOrder, price, idOrder";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (DishOrder dishOrder : entities) {
                statement.setString(1, dishOrder.getName());
                statement.setDouble(2, dishOrder.getQuantityToOrder());
                statement.setDouble(3, dishOrder.getPrice());
                statement.setLong(4, dishOrder.getOrder().getId());
                statement.addBatch();
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    savedDishOrders.add(mapFromResultSet(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saving dish orders", e);
        }

        return savedDishOrders;
    }

    private DishOrder mapFromResultSet(ResultSet resultSet) throws SQLException {
        return new DishOrder(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getDouble("quantityToOrder"),
                resultSet.getDouble("price"),
                new Order(
                        resultSet.getLong("order_id"),
                        TableNumber.valueOf(resultSet.getString("tableNumber")),
                        resultSet.getDouble("amountPaid"),
                        resultSet.getDouble("amountDue"),
                        resultSet.getTimestamp("customerArrivalDateTime").toInstant(),
                        OrderPaymentStatus.valueOf(resultSet.getString("status")),
                        new ArrayList<>()
                )
        );
    }


}
