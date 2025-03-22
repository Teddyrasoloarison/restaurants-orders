package Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class DishOrder {
    private Long id;
    private String name;
    private Double quantityToOrder;
    private Double price;
    private Order order;

}
