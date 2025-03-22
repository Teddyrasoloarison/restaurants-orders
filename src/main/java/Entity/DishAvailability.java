package Entity;

import DAO.DishAvailabilityDao;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DishAvailability {
    private String name;
    private Double availability;

    public List<DishAvailability> getAllDishAvailability(DishAvailabilityDao dishAvailabilityDao) {
        return dishAvailabilityDao.findAll();
    }

    public DishAvailability getDishAvailabilityByName(DishAvailabilityDao dishAvailabilityDao, String name) {
        DishAvailability dishAvailability = dishAvailabilityDao.findByName(name);

        if (dishAvailability == null) {
            System.out.println("Dish availability not found for: " + name);
        }
        return dishAvailability;
    }
}
