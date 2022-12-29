package libreria.libreria.user.service;

import jakarta.persistence.EntityManager;
import libreria.libreria.user.dto.UserRequest;
import libreria.libreria.user.model.Users;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private UserService userService;

    public void createMember(String email, String password) {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(email);
        userRequest.setPassword(password);
        userService.signup(userRequest);
    }

    @Test
    void updatePasswordTest() {
        //given
        String email = "yc1111@gmail.com";
        String password = "1234";
        createMember(email, password);

        //when
        Users users = userService.getUserEntity(email);
        String newPassword = "9999";
        userService.updatePassword(users.getId(), newPassword);
        em.flush();
        em.clear();

        //then
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches(newPassword, userService.getUserEntity(email).getPassword());
        Assertions.assertThat(matches).isTrue();
    }
}