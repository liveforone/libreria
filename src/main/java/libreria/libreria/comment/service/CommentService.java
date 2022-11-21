package libreria.libreria.comment.service;

import libreria.libreria.comment.model.Comment;
import libreria.libreria.comment.dto.CommentRequest;
import libreria.libreria.comment.dto.CommentResponse;
import libreria.libreria.comment.repository.CommentRepository;
import libreria.libreria.item.model.Item;
import libreria.libreria.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;

    //== CommentResponse builder method ==//
    public CommentResponse dtoBuilder(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .writer(comment.getWriter())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .build();
    }

    //== dto -> entity ==//
    public Comment dtoToEntity(CommentRequest comment) {
        return Comment.builder()
                    .id(comment.getId())
                    .writer(comment.getWriter())
                    .content(comment.getContent())
                    .item(comment.getItem())
                    .build();
    }

    //== entity -> dto 편의메소드1 - 리스트 ==//
    public List<CommentResponse> entityToDtoList(List<Comment> commentList) {
        List<CommentResponse> dtoList = new ArrayList<>();

        for (Comment comment : commentList) {
            dtoList.add(dtoBuilder(comment));
        }
        return dtoList;
    }

    //== entity -> dto 편의 메소드2 - detail ==//
    public CommentResponse entityToDtoDetail(Comment comment) {

        if (comment == null) {
            return null;
        }

        return dtoBuilder(comment);
    }

    public List<CommentResponse> getCommentList(Long id) {
        return entityToDtoList(commentRepository.findCommentByItemId(id));
    }

    public Comment getComment(Long id) {
        return commentRepository.findOneById(id);
    }

    @Transactional
    public void saveComment(Long itemId, CommentRequest commentRequest, String writer) {
        Item item = itemRepository.findOneById(itemId);

        commentRequest.setItem(item);
        commentRequest.setWriter(writer);

        commentRepository.save(dtoToEntity(commentRequest));
    }

    /*
    comment id 아닌 item id 리턴
    이유는 리다이렉트 해주기위해서 itemId가 필요하다.
     */
    @Transactional
    public Long editComment(Long id, CommentRequest commentRequest) {
        Comment comment = commentRepository.findOneById(id);

        commentRequest.setId(id);
        commentRequest.setWriter(comment.getWriter());
        commentRequest.setItem(comment.getItem());

        commentRepository.save(dtoToEntity(commentRequest));

        return comment.getItem().getId();
    }

    /*
    edit 와 마찬가지로 redirect 해주기위해서 itemId를 리턴한다.(comment_id 아님!!)
     */
    @Transactional
    public Long deleteComment(Long id) {
        Comment comment = commentRepository.findOneById(id);
        Long itemId = comment.getItem().getId();

        commentRepository.deleteById(id);

        return itemId;
    }
}
