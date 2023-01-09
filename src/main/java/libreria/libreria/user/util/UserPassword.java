package libreria.libreria.user.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserPassword {

    public static boolean isNotMatchingPassword(String inputPassword, String originalPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return !encoder.matches(inputPassword, originalPassword);
    }

    public static String encodePassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return passwordEncoder.encode(password);
    }
}
