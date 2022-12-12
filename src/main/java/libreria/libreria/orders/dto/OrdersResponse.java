package libreria.libreria.orders.dto;

import libreria.libreria.orders.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdersResponse {

    private Long id;
    private OrderStatus status;
    private int orderCount;
    private LocalDate createdDate;
}
