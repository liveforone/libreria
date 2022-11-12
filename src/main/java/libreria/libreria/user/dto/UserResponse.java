package libreria.libreria.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import libreria.libreria.user.model.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;
    private String email;
    private String address;
    private String rank;
    private Role auth;

    @Builder
    public UserResponse(Long id, String email, String address, String rank, Role auth) {
        this.id = id;
        this.email = email;
        this.address = address;
        this.rank = rank;
        this.auth = auth;
    }
}
