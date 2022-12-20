package libreria.libreria.orders.util;

import libreria.libreria.orders.model.Orders;

import java.time.LocalDate;

public class OrdersUtils {

    /*
     * 주문 취소 가능 검증 - 주문 취소 가능 날짜를 넘겼는가?
     * 반환 값 : true(넘겼을때), false(유효할때)
     */
    public static boolean isOverCancelLimitDate(Orders orders) {
        int cancelLimitDate = orders.getCreatedDate().getDayOfYear() + 7;  //생성날짜 + 7일
        int nowDate = LocalDate.now().getDayOfYear();

        return nowDate > cancelLimitDate;
    }

    /*
    * 품절 확인 메소드
    * 반환 값 : true(품절일때), false(품절아닐때)
     */
    public static boolean isSoldOut(int remaining) {
        return remaining <= 0;
    }

    /*
    * 주문 수량이 재고수량을 넘기는지 체크하는 메소드
    * 반환 값 : true(넘길때 - 주문 불가능), false(안넘길때 - 주문 가능)
     */
    public static boolean isOverRemaining(int remaining, int orderCount) {
        return remaining - orderCount <= 0;
    }
}
