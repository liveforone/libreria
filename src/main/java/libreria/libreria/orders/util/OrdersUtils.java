package libreria.libreria.orders.util;

import libreria.libreria.orders.model.Orders;

import java.time.LocalDate;

public class OrdersUtils {

    public static boolean isSoldOut(int remaining) {
        return remaining <= 0;
    }

    public static boolean isOverRemaining(int remaining, int orderCount) {
        return remaining - orderCount <= 0;
    }

    /*
     * 주문 취소 가능 검증 - 주문 취소 가능 날짜를 넘겼는가?
     * 반환 값 : true(넘겼을때), false(유효할때)
     */
    public static boolean isOverCancelLimitDate(Orders orders) {
        int cancelLimitDate = orders.getCreatedDate().getDayOfYear() + 7;  //생성날짜 + 7일
        int nowDate = LocalDate.now().getDayOfYear();

        return nowDate > cancelLimitDate;
    }
}
