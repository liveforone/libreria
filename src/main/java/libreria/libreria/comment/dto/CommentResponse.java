package libreria.libreria.comment.dto;

import libreria.libreria.item.model.Item;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentResponse {

    private Long id;
    private String writer;
    private String content;
    private LocalDateTime createdDate;

    @Builder
    public CommentResponse(Long id, String writer, String content, LocalDateTime createdDate) {
        this.id = id;
        this.writer = writer;
        this.content = content;
        this.createdDate = createdDate;
    }
}
