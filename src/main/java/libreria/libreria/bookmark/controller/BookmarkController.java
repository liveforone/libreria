package libreria.libreria.bookmark.controller;

import libreria.libreria.bookmark.model.Bookmark;
import libreria.libreria.bookmark.service.BookmarkService;
import libreria.libreria.item.model.Item;
import libreria.libreria.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final ItemService itemService;

    @GetMapping("/my-bookmark")
    public ResponseEntity<Map<String, Object>> myBookmark(Principal principal) {
        Map<String, Object> bookmarkList =
                bookmarkService.getBookmarkList(principal.getName());

        return ResponseEntity.ok(bookmarkList);
    }

    @PostMapping("/bookmark/post/{itemId}")
    public ResponseEntity<?> bookmarking(
            @PathVariable("itemId") Long itemId,
            Principal principal
    ) {
        Item item = itemService.getItemEntity(itemId);
        Bookmark bookmark =
                bookmarkService.getBookmarkDetail(itemId, principal.getName());

        if (item == null) {
            return ResponseEntity.ok("해당 상품을 찾을 수 없어 북마킹이 불가능합니다.");
        }

        if (bookmark != null) {
            return ResponseEntity.ok("이미 북마크 하였습니다.");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(
                "/item/" + itemId
        ));

        bookmarkService.saveBookmark(
                principal.getName(),
                itemId
        );
        log.info("북마킹 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @PostMapping("/bookmark/cancel/{itemId}")
    public ResponseEntity<?> bookmarkCancel(
            @PathVariable("itemId") Long itemId,
            Principal principal
    ) {
        Item item = itemService.getItemEntity(itemId);
        Bookmark bookmark =
                bookmarkService.getBookmarkDetail(itemId, principal.getName());

        if (item == null) {
            return ResponseEntity.ok("해당 상품을 찾을 수 없어 북마크 취소가 불가능합니다.");
        }

        if (bookmark == null) {
            return ResponseEntity.ok("이미 북마크가 취소되었습니다.");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(
                "/item/" + itemId
        ));

        bookmarkService.cancelBookmark(
                principal.getName(),
                itemId
        );
        log.info("북마크 취소 성공!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }
}
