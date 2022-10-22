package libreria.libreria.item.controller;

import libreria.libreria.item.model.Item;
import libreria.libreria.item.model.ItemDto;
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

    @GetMapping("/item/search")
    public ResponseEntity<Page<Item>> itemSearch(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "good", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            @RequestParam("keyword") String keyword
    ) {
        Page<Item> searchList = itemService.getSearchList(keyword, pageable);

        return ResponseEntity.ok(searchList);
    }

    @GetMapping("/item/category/{category}")
    public ResponseEntity<Page<Item>> categoryHome(
            @PathVariable("category") String category,
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "good", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable
    ) {
        Page<Item> categoryList = itemService.getCategoryList(category, pageable);

        return ResponseEntity.ok(categoryList);
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

    /*
    수정 버튼은 해당 상품의 등록자에게만 보여주기 위해서 현재 로그인 유저를 함께 보낸다.
     */
    @GetMapping("/item/{id}")
    public ResponseEntity<Map<String, Object>> detail(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        String user = principal.getName();
        Map<String, Object> map = new HashMap<>();

        Item item = itemService.getDetail(id);
        String writer = item.getUsers().getEmail();

        map.put("user", user);
        map.put("body", item);
        map.put("writer", writer);

        return ResponseEntity.ok(map);
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

    @GetMapping("/item/edit/{id}")
    public ResponseEntity<Item> editPage(@PathVariable("id") Long id) {
        Item item = itemService.getDetail(id);

        return ResponseEntity.ok(item);
    }

    //== 상품 수정 ==//
    /*
    상품 수정은 경우가 있음.
    1. 기존 사진을 유지하며 게시글 수정
    2. 사진을 수정하면서 게시글 수정
     */
    @PostMapping("/item/edit/{id}")
    public ResponseEntity<?> editItem(
            @PathVariable("id") Long id,
            @RequestPart MultipartFile uploadFile,
            @RequestPart("itemDto") ItemDto itemDto
    ) throws IllegalStateException, IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/item/" + id));

        if (!uploadFile.isEmpty()) {  //파일을 바꿔서 수정
            itemService.editItemWithFile(id, itemDto, uploadFile);
            log.info("파일 수정 완료!!(파일교체 O)");
        } else {  //기존 파일 유지하며 수정
            itemService.editItemNoFileChange(id, itemDto);
            log.info("파일 수정 완료!!(파일교체 X)");
        }

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }
}