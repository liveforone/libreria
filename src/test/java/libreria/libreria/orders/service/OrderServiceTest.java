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
        //given
        LocalDate createdDate = LocalDate.of(2022, 9, 8);
        int cancelAvailableDate = createdDate.getDayOfYear() + 7;
        int nowDate = LocalDate.now().getDayOfYear();

        //when
        int ok;

        if (nowDate <= cancelAvailableDate) {
            ok = 1;
        } else {
            ok = -1;
        }

        //then
        assertThat(ok).isEqualTo(-1);
    }
}