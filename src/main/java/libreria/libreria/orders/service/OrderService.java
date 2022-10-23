package libreria.libreria.orders.service;

import libreria.libreria.item.model.Item;
import libreria.libreria.orders.model.Orders;
import libreria.libreria.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    //== orderList for myPage ==//
    public List<Orders> getOrderListForMyPage(String email) {
        return orderRepository.findOrderListByEmail(email);
    }

    //== orderList for item detail ==//
    public List<Orders> getOrderListForItemDetail(Long itemId) {
        return orderRepository.findOrderListByItemId(itemId);
    }
}
