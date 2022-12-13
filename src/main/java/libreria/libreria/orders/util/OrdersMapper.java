package libreria.libreria.orders.util;

import libreria.libreria.orders.dto.OrdersRequest;
import libreria.libreria.orders.dto.OrdersResponse;
import libreria.libreria.orders.model.Orders;

import java.util.List;
import java.util.stream.Collectors;

public class OrdersMapper {

    /*
     * dto ->  entity 변환 편의 메소드
     */
    public static Orders dtoToEntity(OrdersRequest order) {
        return Orders.builder()
                .id(order.getId())
                .item(order.getItem())
                .users(order.getUsers())
                .status(order.getStatus())
                .orderCount(order.getOrderCount())
                .build();
    }

    /*
     * OrdersResponse builder 편의 메소드
     */
    private static OrdersResponse dtoBuilder(Orders orders) {
        return OrdersResponse.builder()
                .id(orders.getId())
                .status(orders.getStatus())
                .orderCount(orders.getOrderCount())
                .createdDate(orders.getCreatedDate())
                .build();
    }

    /*
     * entity -> dto 편의 메소드1
     * 반환 타입 : 리스트형식
     */
    public static List<OrdersResponse> entityToDtoList(List<Orders> ordersList) {
        return ordersList
                .stream()
                .map(OrdersMapper::dtoBuilder)
                .collect(Collectors.toList());
    }
}
