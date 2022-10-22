package libreria.libreria.comment.controller;

import libreria.libreria.comment.model.Comment;
import libreria.libreria.comment.model.CommentDto;
import libreria.libreria.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/item/comment/{itemId}")
    public ResponseEntity<List<Comment>> commentList(@PathVariable("itemId") Long itemId) {
        List<Comment> commentList = commentService.getCommentList(itemId);

        return ResponseEntity.ok(commentList);
    }
    
    @PostMapping("/item/comment/post/{itemId}")
    public ResponseEntity<?> commentPost(
            @PathVariable("itemId") Long itemId,
            @RequestBody CommentDto commentDto,
            Principal principal
            ) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/item/comment/" + itemId));

        commentService.saveComment(itemId, commentDto, principal.getName());
        log.info("댓글 작성 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }
    
    //수정, 댓글
}
