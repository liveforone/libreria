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
}
