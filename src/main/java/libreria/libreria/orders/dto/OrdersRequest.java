package libreria.libreria.orders.dto;

import libreria.libreria.item.model.Item;
import libreria.libreria.orders.model.OrderStatus;
import libreria.libreria.orders.model.Orders;
import libreria.libreria.user.model.Users;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class OrdersRequest {

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
