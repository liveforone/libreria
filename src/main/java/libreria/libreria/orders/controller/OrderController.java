package libreria.libreria.orders.controller;

import libreria.libreria.orders.model.Orders;
import libreria.libreria.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    /*
    item detail에서 게시글 수정 때문에 현재 접속 유저(principal)을 보내주었다.(판별을 위해)
    그것으로 판별된 유저(게시글 작성자)는 해당 링크로 접속해서 해당 상품의 주문리스트를 볼 수 있다.
     */
    @GetMapping("/item/orderlist/{itemId}")
    public ResponseEntity<List<Orders>> itemDetailOrderList(@PathVariable("itemId") Long itemId) {
        List<Orders> ordersList = orderService.getOrderListForItemDetail(itemId);

        return ResponseEntity.ok(ordersList);
    }

    //주문, 주문취소, 상품 품절, 유저 카운트
}
