package libreria.libreria.item.repository;

import libreria.libreria.item.dto.ItemResponse;
import libreria.libreria.item.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Override
    @Query("select i from Item i join fetch i.users")
    List<Item> findAll();

    @Query("select i from Item i join fetch i.users where i.id = :id")
    Item findOneById(@Param("id") Long id);

    @Query("select new libreria.libreria.item.dto.ItemResponse" +
            "(i.id, i.title, i.content, i.author, i.remaining, i.category, i.publishedYear, i.good)" +
            " from Item i where i.id = :id")
    ItemResponse findOneDtoById(@Param("id") Long id);

    @Query("select i from Item i join fetch i.users u where u.email = :email")
    List<Item> findItemsByEmail(@Param("email") String email);

    @Query("select i from Item i join i.users where i.title like %:title%")
    Page<Item> searchItemsByTitle(@Param("title") String keyword, Pageable pageable);

    @Query("select i from Item i join i.users where i.category = :category")
    Page<Item> findCategoriesByCategory(@Param("category") String category, Pageable pageable);

    @Modifying
    @Query("update Item i set i.good = i.good + 1 where i.id = :id")
    void updateGood(@Param("id") Long id);

    @Modifying
    @Query("update Item i set i.remaining = i.remaining + :count where i.id = :id")
    void plusRemaining(@Param("count") int count, @Param("id") Long id);

    @Modifying
    @Query("update Item i set i.remaining = i.remaining - :count where i.id = :id")
    void minusRemaining(@Param("count") int count, @Param("id") Long id);
}
