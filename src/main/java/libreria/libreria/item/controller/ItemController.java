package libreria.libreria.item.controller;

import libreria.libreria.item.model.Item;
import libreria.libreria.item.dto.ItemRequest;
import libreria.libreria.item.dto.ItemResponse;
import libreria.libreria.item.service.ItemService;
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
import java.net.URI;
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
        Page<ItemResponse> searchList = itemService.getSearchList(keyword, pageable);

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
        Page<ItemResponse> categoryList = itemService.getCategoryList(category, pageable);

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

        if (!uploadFile.isEmpty()) {
            Long itemId = itemService.saveItem(uploadFile, itemRequest, principal.getName());
            log.info("포스팅 성공!!");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(URI.create("/item/" + itemId));

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .headers(httpHeaders)
                    .build();
        } else {
            log.info("파일이 없어서 포스팅 실패했습니다.");
            return ResponseEntity.ok("파일이 존재하지 않아 포스팅이 실패했습니다. \n파일을 넣고 다시 등록해주세요");
        }
    }

    /*
    수정 버튼은 해당 상품의 등록자에게만 보여주기 위해서 현재 로그인 유저를 함께 보낸다.
    remaining을 보고 뷰에서는 0일경우 주문 버튼을 품절로 바꾼다.
    즉 remaining을 보고 뷰에서 주문 가능한지 불가능한지 판별한다.
    엔티티 리턴시 사용자 엔티티 전부가 적나라하게 노출되기 때문에
    엔티티에서 유저 이메일 빼고, dto로 따로 뷰에 노출시키는 방식을 사용했다.
    성능보단 보안이 우선 !!
     */
    @GetMapping("/item/{id}")
    public ResponseEntity<?> detail(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        Item entity = itemService.getItemEntity(id);

        if (entity != null) {
            String user = principal.getName();
            Map<String, Object> map = new HashMap<>();
            String writer = entity.getUsers().getEmail();
            ItemResponse item = itemService.entityToDtoDetail(entity);

            map.put("user", user);
            map.put("body", item);
            map.put("writer", writer);

            return ResponseEntity.ok(map);
        } else {
            return ResponseEntity.ok("해당 상품이 없어 조회가 불가능합니다.");
        }
    }

    //== 상품 상세조회 이미지 ==//
    /*
    뷰단에서 이미지 태그(html tag)를 이용해서 해당 url을 걸면된다.
     */
    @GetMapping("/item/image/{saveFileName}")
    @ResponseBody
    public Resource showImage(
            @PathVariable("saveFileName") String saveFileName
    ) throws MalformedURLException {
        return new UrlResource("file:C:\\Temp\\upload\\" + saveFileName);
    }

    //== 상품 좋아요 ==//
    @PostMapping("/item/good/{id}")
    public ResponseEntity<?> updateGood(@PathVariable("id") Long id) {
        Item item = itemService.getItemEntity(id);

        if (item != null) {
            String url = "/item/" + id;
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(URI.create(url));

            itemService.updateGood(id);
            log.info("좋아요 업데이트!!");

            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .headers(httpHeaders)
                    .build();
        } else {
            return ResponseEntity.ok("해당 상품이 없어 좋아요가 불가능합니다.");
        }
    }

    @GetMapping("/item/edit/{id}")
    public ResponseEntity<?> editPage(@PathVariable("id") Long id) {
        ItemResponse item = itemService.entityToDtoDetail(itemService.getItemEntity(id));

        return ResponseEntity.ok(Objects.requireNonNullElse(item, "해당 상품이 없어 수정이 불가능합니다."));
    }

    //== 상품 수정 ==//
    /*
    상품 수정은 경우가 있음.
    1. 기존 사진을 유지하며 게시글 수정
    2. 사진을 수정하면서 게시글 수정
    또한 뷰에서 작성자와 현재 유저를 판별했더라도 수정/삭제는 서버단에서 한 번 더 판별한다.
     */
    @PostMapping("/item/edit/{id}")
    public ResponseEntity<?> editItem(
            @PathVariable("id") Long id,
            @RequestPart MultipartFile uploadFile,
            @RequestPart("itemRequest") ItemRequest itemRequest,
            Principal principal
    ) throws IllegalStateException, IOException {
        Item item = itemService.getItemEntity(id);

        if (item != null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(URI.create("/item/" + id));

            if (Objects.equals(item.getUsers().getEmail(), principal.getName())) {

                if (!uploadFile.isEmpty()) {  //파일을 바꿔서 수정
                    itemService.editItemWithFile(id, itemRequest, uploadFile);
                    log.info("파일 수정 완료!!(파일교체 O)");
                } else {  //기존 파일 유지하며 수정
                    itemService.editItemNoFileChange(id, itemRequest);
                    log.info("파일 수정 완료!!(파일교체 X)");
                }

                return ResponseEntity
                        .status(HttpStatus.MOVED_PERMANENTLY)
                        .headers(httpHeaders)
                        .build();

            } else {
                log.info("작성자와 현재 유저가 달라 수정 불가능.");
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .build();
            }

        } else {
            return ResponseEntity.ok("해당 상품이 없어 수정이 불가능합니다.");
        }
    }
}
