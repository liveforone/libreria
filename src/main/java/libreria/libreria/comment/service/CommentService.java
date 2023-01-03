package libreria.libreria.comment.service;

import libreria.libreria.comment.model.Comment;
import libreria.libreria.comment.dto.CommentRequest;
import libreria.libreria.comment.dto.CommentResponse;
import libreria.libreria.comment.repository.CommentRepository;
import libreria.libreria.comment.util.CommentMapper;
import libreria.libreria.item.model.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment getCommentEntity(Long id) {
        return commentRepository.findOneById(id);
    }

    public CommentResponse getCommentDto(Long id) {
        return commentRepository.findOneDtoById(id);
    }

    public Page<CommentResponse> getComments(Item item, Pageable pageable) {
        return CommentMapper.entityToDtoPage(
                commentRepository.findCommentsByItemId(item, pageable)
        );
    }

    @Transactional
    public void saveComment(
            Item item,
            CommentRequest commentRequest,
            String writer
    ) {
        commentRequest.setItem(item);
        commentRequest.setWriter(writer);

        commentRepository.save(
                CommentMapper.dtoToEntity(commentRequest)
        );
    }

    @Transactional
    public Long editComment(Comment comment, CommentRequest commentRequest) {
        commentRequest.setId(comment.getId());
        commentRequest.setWriter(comment.getWriter());
        commentRequest.setItem(comment.getItem());

        commentRepository.save(
                CommentMapper.dtoToEntity(commentRequest)
        );

        return comment.getItem().getId();
    }

    @Transactional
    public void deleteComment(Comment comment) {
        commentRepository.deleteById(comment.getId());
    }
}
