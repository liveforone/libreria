package libreria.libreria.user.util;

import jakarta.servlet.http.HttpServletRequest;
import libreria.libreria.jwt.JwtAuthenticationFilter;
import libreria.libreria.user.model.Users;
import libreria.libreria.utility.CommonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.net.URI;

public class UserUtils {

    /*
     * 이메일 중복 검증
     * 반환 값 : false(중복X), true(중복)
     */
    public static boolean isDuplicateEmail(Users users) {

        return !CommonUtils.isNull(users);
    }

    /*
    * 비밀번호 같지 않은지 검증
    * 반환 값 : true(같지 않을때), false(같을때)
     */
    public static boolean isNotMatchingPassword(String inputPassword, String originalPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return !encoder.matches(inputPassword, originalPassword);
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

    public static HttpHeaders makeHttpHeadersWhenSignupRedirect(HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();

        String url = "/user/login";
        String token = JwtAuthenticationFilter.resolveToken(request);

        httpHeaders.setBearerAuth(token);
        httpHeaders.setLocation(URI.create(url));

        return httpHeaders;
    }
}
