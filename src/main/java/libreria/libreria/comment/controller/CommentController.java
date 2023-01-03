package libreria.libreria.comment.controller;

import jakarta.servlet.http.HttpServletRequest;
import libreria.libreria.comment.dto.CommentRequest;
import libreria.libreria.comment.dto.CommentResponse;
import libreria.libreria.comment.model.Comment;
import libreria.libreria.comment.service.CommentService;
import libreria.libreria.item.model.Item;
import libreria.libreria.item.service.ItemService;
import libreria.libreria.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final ItemService itemService;
    
    @PostMapping("/comment/post/{itemId}")
    public ResponseEntity<?> commentPost(
            @PathVariable("itemId") Long itemId,
            @RequestBody CommentRequest commentRequest,
            Principal principal,
            HttpServletRequest request
    ) {
        Item item = itemService.getItemEntity(itemId);

        if (CommonUtils.isNull(item)) {
            log.info("상품이 존재하지 않음.");
            return ResponseEntity.ok("해당 상품이 없어 댓글 작성이 불가능합니다.");
        }

        commentService.saveComment(
                item,
                commentRequest,
                principal.getName()
        );
        log.info("댓글 작성 성공");

        String url = "/comment/" + itemId;

        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @GetMapping("/comment/{itemId}")
    public ResponseEntity<?> commentHome(
            @PathVariable("itemId") Long itemId,
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            Principal principal
    ) {
        Item item = itemService.getItemEntity(itemId);

        if (CommonUtils.isNull(item)) {
            return ResponseEntity.ok("게시글이 없습니다.");
        }

        Map<String, Object> map = new HashMap<>();
        String user = principal.getName();
        Page<CommentResponse> comments = commentService.getComments(item, pageable);

        map.put("user", user);
        map.put("body", comments);

        return ResponseEntity.ok(map);
    }

    @GetMapping("/comment/edit/{id}")
    public ResponseEntity<?> editCommentPage(@PathVariable("id") Long id) {
        CommentResponse comment = commentService.getCommentDto(id);

        if (CommonUtils.isNull(comment)) {
            log.info("id : " + id + " 댓글이 존재 하지 않음");
            return ResponseEntity.ok("댓글을 찾을 수 없어 수정이 불가능합니다.");
        }

        return ResponseEntity.ok(comment);
    }

    @PostMapping("/comment/edit/{id}")
    public ResponseEntity<?> editComment(
            @PathVariable("id") Long id,
            @RequestBody CommentRequest commentRequest,
            Principal principal,
            HttpServletRequest request
    ) {
        Comment comment = commentService.getCommentEntity(id);

        if (CommonUtils.isNull(comment)) {
            log.info("id : " + id + " 댓글이 존재 하지 않음");
            return ResponseEntity.ok("댓글을 찾을 수 없어 수정이 불가능합니다.");
        }

        String writer = comment.getWriter();
        String user = principal.getName();
        if (!Objects.equals(writer, user)) {
            log.info("작성자와 현재 유저가 달라 수정 불가능.");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        Long itemId = commentService.editComment(comment, commentRequest);
        log.info("댓글 업데이트 성공");

        String url = "/item/comment/" + itemId;

        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @PostMapping("/comment/delete/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable("id") Long id,
            Principal principal,
            HttpServletRequest request
    ) {
        Comment comment = commentService.getCommentEntity(id);

        if (CommonUtils.isNull(comment)) {
            log.info("id : " + id + " 댓글이 존재 하지 않음");
            return ResponseEntity.ok("댓글을 찾을 수 없어 삭제가 불가능합니다.");
        }

        String writer = comment.getWriter();
        String user = principal.getName();
        if (!Objects.equals(writer, user)) {
            log.info("작성자와 현재유저가 달라 삭제 불가능");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        commentService.deleteComment(comment);
        log.info("댓글 " + id + "삭제완료");

        Long itemId = comment.getItem().getId();
        String url = "/item/comment/" + itemId;
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }
}
