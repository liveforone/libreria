package libreria.libreria.orders.controller;

import jakarta.servlet.http.HttpServletRequest;
import libreria.libreria.item.model.Item;
import libreria.libreria.item.service.ItemService;
import libreria.libreria.orders.dto.OrdersRequest;
import libreria.libreria.orders.dto.OrdersResponse;
import libreria.libreria.orders.model.Orders;
import libreria.libreria.orders.service.OrderService;
import libreria.libreria.orders.util.OrdersUtils;
import libreria.libreria.user.service.UserService;
import libreria.libreria.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping("/item/order/{itemId}")
    public ResponseEntity<?> order(
            @PathVariable("itemId") Long itemId,
            @RequestBody OrdersRequest ordersRequest,
            Principal principal,
            HttpServletRequest request
    ) {
        String email = principal.getName();
        Item item = itemService.getItemEntity(itemId);

        if (CommonUtils.isNull(item)) {
            log.info("상품이 존재하지 않음. 잘못된 경로.");
            return ResponseEntity.ok("해당 상품이 없어 주문이 불가능합니다.");
        }

        if (OrdersUtils.isSoldOut(item.getRemaining())) {
            log.info("품절입니다.");
            return ResponseEntity.ok("품절된 상품입니다. 상품 홈으로 돌아가주세요");
        }

        if (OrdersUtils.isOverRemaining(
                item.getRemaining(),
                ordersRequest.getOrderCount()
        )) {
            log.info("주문 불가능, 주문 수량이 재고보다 많음.");
            return ResponseEntity.ok("주문 수량이 재고보다 많아 주문이 불가능합니다.");
        }

        orderService.saveOrder(
                item,
                ordersRequest,
                email
        );
        itemService.minusItemRemaining(
                ordersRequest.getOrderCount(),
                itemId
        );
        userService.plusCount(email);
        log.info("주문 성공");

        String url = "/item/" + itemId;
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }
    
    @GetMapping("/item/order-list/{itemId}")
    public ResponseEntity<?> ordersListPage(@PathVariable("itemId") Long itemId) {
        List<OrdersResponse> orderDtos =
                orderService.getOrdersForItemDetail(itemId);

        if (CommonUtils.isNull(orderDtos)) {
            return ResponseEntity.ok("주문자가 아직 없습니다.");
        }

        return ResponseEntity.ok(orderDtos);
    }

    @GetMapping("/item/order/{itemId}")
    public ResponseEntity<?> orderPage(@PathVariable("itemId") Long itemId) {
        Item item = itemService.getItemEntity(itemId);

        if (CommonUtils.isNull(item)) {
            log.info("상품이 존재하지 않음. 잘못된 경로.");
            return ResponseEntity.ok("해당 상품이 없어 주문이 불가능합니다.");
        }

        return ResponseEntity.ok("주문 페이지");
    }

    @GetMapping("/item/cancel/{orderId}")
    public ResponseEntity<?> cancelPage(@PathVariable("orderId") Long orderId) {
        OrdersResponse order = orderService.getOrderDto(orderId);

        if (CommonUtils.isNull(order)) {
            log.info("주문이 존재하지 않음. 잘못된 경로.");
            return ResponseEntity.ok("해당 주문이 없어 주문취소가 불가능합니다.");
        }

        return ResponseEntity.ok(order);
    }

    @PostMapping("/item/cancel/{orderId}")
    public ResponseEntity<?> cancel(
            @PathVariable("orderId") Long orderId,
            Principal principal,
            HttpServletRequest request
    ) {
        String currentUserEmail = principal.getName();
        Orders orders = orderService.getOrderEntity(orderId);

        if (CommonUtils.isNull(orders)) {
            log.info("주문이 존재하지 않음. 잘못된 경로.");
            return ResponseEntity
                    .ok("해당 주문을 찾을 수 없어 주문 취소가 불가능합니다.");
        }

        String orderer = orders.getUsers().getEmail();
        if (!Objects.equals(currentUserEmail, orderer)) {
            log.info("작성자와 현재 유저가 달라 주문 취소 불가능");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        if (OrdersUtils.isOverCancelLimitDate(orders)) {
            log.info("주문 취소 실패");
            return ResponseEntity.ok("주문 한지 7일이 지나 주문 취소가 불가능합니다.");
        }

        orderService.cancelOrder(orderId);
        itemService.plusItemRemaining(
                orders.getOrderCount(),
                orders.getItem().getId()
        );
        userService.minusCount(currentUserEmail);
        log.info("주문 취소 성공");

        String url = "/user/order-list";
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }
}
