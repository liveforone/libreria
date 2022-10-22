package libreria.libreria.item.repository;

import libreria.libreria.item.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    //== 아이템 for myPage itemList & fetch join ==//
    @Query("select i from Item i join fetch i.users u where u.email = :email")
    List<Item> findItemWithJoinByEmail(@Param("email") String email);

    @Override
    @Query("select i from Item i join fetch i.users")
    List<Item> findAll();

    @Query("select i from Item i join i.users where i.title like %:title%")
    Page<Item> searchByTitle(@Param("title") String keyword, Pageable pageable);

    /*
    페이징을 위해 fetch를 포기했다.
    다만, yml에 batch_fetch_size 저장해두어서 괜찮다!
     */
    @Query("select i from Item i join i.users where i.category = :category")
    Page<Item> findCategoryListByCategory(@Param("category") String category, Pageable pageable);

    @Query("select i from Item i join fetch i.users where i.id = :id")
    Item findOneById(@Param("id") Long id);

    //== 좋아요 업데이트 ==//
    @Modifying
    @Query("update Item i set i.good = i.good + 1 where i.id = :id")
    void updateGood(@Param("id") Long id);
}
