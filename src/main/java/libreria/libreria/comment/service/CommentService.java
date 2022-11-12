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

    //== entity -> dto 편의메소드1 - 리스트 ==//
    public List<CommentResponse> entityToDtoList(List<Comment> commentList) {
        List<CommentResponse> dtoList = new ArrayList<>();

        for (Comment comment : commentList) {
            CommentResponse commentResponse = CommentResponse.builder()
                    .id(comment.getId())
                    .writer(comment.getWriter())
                    .content(comment.getContent())
                    .createdDate(comment.getCreatedDate())
                    .build();
            dtoList.add(commentResponse);
        }
        return dtoList;
    }

    //== entity -> dto 편의 메소드2 - detail ==//
    public CommentResponse entityToDtoDetail(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .writer(comment.getWriter())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .build();
    }

    public List<CommentResponse> getCommentList(Long id) {
        return entityToDtoList(commentRepository.findCommentByItemId(id));
    }

    public CommentResponse getComment(Long id) {
        return entityToDtoDetail(commentRepository.findOneById(id));
    }

    @Transactional
    public void saveComment(Long itemId, CommentRequest commentRequest, String writer) {
        Item item = itemRepository.findOneById(itemId);

        commentRequest.setItem(item);
        commentRequest.setWriter(writer);

        commentRepository.save(commentRequest.toEntity());
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

        commentRepository.save(commentRequest.toEntity());

        return comment.getItem().getId();
    }

    /*
    edit와 마찬가지로 redirect 해주기위해서 itemId를 리턴한다.(comment_id 아님!!)
     */
    @Transactional
    public Long deleteComment(Long id) {
        Comment comment = commentRepository.findOneById(id);
        Long itemId = comment.getItem().getId();

        commentRepository.deleteById(id);

        return itemId;
    }
}
