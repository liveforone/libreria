package libreria.libreria.user.util;

import libreria.libreria.user.dto.UserRequest;
import libreria.libreria.user.model.Users;

public class UserMapper {

    //== dto -> entity ==//
    public static Users dtoToEntity(UserRequest userRequest) {
        return Users.builder()
                .id(userRequest.getId())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .auth(userRequest.getAuth())
                .count(userRequest.getCount())
                .address(userRequest.getAddress())
                .build();
    }
}
