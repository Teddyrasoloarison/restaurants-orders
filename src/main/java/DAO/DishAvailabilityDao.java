package DAO;

import DB.DataSource;
import Entity.DishAvailability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DishAvailabilityDao {
    private DataSource dataSource;

    public DishAvailabilityDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public List<DishAvailability> findAll() {
        String sql = "SELECT name, availability FROM dish_availability";
        List<DishAvailability> availabilities = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                DishAvailability dishAvailability = new DishAvailability();
                dishAvailability.setName(rs.getString("name"));
                dishAvailability.setAvailability(rs.getDouble("availability"));
                availabilities.add(dishAvailability);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return availabilities;
    }

    public DishAvailability findByName(String dishName) {
        String sql = "SELECT name, availability FROM dish_availability WHERE name = ?";
        DishAvailability dishAvailability = null;  // Initialiser à null, et pas à un nouvel objet à chaque fois
        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, dishName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dishAvailability = new DishAvailability();
                    dishAvailability.setName(rs.getString("name"));
                    dishAvailability.setAvailability(rs.getDouble("availability"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dishAvailability;
    }

    public List<DishAvailability> saveAll(List<DishAvailability> entities) {
        List<DishAvailability> savedDishAvailabilities = new ArrayList<>();

        String sql = "INSERT INTO dish_availability (name, availability) " +
                "VALUES (?, ?) " +
                "ON CONFLICT (name) DO UPDATE SET availability = EXCLUDED.availability " +
                "RETURNING id, name, availability";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (DishAvailability dishAvailability : entities) {
                statement.setString(1, dishAvailability.getName());
                statement.setDouble(2, dishAvailability.getAvailability());
                statement.addBatch(); // Ajouter à la batch pour exécuter toutes les requêtes ensemble
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    savedDishAvailabilities.add(mapFromResultSet(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saving dish availabilities", e);
        }

        return savedDishAvailabilities;
    }
    private DishAvailability mapFromResultSet(ResultSet resultSet) throws SQLException {
        DishAvailability dishAvailability = new DishAvailability();
        dishAvailability.setName(resultSet.getString("name"));
        dishAvailability.setAvailability(resultSet.getDouble("availability"));
        return dishAvailability;
    }

}

