package libreria.libreria.orders.service;

import libreria.libreria.item.model.Item;
import libreria.libreria.orders.model.Orders;
import libreria.libreria.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    //== 마이페이지 orderList ==//
    public List<Orders> getOrderListForMyPage(String email) {
        return orderRepository.findOrderListByEmail(email);
    }
}
