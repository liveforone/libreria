package libreria.libreria.user.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column
    @Enumerated(value = EnumType.STRING)
    private Role auth;

    @Column(columnDefinition = "integer default 0")
    private int count;  //등급을 위한 주문 건수
    private String address;

    @Builder
    public Users(Long id, String email, String password, Role auth, int count, String address) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.auth = auth;
        this.count = count;
        this.address = address;
    }
}
