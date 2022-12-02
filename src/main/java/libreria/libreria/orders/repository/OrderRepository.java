package libreria.libreria.orders.repository;

import libreria.libreria.orders.model.OrderStatus;
import libreria.libreria.orders.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    //== 주문리스트 for my-page orderList - 이중 페치 조인 ==//
    @Query("select o from Orders o join fetch o.item join fetch o.users u where u.email = :email")
    List<Orders> findOrderListByEmail(@Param("email") String email);

    //== 주문리스트 for item detail - 이중 페치 조인 ==//
    @Query("select o from Orders o join fetch o.users join fetch o.item i where i.id = :id")
    List<Orders> findOrderListByItemId(@Param("id") Long id);

    @Query("select o from Orders o join fetch o.users join fetch o.item where o.id = :id")
    Orders findOneById(@Param("id") Long id);

    @Modifying
    @Query("update Orders o set o.status = :status where o.id = :id")
    void updateStatus(@Param("status")OrderStatus status, @Param("id") Long id);
}
