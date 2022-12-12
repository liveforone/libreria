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

    //== user rank check ==//
    public static String rankCheck(int count) {
        if (count >= 120) {
            return "DIA";
        }

        if (count >= 60) {
            return "PLATINUM";
        }

        if (count >= 30) {
            return "GOLD";
        }

        if (count >= 15) {
            return "SILVER";
        }

        return "BRONZE";
    }
}
