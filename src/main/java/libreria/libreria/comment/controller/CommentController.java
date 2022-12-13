package libreria.libreria.comment.controller;

import libreria.libreria.comment.dto.CommentRequest;
import libreria.libreria.comment.dto.CommentResponse;
import libreria.libreria.comment.model.Comment;
import libreria.libreria.comment.service.CommentService;
import libreria.libreria.item.model.Item;
import libreria.libreria.item.service.ItemService;
import libreria.libreria.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final ItemService itemService;

    @GetMapping("/item/comment/{itemId}")
    public ResponseEntity<Map<String, Object>> commentList(
            @PathVariable("itemId") Long itemId,
            Principal principal
    ) {
        Map<String, Object> map = new HashMap<>();
        String user = principal.getName();
        List<CommentResponse> commentList = commentService.getCommentList(itemId);

        map.put("user", user);
        map.put("body", commentList);

        return ResponseEntity.ok(map);
    }
    
    @PostMapping("/item/comment/post/{itemId}")
    public ResponseEntity<?> commentPost(
            @PathVariable("itemId") Long itemId,
            @RequestBody CommentRequest commentRequest,
            Principal principal
    ) {
        Item item = itemService.getItemEntity(itemId);

        if (CommonUtils.isNull(item)) {
            return ResponseEntity.ok("해당 상품이 없어 댓글 작성이 불가능합니다.");
        }

        String url = "/item/comment/" + itemId;
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        commentService.saveComment(
                itemId,
                commentRequest,
                principal.getName()
        );
        log.info("댓글 작성 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @GetMapping("/item/comment/edit/{id}")
    public ResponseEntity<?> editPage(@PathVariable("id") Long id) {
        CommentResponse comment = commentService.getCommentResponse(id);

        if (CommonUtils.isNull(comment)) {
            return ResponseEntity.ok("댓글을 찾을 수 없어 수정이 불가능합니다.");
        }

        return ResponseEntity.ok(comment);
    }

    @PostMapping("/item/comment/edit/{id}")
    public ResponseEntity<?> editComment(
            @PathVariable("id") Long id,
            @RequestBody CommentRequest commentRequest,
            Principal principal
    ) {
        Comment comment = commentService.getComment(id);

        if (CommonUtils.isNull(comment)) {
            return ResponseEntity.ok("댓글을 찾을 수 없어 수정이 불가능합니다.");
        }

        if (!Objects.equals(comment.getWriter(), principal.getName())) {
            log.info("작성자와 현재 유저가 달라 수정 불가능.");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        Long itemId = commentService.editComment(
                id,
                commentRequest
        );
        log.info("리뷰 업데이트 성공!!");

        String url = "/item/comment/" + itemId;
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @PostMapping("/item/comment/delete/{id}")
    public ResponseEntity<?> commentDelete(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        Comment comment = commentService.getComment(id);

        if (CommonUtils.isNull(comment)) {
            return ResponseEntity.ok("댓글을 찾을 수 없어 삭제가 불가능합니다.");
        }

        if (!Objects.equals(comment.getWriter(), principal.getName())) {
            log.info("작성자와 현재유저가 달라 삭제 불가능");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        Long itemId = commentService.deleteComment(id);
        log.info("댓글 " + id + "삭제완료!!");

        String url = "/item/comment/" + itemId;
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }
}
