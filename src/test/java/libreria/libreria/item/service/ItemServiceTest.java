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
        em.persist(users);

        Item item = Item.builder()
                .title("test1")
                .users(users)
                .build();
        em.persist(item);

        return item.getId();
    }

    @Test
    @Transactional
    void editItemTest() {
        //given
        Long id = makeItemAndUser();

        //when
        String title = "updated title";
        Item updateTitle = Item.builder()
                .id(id)
                .title(title)
                .build();
        em.merge(updateTitle);

        Item finalItem = em.find(Item.class, id);

        //then
        Assertions.assertThat(finalItem.getTitle()).isEqualTo(title);
    }
}