package libreria.libreria.comment.repository;

import libreria.libreria.comment.dto.CommentResponse;
import libreria.libreria.comment.model.Comment;
import libreria.libreria.item.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c join fetch c.item where c.id = :id")
    Comment findOneById(@Param("id") Long id);

    @Query("select new libreria.libreria.comment.dto.CommentResponse" +
            "(c.id, c.writer, c.content, c.createdDate)" +
            " from Comment c where c.id = :id")
    CommentResponse findOneDtoById(@Param("id") Long id);

    @Query("select c from Comment c join c.item where c.item = :item")
    Page<Comment> findCommentsByItemId(@Param("item") Item item, Pageable pageable);
}
