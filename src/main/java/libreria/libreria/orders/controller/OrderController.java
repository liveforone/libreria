package libreria.libreria.orders.controller;

import libreria.libreria.item.model.Item;
import libreria.libreria.item.service.ItemService;
import libreria.libreria.orders.dto.OrdersRequest;
import libreria.libreria.orders.dto.OrdersResponse;
import libreria.libreria.orders.model.Orders;
import libreria.libreria.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final ItemService itemService;

    /*
    item detail에서 게시글 수정 때문에 현재 접속 유저(principal)을 보내주었다.(판별을 위해)
    그것으로 판별된 유저(게시글 작성자)는 해당 링크로 접속해서 해당 상품의 주문리스트를 볼 수 있다.
     */
    @GetMapping("/item/order-list/{itemId}")
    public ResponseEntity<List<OrdersResponse>> itemDetailOrderList(@PathVariable("itemId") Long itemId) {
        List<OrdersResponse> ordersList =
                orderService.getOrderListForItemDetail(itemId);

        return ResponseEntity.ok(ordersList);
    }

    @GetMapping("/item/order/{itemId}")
    public ResponseEntity<?> orderPage(@PathVariable("itemId") Long itemId) {
        Item item = itemService.getItemEntity(itemId);

        return ResponseEntity.ok(
                Objects.requireNonNullElse(item, "해당 상품이 없어 주문이 불가능합니다.")
        );
    }

    @PostMapping("/item/order/{itemId}")
    public ResponseEntity<?> order(
            @PathVariable("itemId") Long itemId,
            @RequestBody OrdersRequest ordersRequest,
            Principal principal
            ) {
        Item item = itemService.getItemEntity(itemId);

        if (item == null) {
            return ResponseEntity.ok("해당 상품이 없어 주문이 불가능합니다.");
        }

        if (item.getRemaining() <= 0) {
            log.info("품절입니다.");
            return ResponseEntity.ok("품절된 상품입니다. 상품 홈으로 돌아가주세요");
        }

        if (item.getRemaining() - ordersRequest.getOrderCount() <= 0) {
            log.info("주문 불가능, 수량이 재고보다 많음.");
            return ResponseEntity.ok("주문 수량이 재고보다 많아 주문이 불가능합니다.");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/item/" + itemId));

        orderService.saveOrder(
                itemId,
                ordersRequest,
                principal.getName()
        );
        log.info("주문 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @GetMapping("/item/cancel/{orderId}")
    public ResponseEntity<?> cancelPage(@PathVariable("orderId") Long orderId) {
        OrdersResponse order = orderService
                .entityToDtoDetail(orderService.getOrderEntity(orderId));

        return ResponseEntity.ok(
                Objects.requireNonNullElse(
                        order,
                        "해당 주문이 없어 주문취소가 불가능합니다.")
        );
    }

    /*
    myPage에 접근은 principal로 현재 객체를 가져와서 접근한다.
    그럼에도 주문을 취소하는 로직은 민감한 부분이므로 본 유저와 현재 객체를 판별한다.
     */
    @PostMapping("/item/cancel/{orderId}")
    public ResponseEntity<?> cancel(
            @PathVariable("orderId") Long orderId,
            Principal principal
    ) {
        Orders orders = orderService.getOrderEntity(orderId);

        if (orders == null) {
            return ResponseEntity
                    .ok("해당 주문을 찾을 수 없어 주문 취소가 불가능합니다.");
        }

        if (!Objects.equals(principal.getName(), orders.getUsers().getEmail())) {
            log.info("작성자와 현재 유저가 달라 주문 취소 불가능");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        //취소가능 : 1, 취소불가능 : -1
        int ableCancelDate = orderService.getOrderDay(orderId);

        if (ableCancelDate != 1) {  //주문 가능 날짜 판별
            log.info("주문 취소 실패!!");
            return ResponseEntity.ok("주문 한지 7일이 지나 주문 취소가 불가능합니다.");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(
                "/user/order-list"
        ));

        orderService.cancelOrder(orderId);
        log.info("주문 취소 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }
}
