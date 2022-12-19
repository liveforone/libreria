package libreria.libreria.user.util;

import libreria.libreria.user.model.Users;
import libreria.libreria.utility.CommonUtils;
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
     * 이메일 중복 검증
     * 반환 값 : 1(중복아님), 0(중복)
     */
    public static int checkDuplicateEmail(Users users) {

        if (CommonUtils.isNull(users)) {
            return UserConstants.NOT_DUPLICATE.getValue();
        }
        return UserConstants.DUPLICATE.getValue();
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
