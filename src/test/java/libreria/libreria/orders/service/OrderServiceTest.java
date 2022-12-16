package libreria.libreria.orders.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {

    @Test
    @DisplayName("주문 취소 날짜 테스트")
    void getOrderAbleCancelDayTest() {
        //테스트 당시 날짜 기준으로 한참 전 날짜임
        LocalDate localDate = LocalDate.of(2022, 9, 8);
        int ableDate = localDate.getDayOfYear() + 7;  //생성날짜 + 7일
        int nowDate = LocalDate.now().getDayOfYear();

        int ok;

        if (nowDate <= ableDate) {
            ok = 1;  //주문취소 가능, 1 == true라는 뜻 => 실제 리턴값임
        } else {
            ok = -1;  //주문취소 불가능, -1 == False라는 뜻 => 실제 리턴값임
        }

        assertThat(ok).isEqualTo(-1);
    }
}