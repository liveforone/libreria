package libreria.libreria.orders.util;

import libreria.libreria.orders.dto.OrdersRequest;
import libreria.libreria.orders.dto.OrdersResponse;
import libreria.libreria.orders.model.Orders;

import java.util.List;
import java.util.stream.Collectors;

public class OrdersMapper {

    public static Orders dtoToEntity(OrdersRequest ordersRequest) {
        return Orders.builder()
                .id(ordersRequest.getId())
                .item(ordersRequest.getItem())
                .users(ordersRequest.getUsers())
                .status(ordersRequest.getStatus())
                .orderCount(ordersRequest.getOrderCount())
                .build();
    }

    private static OrdersResponse dtoBuilder(Orders orders) {
        return OrdersResponse.builder()
                .id(orders.getId())
                .status(orders.getStatus())
                .orderCount(orders.getOrderCount())
                .createdDate(orders.getCreatedDate())
                .build();
    }

    public static List<OrdersResponse> entityToDtoList(List<Orders> orders) {
        return orders
                .stream()
                .map(OrdersMapper::dtoBuilder)
                .collect(Collectors.toList());
    }
}
