package libreria.libreria.comment.service;

import libreria.libreria.comment.model.Comment;
import libreria.libreria.comment.dto.CommentRequest;
import libreria.libreria.comment.dto.CommentResponse;
import libreria.libreria.comment.repository.CommentRepository;
import libreria.libreria.comment.util.CommentMapper;
import libreria.libreria.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;

    public Comment getCommentEntity(Long id) {
        return commentRepository.findOneById(id);
    }

    public CommentResponse getCommentDto(Long id) {
        return commentRepository.findOneDtoById(id);
    }

    public List<CommentResponse> getComments(Long id) {
        return CommentMapper.entityToDtoList(
                commentRepository.findCommentsByItemId(id)
        );
    }

    @Transactional
    public void saveComment(
            Long itemId,
            CommentRequest commentRequest,
            String writer
    ) {
        commentRequest.setItem(itemRepository.findOneById(itemId));
        commentRequest.setWriter(writer);

        commentRepository.save(
                CommentMapper.dtoToEntity(commentRequest)
        );
    }

    /*
    * 댓글 수정
    * 반환 타입 : item id
    * 반환 타입이 item id인 이유는 수정 후 해당 게시글로 리다이렉트 할 것이기 때문이다.
     */
    @Transactional
    public Long editComment(Long id, CommentRequest commentRequest) {
        Comment comment = commentRepository.findOneById(id);

        commentRequest.setId(id);
        commentRequest.setWriter(comment.getWriter());
        commentRequest.setItem(comment.getItem());

        commentRepository.save(
                CommentMapper.dtoToEntity(commentRequest)
        );

        return comment.getItem().getId();
    }

    /*
     * 댓글 삭제
     * 반환 타입 : item id
     * 반환 타입이 item id인 이유는 삭제 후 해당 게시글로 리다이렉트 할 것이기 때문이다.
     */
    @Transactional
    public Long deleteComment(Long id) {
        Comment comment = commentRepository.findOneById(id);
        commentRepository.deleteById(id);

        return comment.getItem().getId();
    }
}
