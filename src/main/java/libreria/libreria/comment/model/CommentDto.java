package libreria.libreria.comment.model;

import libreria.libreria.item.model.Item;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {

    private Long id;
    private String writer;
    private String content;
    private Item item;
    private LocalDateTime createdDate;

    public Comment toEntity() {
        return Comment.builder()
                .id(id)
                .writer(writer)
                .content(content)
                .item(item)
                .build();
    }
}