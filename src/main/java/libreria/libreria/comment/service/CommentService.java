package libreria.libreria.comment.service;

import libreria.libreria.comment.model.Comment;
import libreria.libreria.comment.model.CommentDto;
import libreria.libreria.comment.repository.CommentRepository;
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

    public List<Comment> getCommentList(Long id) {
        return commentRepository.findCommentByItemId(id);
    }

    @Transactional
    public void saveComment(Long itemId, CommentDto commentDto, String user) {
        Item item = itemRepository.findOneById(itemId);

        commentDto.setItem(item);
        commentDto.setUser(user);

        commentRepository.save(commentDto.toEntity());
    }

    public Comment getComment(Long id) {
        return commentRepository.findOneById(id);
    }

    /*
    comment id 아닌 item id 리턴
    이유는 리다이렉트 해주기위해서 itemId가 필요하다.
     */
    @Transactional
    public Long editComment(Long id, CommentDto commentDto) {
        Comment comment = commentRepository.findOneById(id);

        commentDto.setId(id);
        commentDto.setUser(comment.getUser());
        commentDto.setItem(comment.getItem());

        commentRepository.save(commentDto.toEntity());

        return comment.getItem().getId();
    }
}
