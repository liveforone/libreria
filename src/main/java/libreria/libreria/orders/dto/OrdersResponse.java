package libreria.libreria.orders.dto;

import libreria.libreria.orders.model.OrderStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class OrdersResponse {

    private Long id;
    private OrderStatus status;
    private int orderCount;
    private LocalDate createdDate;

    @Builder
    public OrdersResponse(Long id, OrderStatus status, int orderCount, LocalDate createdDate) {
        this.id = id;
        this.status = status;
        this.orderCount = orderCount;
        this.createdDate = createdDate;
    }
}
