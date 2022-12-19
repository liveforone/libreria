package libreria.libreria.bookmark.repository;

import libreria.libreria.bookmark.model.Bookmark;
import libreria.libreria.item.model.Item;
import libreria.libreria.user.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("select b from Bookmark b join fetch b.item join fetch b.users u where u.email = :email")
    List<Bookmark> findBookmarksByUserEmail(@Param("email") String email);

    @Query("select b from Bookmark b join fetch b.users join fetch b.item where b.users = :users and b.item = :item")
    Bookmark findOneBookmark(@Param("users") Users users, @Param("item") Item item);
}
