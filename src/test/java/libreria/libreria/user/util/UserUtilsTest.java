package libreria.libreria.user.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class UserUtilsTest {

    @Test
    @DisplayName("비밀번호 복호화 테스트")
    void checkPasswordMatching() {
        //given
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        //when
        String inputPassword = "1234";
        String password = passwordEncoder.encode(inputPassword);

        //then
        boolean matches = passwordEncoder.matches(inputPassword, password);
        Assertions.assertThat(matches).isTrue();
    }
}