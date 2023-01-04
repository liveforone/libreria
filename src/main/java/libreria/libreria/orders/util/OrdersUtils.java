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
        LocalDate createdDate = orders.getCreatedDate();
        int orderDate = createdDate.getDayOfYear();

        int nowYear = LocalDate.now().getYear();
        int nowDate = LocalDate.now().getDayOfYear();

        int cancelLimitDate = switch (orderDate) {
            case 359 -> LocalDate.of(nowYear, 1, 1).getDayOfYear();
            case 360 -> LocalDate.of(nowYear, 1, 2).getDayOfYear();
            case 361 -> LocalDate.of(nowYear, 1, 3).getDayOfYear();
            case 362 -> LocalDate.of(nowYear, 1, 4).getDayOfYear();
            case 363 -> LocalDate.of(nowYear, 1, 5).getDayOfYear();
            case 364 -> LocalDate.of(nowYear, 1, 6).getDayOfYear();
            case 365 -> LocalDate.of(nowYear, 1, 7).getDayOfYear();
            default -> orderDate + 7;
        };

        return nowDate > cancelLimitDate;
    }
}
