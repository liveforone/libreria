package libreria.libreria.orders.service;

import libreria.libreria.item.model.Item;
import libreria.libreria.orders.model.OrderStatus;
import libreria.libreria.orders.model.Orders;
import libreria.libreria.orders.dto.OrdersRequest;
import libreria.libreria.orders.dto.OrdersResponse;
import libreria.libreria.orders.repository.OrderRepository;
import libreria.libreria.orders.util.OrdersMapper;
import libreria.libreria.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
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

    @Transactional
    public void saveOrder(
            Item item,
            OrdersRequest ordersRequest,
            String email
    ) {
        ordersRequest.setItem(item);
        ordersRequest.setUsers(userRepository.findByEmail(email));
        ordersRequest.setStatus(OrderStatus.ORDER);

        orderRepository.save(
                OrdersMapper.dtoToEntity(ordersRequest)
        );
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        orderRepository.updateStatus(
                OrderStatus.CANCEL,
                orderId
        );
    }
}
