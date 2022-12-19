package libreria.libreria.orders.util;

import libreria.libreria.orders.model.Orders;

import java.time.LocalDate;

public class OrdersUtils {

    /*
     * 주문 취소 가능 검증
     */
    public static int getOrderDay(Orders orders) {
        int cancelLimitDate = orders.getCreatedDate().getDayOfYear() + 7;  //생성날짜 + 7일
        int nowDate = LocalDate.now().getDayOfYear();

        if (nowDate <= cancelLimitDate) {
            return OrdersConstants.CAN_CANCEL.getValue();
        }
        return OrdersConstants.CANT_CANCEL.getValue();
    }
}
