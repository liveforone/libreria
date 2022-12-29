package libreria.libreria.user.util;

import jakarta.servlet.http.HttpServletRequest;
import libreria.libreria.jwt.JwtAuthenticationFilter;
import libreria.libreria.user.model.Role;
import libreria.libreria.user.model.Users;
import libreria.libreria.utility.CommonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class UserUtils {

    public static boolean isDuplicateEmail(Users users) {

        return !CommonUtils.isNull(users);
    }

    public static boolean isNotMatchingPassword(String inputPassword, String originalPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return !encoder.matches(inputPassword, originalPassword);
    }

    public static String encodePassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return passwordEncoder.encode(password);
    }

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

    public static void updateContextHolderAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
        updatedAuthorities.add(new SimpleGrantedAuthority(Role.SELLER.getValue()));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                auth.getPrincipal(),
                auth.getCredentials(),
                updatedAuthorities
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
