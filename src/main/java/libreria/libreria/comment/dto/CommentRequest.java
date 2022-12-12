package libreria.libreria.comment.dto;

import libreria.libreria.item.model.Item;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentRequest {

    private Long id;
    private String writer;
    private String content;
    private Item item;
    private LocalDateTime createdDate;
}