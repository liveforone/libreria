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

import java.net.URI;
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

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(
                "/item/comment/" + itemId
        ));

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
        CommentResponse comment =
                commentService.entityToDtoDetail(commentService.getComment(id));

        return ResponseEntity.ok(
                Objects.requireNonNullElse(
                comment,
                "댓글을 찾을 수 없어 수정이 불가능합니다."
                )
        );
    }

    /*
    수정과 삭제 모두 화면단(프론트)에서 작성자와 현재 유저 판별이 끝났다.
    하지만 민감한 부분인 수정과 삭제시 서버단에서 다시 한번 판별한다.
     */
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

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(
                "/item/comment/" + itemId
        ));

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    /*
    삭제전 js의 alert 로 삭제할 것이지 물어보기.
    수정과 마찬가지로 뷰에서 작성자와 현재 유저 판별이 끝남.
    그러나 민감한 부분인 만큼 다시 서버단에서 작성자와 현재 유저를 판별한다.
     */
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

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(
                "/item/comment/" + itemId
        ));

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }
}
