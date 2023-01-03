package libreria.libreria.item.service;

import jakarta.persistence.EntityManager;
import libreria.libreria.item.dto.ItemRequest;
import libreria.libreria.user.dto.UserRequest;
import libreria.libreria.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @DisplayName("가장 먼저 생성하라")
    public void createUsers(String email, String password) {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail(email);
        userRequest.setPassword(password);
        userService.signup(userRequest);
    }

    public Long createItem(String title, String email) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setTitle(title);
        return itemService.saveItem(itemRequest, email);
    }

    @Test
    @Transactional
    void editItemTest() {
        //given
        String email = "yc1111@gmail.com";
        String password = "1111";
        createUsers(email, password);
        em.flush();
        em.clear();

        String title = "test title";
        Long itemId = createItem(title, email);
        em.flush();
        em.clear();

        //when
        String updateTitle = "updated title";
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setTitle(updateTitle);
        itemService.editItem(itemService.getItemEntity(itemId), itemRequest);
        em.flush();
        em.clear();

        //then
        Assertions.assertThat(itemService.getItemEntity(itemId).getTitle()).isEqualTo(updateTitle);
    }
}