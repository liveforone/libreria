package libreria.libreria.comment.model;

import libreria.libreria.item.model.Item;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {

    private Long id;
    private String user;
    private String content;
    private Item item;
    private LocalDateTime createdDate;

    public Comment toEntity() {
        return Comment.builder()
                .id(id)
                .user(user)
                .content(content)
                .item(item)
                .build();
    }
}