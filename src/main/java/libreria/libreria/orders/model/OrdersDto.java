package libreria.libreria.orders.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import libreria.libreria.item.model.Item;
import libreria.libreria.user.model.Users;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class OrdersDto {

    private Long id;
    private Item item;
    private Users users;
    private OrderStatus status;
    private LocalDate createdDate;

    public Orders toEntity() {
        return Orders.builder()
                .id(id)
                .item(item)
                .users(users)
                .status(status)
                .build();
    }

    @Builder
    public OrdersDto(Long id, Item item, Users users, OrderStatus status) {
        this.id = id;
        this.item = item;
        this.users = users;
        this.status = status;
    }
}
