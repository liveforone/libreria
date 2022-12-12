package libreria.libreria.orders.dto;

import libreria.libreria.item.model.Item;
import libreria.libreria.orders.model.OrderStatus;
import libreria.libreria.user.model.Users;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrdersRequest {

    private Long id;
    private Item item;
    private Users users;
    private OrderStatus status;
    private int orderCount;
    private LocalDate createdDate;
}
