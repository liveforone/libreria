package libreria.libreria.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {

    private Long id;
    private String email;
    private String address;
    private String rank;
    private Role auth;

    @Builder
    public UserResponseDto(Long id, String email, String address, String rank, Role auth) {
        this.id = id;
        this.email = email;
        this.address = address;
        this.rank = rank;
        this.auth = auth;
    }
}
