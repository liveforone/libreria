package libreria.libreria.orders.controller;

import libreria.libreria.item.model.Item;
import libreria.libreria.item.service.ItemService;
import libreria.libreria.orders.model.Orders;
import libreria.libreria.orders.model.OrdersDto;
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
    @GetMapping("/item/orderlist/{itemId}")
    public ResponseEntity<List<Orders>> itemDetailOrderList(@PathVariable("itemId") Long itemId) {
        List<Orders> ordersList = orderService.getOrderListForItemDetail(itemId);

        return ResponseEntity.ok(ordersList);
    }

    @GetMapping("/item/order/{itemId}")
    public ResponseEntity<Item> orderPage(@PathVariable("itemId") Long itemId) {
        Item item = itemService.getDetail(itemId);

        return ResponseEntity.ok(item);
    }

    @PostMapping("/item/order/{itemId}")
    public ResponseEntity<?> order(
            @PathVariable("itemId") Long itemId,
            @RequestBody OrdersDto ordersDto,
            Principal principal
            ) {
        Item item = itemService.getDetail(itemId);

        if (item.getRemaining() <= 0) {
            log.info("품절입니다.");
            return ResponseEntity.ok("품절된 상품입니다. 상품 홈으로 돌아가주세요");
        } else {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(URI.create("/item/" + itemId));

            orderService.saveOrder(itemId, ordersDto, principal.getName());
            log.info("주문 성공!!");

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .headers(httpHeaders)
                    .build();
        }
    }


    //주문취소 & localDate parshing
}
