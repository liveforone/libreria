package libreria.libreria.comment.dto;

import libreria.libreria.comment.model.Comment;
import libreria.libreria.item.model.Item;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentRequest {

    private Long id;
    private String writer;
    private String content;
    private Item item;
    private LocalDateTime createdDate;
}