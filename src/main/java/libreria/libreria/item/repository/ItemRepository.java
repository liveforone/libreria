package libreria.libreria.item.repository;

import libreria.libreria.item.model.Item;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    //== 아이템 for myPage itemList & fetch join ==//
    @Query("select i from Item i left join fetch i.users u where u.email = :email")
    List<Item> findItemWithJoinByEmail(@Param("email") String email);

    @Override
    @EntityGraph(attributePaths = {"users"})
    List<Item> findAll();

    //== 좋아요 업데이트 ==//
    @Modifying
    @Query("update Item i set i.good = i.good + 1 where i.id = :id")
    void updateGood(@Param("id") Long id);
}
