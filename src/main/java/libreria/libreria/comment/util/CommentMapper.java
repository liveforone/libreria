package libreria.libreria.comment.util;

import libreria.libreria.comment.dto.CommentRequest;
import libreria.libreria.comment.dto.CommentResponse;
import libreria.libreria.comment.model.Comment;
import org.springframework.data.domain.Page;

public class CommentMapper {

    public static Comment dtoToEntity(CommentRequest commentRequest) {
        return Comment.builder()
                .id(commentRequest.getId())
                .writer(commentRequest.getWriter())
                .content(commentRequest.getContent())
                .item(commentRequest.getItem())
                .build();
    }

    private static CommentResponse dtoBuilder(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .writer(comment.getWriter())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .build();
    }

    public static Page<CommentResponse> entityToDtoPage(Page<Comment> comments) {
        return comments.map(CommentMapper::dtoBuilder);
    }
}
