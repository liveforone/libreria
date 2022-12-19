package libreria.libreria.user.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserUtils {

    /*
    * 비밀번호 복호화
     */
    public static int checkPasswordMatching(String inputPassword, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(encoder.matches(inputPassword, password)) {
            return UserConstants.PASSWORD_MATCH.getValue();
        }
        return UserConstants.PASSWORD_NOT_MATCH.getValue();
    }

    /*
    * 유저 등급 체크
     */
    public static String checkUserRank(int count) {
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
