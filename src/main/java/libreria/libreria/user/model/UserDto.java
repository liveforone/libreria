package libreria.libreria.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String password;
    private Role auth;
    private int count;  //등급을 위한 주문 건수
    private String address;

    public Users toEntity() {
        return Users.builder()
                .id(id)
                .email(email)
                .password(password)
                .auth(auth)
                .count(count)
                .address(address)
                .build();
    }
}
