package libreria.libreria.orders.service;

import jakarta.persistence.EntityManager;
import libreria.libreria.item.dto.ItemRequest;
import libreria.libreria.item.service.ItemService;
import libreria.libreria.orders.dto.OrdersRequest;
import libreria.libreria.orders.model.OrderStatus;
import libreria.libreria.user.dto.UserRequest;
import libreria.libreria.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    public Long createUserAndItem(String email, String password, String title) {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(email);
        userRequest.setPassword(password);
        userService.signup(userRequest);
        em.flush();
        em.clear();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setTitle(title);
        return itemService.saveItem(itemRequest, email);
    }

    public Long createOrder(
            int orderCount,
            Long itemId,
            String email
    ) {
        OrdersRequest ordersRequest = new OrdersRequest();
        ordersRequest.setOrderCount(orderCount);
        return orderService.saveOrder(
                itemService.getItemEntity(itemId),
                ordersRequest,
                email
        );
    }

    @Test
    @Transactional
    void saveOrderTest() {
        //given
        String email = "yc1111@naver.com";
        String password = "1111";
        String itemTitle = "test_title";
        Long itemId = createUserAndItem(email, password, itemTitle);
        em.flush();
        em.clear();

        //when
        int orderCount = 1;
        Long orderId = createOrder(orderCount, itemId, email);
        em.flush();
        em.clear();

        //then
        assertThat(orderService.getOrderEntity(orderId).getItem().getTitle())
                .isEqualTo(itemTitle);
    }

    @Test
    @Transactional
    void cancelOrderTest() {
        //given
        String email = "yc1111@naver.com";
        String password = "1111";
        String itemTitle = "test_title";
        Long itemId = createUserAndItem(email, password, itemTitle);
        em.flush();
        em.clear();

        int orderCount = 1;
        Long orderId = createOrder(orderCount, itemId, email);
        em.flush();
        em.clear();

        //when
        orderService.cancelOrder(orderId);
        em.flush();
        em.clear();

        //then
        assertThat(orderService.getOrderEntity(orderId).getStatus())
                .isEqualTo(OrderStatus.CANCEL);
    }

    @Test
    @DisplayName("주문 취소 날짜 테스트")
    void getOrderAbleCancelDayTest() {
        LocalDate createdDate = LocalDate.of(2022, 12, 26);
        int orderDate = createdDate.getDayOfYear();

        int nowYear = LocalDate.now().getYear();
        int nowDate = LocalDate.of(2023, 1, 3).getDayOfYear();

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

        //when
        boolean isOverCancelDate = nowDate > cancelLimitDate;

        //then
        assertThat(isOverCancelDate)
                .isTrue();
    }
}