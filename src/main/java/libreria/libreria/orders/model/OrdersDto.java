package libreria.libreria.orders.model;

import libreria.libreria.item.model.Item;
import libreria.libreria.user.model.Users;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class OrdersDto {

    private Long id;
    private Item item;
    private Users users;
    private OrderStatus status;
    private int orderCount;
    private LocalDate createdDate;

    public Orders toEntity() {
        return Orders.builder()
                .id(id)
                .item(item)
                .users(users)
                .status(status)
                .orderCount(orderCount)
                .build();
    }
}
