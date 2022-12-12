package libreria.libreria.user.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserUtils {

    private static final int PASSWORD_MATCH = 1;
    private static final int PASSWORD_NOT_MATCH = 0;

    //== 비밀번호 복호화 ==//
    public static int checkPasswordMatching(String inputPassword, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(encoder.matches(inputPassword, password)) {
            return PASSWORD_MATCH;
        }
        return PASSWORD_NOT_MATCH;
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
