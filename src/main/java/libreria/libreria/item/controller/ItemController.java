package libreria.libreria.item.controller;

import libreria.libreria.item.model.Item;
import libreria.libreria.item.model.ItemDto;
import libreria.libreria.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/item")
    public ResponseEntity<Page<Item>> itemHome(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "good", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable
    ) {
        Page<Item> itemList = itemService.getItemList(pageable);

        return ResponseEntity.ok(itemList);
    }

    @GetMapping("/item/post")
    public ResponseEntity<?> itemPostPage() {
        return ResponseEntity.ok("상품 등록 페이지");
    }

    @PostMapping("/item/post")
    public ResponseEntity<?> itemPost(
            @RequestPart MultipartFile uploadFile,
            @RequestPart("itemDto") ItemDto itemDto,
            Principal principal
            ) throws IllegalStateException, IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/item"));

        if (!uploadFile.isEmpty()) {
            itemService.saveItem(uploadFile, itemDto, principal.getName());
            log.info("포스팅 성공!!");

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .headers(httpHeaders)
                    .build();
        } else {
            log.info("파일이 없어서 포스팅 실패했습니다.");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("파일이 존재하지 않아 포스팅이 실패했습니다.");
        }
    }

    //== 상품 좋아요 ==//
    @PostMapping("/item/good/{id}")
    public ResponseEntity<?> updateGood(@PathVariable("id") Long id) {
        String url = "/item/" + id;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(url));

        itemService.updateGood(id);
        log.info("좋아요 업데이트!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    //detail, edit, 검색, 카테고리 -> comment
}
