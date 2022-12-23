package libreria.libreria.item.service;

import jakarta.persistence.EntityManager;
import libreria.libreria.item.model.Item;
import libreria.libreria.user.model.Role;
import libreria.libreria.user.model.Users;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class ItemServiceTest {

    @Autowired
    private EntityManager em;

    @Transactional
    public Long makeItemAndUser() {
        Users users = Users.builder()
                .email("yc1234@gmail.com")
                .password("1234")
                .auth(Role.SELLER)
                .build();
        em.persist(users);  //영속화

        Item item = Item.builder()
                .title("test1")
                .users(users)
                .build();
        em.persist(item);  //영속화

        return item.getId();
    }

    @Test
    @Transactional
    void editItemTest() {
        //given
        Long id = makeItemAndUser();  //영속 상태

        //when
        String title = "updated title";  //변경할 제목
        Item updateTitle = Item.builder()
                .id(id)
                .title(title)
                .build();
        em.merge(updateTitle);  //머지(1차 캐시에 있는 객체 업데이트)

        Item finalItem = em.find(Item.class, id);  //1차 캐시에 있는 객체 조회

        //then
        Assertions.assertThat(finalItem.getTitle()).isEqualTo(title);
    }
}