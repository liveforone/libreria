package libreria.libreria.orders.service;

import libreria.libreria.item.model.Item;
import libreria.libreria.item.repository.ItemRepository;
import libreria.libreria.orders.model.OrderStatus;
import libreria.libreria.orders.model.Orders;
import libreria.libreria.orders.dto.OrdersRequest;
import libreria.libreria.orders.dto.OrdersResponse;
import libreria.libreria.orders.repository.OrderRepository;
import libreria.libreria.orders.util.OrdersConstants;
import libreria.libreria.orders.util.OrdersMapper;
import libreria.libreria.user.model.Users;
import libreria.libreria.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    /*
    * order-list
    * when : my-page
     */
    public List<OrdersResponse> getOrdersForMyPage(String email) {
        return OrdersMapper.entityToDtoList(
                orderRepository.findOrdersByEmail(email)
        );
    }

    /*
    * order-list
    * when : item detail
     */
    public List<OrdersResponse> getOrdersForItemDetail(Long itemId) {
        return OrdersMapper.entityToDtoList(
                orderRepository.findOrdersByItemId(itemId)
        );
    }

    public Orders getOrderEntity(Long orderId) {
        return orderRepository.findOneById(orderId);
    }

    public OrdersResponse getOrderDto(Long id) {
        return orderRepository.findOneDtoById(id);
    }

    /*
    * 주문 날짜 조회
    * when : 주문 취소
     */
    public int getOrderDay(Long orderId) {
        Orders orders = orderRepository.findOneById(orderId);

        int cancelLimitDate = orders.getCreatedDate().getDayOfYear() + 7;  //생성날짜 + 7일
        int nowDate = LocalDate.now().getDayOfYear();

        if (nowDate <= cancelLimitDate) {
            return OrdersConstants.CAN_CANCEL.getValue();
        }
        return OrdersConstants.CANT_CANCEL.getValue();
    }

    @Transactional
    public void saveOrder(
            Long itemId,
            OrdersRequest ordersRequest,
            String user
    ) {
        Item item = itemRepository.findOneById(itemId);
        Users users = userRepository.findByEmail(user);

        ordersRequest.setItem(item);
        ordersRequest.setUsers(users);
        ordersRequest.setStatus(OrderStatus.ORDER);

        itemRepository.minusRemaining(
                ordersRequest.getOrderCount(),
                itemId
        );
        userRepository.plusCount(user);
        orderRepository.save(
                OrdersMapper.dtoToEntity(ordersRequest)
        );
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Orders orders = orderRepository.findOneById(orderId);

        orderRepository.updateStatus(
                OrderStatus.CANCEL,
                orderId
        );
        itemRepository.plusRemaining(
                orders.getOrderCount(),
                orders.getItem().getId()
        );
        userRepository.minusCount(orders.getUsers().getEmail());
    }
}
