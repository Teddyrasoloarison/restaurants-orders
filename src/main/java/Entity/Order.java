package Entity;

import DAO.DishAvailabilityDao;
import DB.DataSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor


public class Order {
    private Long id;
    private TableNumber tableNumber;
    private Double amountPaid;
    private Double amountDue;
    private Instant customerArrivalDateTime;
    private OrderPaymentStatus status;
    private List<DishOrder> dishOrderList;

    public Order() {
    }

    public List<DishOrder> addDishOrders(List<DishOrder> dishOrderList) {
        List<DishOrder> successfullyAddedDishes = new ArrayList<>();

        DataSource dataSource = new DataSource();

        DishAvailabilityDao dishAvailabilityDao = new DishAvailabilityDao(dataSource);

        // Utilise dishAvailabilityDao pour connaître la disponibilité des plats
        for (DishOrder dishOrder : dishOrderList) {
            DishAvailability dishAvailability = dishAvailabilityDao.findByName(dishOrder.getName());

            if (dishAvailability == null || dishAvailability.getAvailability() < dishOrder.getQuantityToOrder()) {
                System.out.println("Ordered canceled: this " + dishOrder.getName() + " dish is not available");
                return new ArrayList<>();
            }

            successfullyAddedDishes.add(dishOrder);
        }

        return successfullyAddedDishes;
    }


    public OrderPaymentStatus getPaymentStatus() {
        return (amountPaid >= amountDue) ? OrderPaymentStatus.PAID : OrderPaymentStatus.UNPAID;
    }

    public Double getTotalPrice() {
        return dishOrderList.stream()
                .mapToDouble(dish -> dish.getPrice() * dish.getQuantityToOrder())
                .sum();
    }
}
