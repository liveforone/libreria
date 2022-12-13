package libreria.libreria.orders.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrdersConstants {
    CAN_CANCEL(1),
    SOLD_OUT(0),
    CANT_CANCEL(0);

    private int value;
}
