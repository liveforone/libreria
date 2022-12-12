package libreria.libreria.comment.service;

import libreria.libreria.comment.model.Comment;
import libreria.libreria.comment.dto.CommentRequest;
import libreria.libreria.comment.dto.CommentResponse;
import libreria.libreria.comment.repository.CommentRepository;
import libreria.libreria.comment.util.CommentMapper;
import libreria.libreria.item.model.Item;
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

    public List<CommentResponse> getCommentList(Long id) {
        return CommentMapper.entityToDtoList(
                commentRepository.findCommentByItemId(id)
        );
    }

    public Comment getComment(Long id) {
        return commentRepository.findOneById(id);
    }

    public CommentResponse getCommentResponse(Long id) {
        return commentRepository.findOneDtoById(id);
    }

    @Transactional
    public void saveComment(
            Long itemId,
            CommentRequest commentRequest,
            String writer
    ) {
        Item item = itemRepository.findOneById(itemId);

        commentRequest.setItem(item);
        commentRequest.setWriter(writer);

        commentRepository.save(
                CommentMapper.dtoToEntity(commentRequest)
        );
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

        commentRepository.save(
                CommentMapper.dtoToEntity(commentRequest)
        );

        return comment.getItem().getId();
    }

    /*
    edit 와 마찬가지로 redirect 해주기위해서 itemId를 리턴한다.(comment_id 아님!!)
     */
    @Transactional
    public Long deleteComment(Long id) {
        Comment comment = commentRepository.findOneById(id);
        commentRepository.deleteById(id);

        return comment.getItem().getId();
    }
}
