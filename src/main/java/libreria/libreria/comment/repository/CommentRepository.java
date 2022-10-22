package libreria.libreria.comment.repository;

import libreria.libreria.comment.model.Comment;
import libreria.libreria.item.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c join fetch c.item i where i.id = :id")
    List<Comment> findCommentByItemId(@Param("id") Long id);

    @Query("select c from Comment c join fetch c.item where c.id = :id")
    Comment findOneById(@Param("id") Long id);
}
