package libreria.libreria.orders.repository;

import libreria.libreria.orders.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    //== 주문 for mypage orderList - 이중 페치 조인 ==//
    @Query("select o from Orders o join fetch o.item join fetch o.users u where u.email = :email")
    List<Orders> findOrderListByEmail(@Param("email") String email);
}
