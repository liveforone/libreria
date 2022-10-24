package libreria.libreria.orders.service;

import libreria.libreria.item.model.Item;
import libreria.libreria.item.repository.ItemRepository;
import libreria.libreria.orders.model.OrderStatus;
import libreria.libreria.orders.model.Orders;
import libreria.libreria.orders.model.OrdersDto;
import libreria.libreria.orders.repository.OrderRepository;
import libreria.libreria.user.model.Users;
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
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    //== orderList for myPage ==//
    public List<Orders> getOrderListForMyPage(String email) {
        return orderRepository.findOrderListByEmail(email);
    }

    //== orderList for item detail ==//
    public List<Orders> getOrderListForItemDetail(Long itemId) {
        return orderRepository.findOrderListByItemId(itemId);
    }

    //== 주문 ==//
    @Transactional
    public void saveOrder(Long itemId, OrdersDto ordersDto, String user) {
        Item item = itemRepository.findOneById(itemId);
        Users users = userRepository.findByEmail(user);

        ordersDto.setItem(item);
        ordersDto.setUsers(users);
        ordersDto.setStatus(OrderStatus.ORDER);

        itemRepository.minusRemaining(ordersDto.getOrderCount(), itemId);
        userRepository.plusCount(user);
        orderRepository.save(ordersDto.toEntity());
    }
}
