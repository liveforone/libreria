package libreria.libreria.orders.service;

import libreria.libreria.item.model.Item;
import libreria.libreria.item.repository.ItemRepository;
import libreria.libreria.orders.model.OrderStatus;
import libreria.libreria.orders.model.Orders;
import libreria.libreria.orders.dto.OrdersRequest;
import libreria.libreria.orders.dto.OrdersResponse;
import libreria.libreria.orders.repository.OrderRepository;
import libreria.libreria.user.model.Users;
import libreria.libreria.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    //== entity -> dto 편의 메소드1 - 리스트 ==//
    public List<OrdersResponse> entityToDtoList(List<Orders> ordersList) {
        List<OrdersResponse> dtoList = new ArrayList<>();

        for (Orders orders : ordersList) {
            OrdersResponse ordersResponse = OrdersResponse.builder()
                    .id(orders.getId())
                    .status(orders.getStatus())
                    .orderCount(orders.getOrderCount())
                    .createdDate(orders.getCreatedDate())
                    .build();
            dtoList.add(ordersResponse);
        }

        return dtoList;
    }

    //== entity -> dto 편의 메소드2 - detail ==//
    public OrdersResponse entityToDtoDetail(Orders orders) {
        return OrdersResponse.builder()
                .id(orders.getId())
                .status(orders.getStatus())
                .orderCount(orders.getOrderCount())
                .createdDate(orders.getCreatedDate())
                .build();
    }

    //== orderList for myPage ==//
    public List<OrdersResponse> getOrderListForMyPage(String email) {
        return entityToDtoList(orderRepository.findOrderListByEmail(email));
    }

    //== orderList for item detail ==//
    public List<OrdersResponse> getOrderListForItemDetail(Long itemId) {
        return entityToDtoList(orderRepository.findOrderListByItemId(itemId));
    }

    public OrdersResponse getOrder(Long orderId) {
        return entityToDtoDetail(orderRepository.findOneById(orderId));
    }

    //== 엔티티 직접 리턴 - 연관관계 참조를 위해 ==//
    public Orders getOrderEntity(Long orderId) {
        return orderRepository.findOneById(orderId);
    }

    //== 주문 날짜 - 주문 취소를 위한 ==//
    public int getOrderDay(Long orderId) {
        Orders orders = orderRepository.findOneById(orderId);

        int ableDate = orders.getCreatedDate().getDayOfYear() + 7;  //생성날짜 + 7일
        int nowDate = LocalDate.now().getDayOfYear();

        if (nowDate <= ableDate) {
            return 1;  //주문취소 가능, 1 == true라는 뜻
        } else {
            return -1;  //주문취소 불가능, -1 == False라는 뜻
        }
    }

    //== 주문 ==//
    @Transactional
    public void saveOrder(Long itemId, OrdersRequest ordersRequest, String user) {
        Item item = itemRepository.findOneById(itemId);
        Users users = userRepository.findByEmail(user);

        ordersRequest.setItem(item);
        ordersRequest.setUsers(users);
        ordersRequest.setStatus(OrderStatus.ORDER);

        itemRepository.minusRemaining(ordersRequest.getOrderCount(), itemId);
        userRepository.plusCount(user);
        orderRepository.save(ordersRequest.toEntity());
    }

    //== 주문 취소 ==//
    @Transactional
    public void cancelOrder(Long orderId) {
        Orders orders = orderRepository.findOneById(orderId);

        orderRepository.updateStatus(OrderStatus.CANCEL, orderId);
        itemRepository.plusRemaining(orders.getOrderCount(), orders.getItem().getId());
        userRepository.minusCount(orders.getUsers().getEmail());
    }
}
