package libreria.libreria.comment.util;

import libreria.libreria.comment.dto.CommentRequest;
import libreria.libreria.comment.dto.CommentResponse;
import libreria.libreria.comment.model.Comment;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    /*
     * dto ->  entity 변환 편의 메소드
     */
    public static Comment dtoToEntity(CommentRequest comment) {
        return Comment.builder()
                .id(comment.getId())
                .writer(comment.getWriter())
                .content(comment.getContent())
                .item(comment.getItem())
                .build();
    }

    /*
     * CommentResponse builder 편의 메소드
     */
    private static CommentResponse dtoBuilder(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .writer(comment.getWriter())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .build();
    }

    /*
     * entity -> dto 편의 메소드1
     * 반환 타입 : 리스트형식
     */
    public static List<CommentResponse> entityToDtoList(List<Comment> commentList) {
        return commentList
                .stream()
                .map(CommentMapper::dtoBuilder)
                .collect(Collectors.toList());
    }
}
