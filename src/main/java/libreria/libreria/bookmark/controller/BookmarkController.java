package libreria.libreria.bookmark.controller;

import jakarta.servlet.http.HttpServletRequest;
import libreria.libreria.bookmark.model.Bookmark;
import libreria.libreria.bookmark.service.BookmarkService;
import libreria.libreria.item.model.Item;
import libreria.libreria.item.service.ItemService;
import libreria.libreria.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
                bookmarkService.getBookmarks(principal.getName());

        return ResponseEntity.ok(bookmarkList);
    }

    @PostMapping("/bookmark/post/{itemId}")
    public ResponseEntity<?> bookmarking(
            @PathVariable("itemId") Long itemId,
            Principal principal,
            HttpServletRequest request
    ) {
        Item item = itemService.getItemEntity(itemId);
        Bookmark bookmark =
                bookmarkService.getBookmarkDetail(itemId, principal.getName());

        if (CommonUtils.isNull(item)) {
            return ResponseEntity.ok("해당 상품을 찾을 수 없어 북마킹이 불가능합니다.");
        }

        if (!CommonUtils.isNull(bookmark)) {
            return ResponseEntity.ok("이미 북마크 하였습니다.");
        }

        bookmarkService.saveBookmark(
                principal.getName(),
                itemId
        );
        log.info("북마킹 성공!!");

        String url = "/item/" + itemId;

        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @PostMapping("/bookmark/cancel/{itemId}")
    public ResponseEntity<?> cancelBookmark(
            @PathVariable("itemId") Long itemId,
            Principal principal,
            HttpServletRequest request
    ) {
        Item item = itemService.getItemEntity(itemId);
        Bookmark bookmark =
                bookmarkService.getBookmarkDetail(itemId, principal.getName());

        if (CommonUtils.isNull(item)) {
            return ResponseEntity.ok("해당 상품을 찾을 수 없어 북마크 취소가 불가능합니다.");
        }

        if (CommonUtils.isNull(bookmark)) {
            return ResponseEntity.ok("이미 북마크가 취소되었습니다.");
        }

        bookmarkService.cancelBookmark(
                principal.getName(),
                itemId
        );
        log.info("북마크 취소 성공!!");

        String url = "/item/" + itemId;

        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }
}
