package libreria.libreria.user.service;

import jakarta.persistence.EntityManager;
import libreria.libreria.user.model.Role;
import libreria.libreria.user.model.Users;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private EntityManager em;

    @Transactional
    public Long makeUser() {
        Users users = Users.builder()
                .email("yc1234@gmail.com")
                .password("1234")
                .auth(Role.MEMBER)
                .build();
        em.persist(users);

        return users.getId();
    }

    @Test
    @Transactional
    void updatePasswordTest() {
        //given
        Long id = makeUser();
        String inputPassword = "1357";

        //when
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String newPassword =  passwordEncoder.encode(inputPassword);
        Users users = Users.builder()
                .id(id)
                .password(newPassword)
                .build();
        em.merge(users);

        //then
        Users finalUser = em.find(Users.class, id);
        boolean matches = passwordEncoder.matches(inputPassword, finalUser.getPassword());
        Assertions.assertThat(matches).isTrue();
    }
}