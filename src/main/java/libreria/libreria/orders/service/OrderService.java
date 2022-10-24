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

import java.time.LocalDate;
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

    public Orders getOrder(Long orderId) {
        return orderRepository.findOneById(orderId);
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


    //== 주문 취소 ==//
    @Transactional
    public void cancelOrder(Long orderId) {
        Orders orders = orderRepository.findOneById(orderId);

        orderRepository.updateStatus(OrderStatus.CANCEL, orderId);
        itemRepository.plusRemaining(orders.getOrderCount(), orders.getItem().getId());
        userRepository.minusCount(orders.getUsers().getEmail());
    }
}
