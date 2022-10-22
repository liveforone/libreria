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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    /*
    현재객체 판별을 위해 현재객체를 함께 리턴했다.
    현재객체는 수정, 삭제 버튼 작성자 판별에 사용된다.
     */
    @GetMapping("/item/comment/{itemId}")
    public ResponseEntity<Map<String, Object>> commentList(
            @PathVariable("itemId") Long itemId,
            Principal principal
    ) {
        Map<String, Object> map = new HashMap<>();
        String user = principal.getName();
        List<Comment> commentList = commentService.getCommentList(itemId);

        map.put("user", user);
        map.put("body", commentList);

        return ResponseEntity.ok(map);
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

    @GetMapping("/item/comment/edit/{id}")
    public ResponseEntity<Comment> editPage(@PathVariable("id") Long id) {
        Comment comment = commentService.getComment(id);

        return ResponseEntity.ok(comment);
    }

    @PostMapping("/item/comment/edit/{id}")
    public ResponseEntity<?> editComment(
            @PathVariable("id") Long id,
            @RequestBody CommentDto commentDto
    ) {
        Long itemId = commentService.editComment(id, commentDto);
        log.info("리뷰 업데이트 성공!!");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/item/comment/" + itemId));

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }
    
    //삭제
}