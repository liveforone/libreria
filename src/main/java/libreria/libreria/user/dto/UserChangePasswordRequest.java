package libreria.libreria.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserChangePasswordRequest {

    private String oldPassword;
    private String newPassword;
}
