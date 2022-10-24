package libreria.libreria.orders.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import libreria.libreria.item.model.Item;
import libreria.libreria.user.model.Users;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users users;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status; //ORDER, CANCEL

    @Column(columnDefinition = "integer default 0")
    private int orderCount;

    @CreatedDate
    private LocalDate createdDate;  //plusDays(5)로 5일후 날짜 얻을 수 있음.

    @Builder
    public Orders(Long id, Item item, Users users, OrderStatus status, int orderCount) {
        this.id = id;
        this.item = item;
        this.users = users;
        this.status = status;
        this.orderCount = orderCount;
    }
}
