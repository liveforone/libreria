package libreria.libreria.item.controller;

import libreria.libreria.item.model.Item;
import libreria.libreria.item.dto.ItemRequest;
import libreria.libreria.item.dto.ItemResponse;
import libreria.libreria.item.service.ItemService;
import libreria.libreria.item.util.ItemMapper;
import libreria.libreria.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
import java.net.MalformedURLException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/item")
    public ResponseEntity<Page<ItemResponse>> itemHome(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "good", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable
    ) {
        Page<ItemResponse> itemList = itemService.getItemList(pageable);

        return ResponseEntity.ok(itemList);
    }

    @GetMapping("/item/search")
    public ResponseEntity<Page<ItemResponse>> itemSearch(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "good", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            @RequestParam("keyword") String keyword
    ) {
        Page<ItemResponse> searchList =
                itemService.getSearchListByTitle(keyword, pageable);

        return ResponseEntity.ok(searchList);
    }

    @GetMapping("/item/category/{category}")
    public ResponseEntity<Page<ItemResponse>> categoryHome(
            @PathVariable("category") String category,
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "good", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable
    ) {
        Page<ItemResponse> categoryList =
                itemService.getCategoryList(category, pageable);

        return ResponseEntity.ok(categoryList);
    }

    @GetMapping("/item/post")
    public ResponseEntity<?> itemPostPage() {
        return ResponseEntity.ok("상품 등록 페이지");
    }

    @PostMapping("/item/post")
    public ResponseEntity<?> itemPost(
            @RequestPart MultipartFile uploadFile,
            @RequestPart("itemRequest") ItemRequest itemRequest,
            Principal principal
    ) throws IllegalStateException, IOException {

        if (uploadFile.isEmpty()) {
            log.info("파일이 없어서 포스팅 실패했습니다.");
            return ResponseEntity
                    .ok("파일이 존재하지 않아 포스팅이 실패했습니다. \n파일을 넣고 다시 등록해주세요");
        }

        Long itemId = itemService.saveItem(
                uploadFile,
                itemRequest,
                principal.getName()
        );
        log.info("포스팅 성공!!");

        String url = "/item/" + itemId;
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    /*
    * remaining 을 클라이언트로 내보낸다.
    * 클라이언트는 remaining 이 0일경우 주문 버튼을 품절로 바꾼다.
     */
    @GetMapping("/item/{id}")
    public ResponseEntity<?> detail(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        Item entity = itemService.getItemEntity(id);

        if (CommonUtils.isNull(entity)) {
            return ResponseEntity.ok("해당 상품이 없어 조회가 불가능합니다.");
        }

        String user = principal.getName();
        Map<String, Object> map = new HashMap<>();
        String writer = entity.getUsers().getEmail();
        ItemResponse item = ItemMapper.entityToDtoDetail(entity);

        map.put("user", user);
        map.put("body", item);
        map.put("writer", writer);

        return ResponseEntity.ok(map);
    }

    /*
    * 상품 이미지
    * 뷰단에서 이미지 태그(html tag)를 이용해서 해당 api 를 걸면된다.
     */
    @GetMapping("/item/image/{saveFileName}")
    @ResponseBody
    public Resource showImage(
            @PathVariable("saveFileName") String saveFileName
    ) throws MalformedURLException {
        return new UrlResource("file:C:\\Temp\\upload\\" + saveFileName);
    }

    @PostMapping("/item/good/{id}")
    public ResponseEntity<?> updateGood(@PathVariable("id") Long id) {
        Item item = itemService.getItemEntity(id);

        if (CommonUtils.isNull(item)) {
            return ResponseEntity.ok("해당 상품이 없어 좋아요가 불가능합니다.");
        }

        String url = "/item/" + id;
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        itemService.updateGood(id);
        log.info("좋아요 업데이트!!");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @GetMapping("/item/edit/{id}")
    public ResponseEntity<?> editPage(@PathVariable("id") Long id) {
        ItemResponse item = itemService.getItemResponse(id);

        if (CommonUtils.isNull(item)) {
            return ResponseEntity.ok("해당 상품이 없어 수정이 불가능합니다.");
        }

        return ResponseEntity.ok(id);
    }

    /*
    * 상품 수정
    * 조건1 : 기존 사진을 유지하며 게시글 수정
    * 조건2 : 사진을 수정하면서 게시글 수정
     */
    @PostMapping("/item/edit/{id}")
    public ResponseEntity<?> editItem(
            @PathVariable("id") Long id,
            @RequestPart MultipartFile uploadFile,
            @RequestPart("itemRequest") ItemRequest itemRequest,
            Principal principal
    ) throws IllegalStateException, IOException {

        String url = "/item/" + id;
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        Item item = itemService.getItemEntity(id);

        if (CommonUtils.isNull(item)) {
            return ResponseEntity.ok("해당 상품이 없어 수정이 불가능합니다.");
        }

        if (!Objects.equals(item.getUsers().getEmail(), principal.getName())) {
            log.info("작성자와 현재 유저가 달라 수정 불가능.");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .build();
        }

        if (uploadFile.isEmpty()) {
            itemService.editItemNoFileChange(id, itemRequest);
            log.info("파일 수정 완료!!(파일교체 X)");

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .headers(httpHeaders)
                    .build();
        }

        itemService.editItemWithFile(
                id,
                itemRequest,
                uploadFile
        );
        log.info("파일 수정 완료!!(파일교체 O)");

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }
}
