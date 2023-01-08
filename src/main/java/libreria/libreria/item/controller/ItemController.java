package libreria.libreria.item.controller;

import jakarta.servlet.http.HttpServletRequest;
import libreria.libreria.item.model.Item;
import libreria.libreria.item.dto.ItemRequest;
import libreria.libreria.item.dto.ItemResponse;
import libreria.libreria.item.service.ItemService;
import libreria.libreria.item.util.ItemMapper;
import libreria.libreria.uploadFile.service.UploadFileService;
import libreria.libreria.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final UploadFileService uploadFileService;

    @GetMapping("/item")
    public ResponseEntity<Page<ItemResponse>> itemHome(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "good", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable
    ) {
        Page<ItemResponse> items = itemService.getAllItems(pageable);

        return ResponseEntity.ok(items);
    }

    @GetMapping("/item/search")
    public ResponseEntity<Page<ItemResponse>> itemSearchPage(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "good", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            @RequestParam("keyword") String keyword
    ) {
        Page<ItemResponse> items =
                itemService.searchItemsByTitle(keyword, pageable);

        return ResponseEntity.ok(items);
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
        Page<ItemResponse> categories =
                itemService.getCategories(category, pageable);

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/item/post")
    public ResponseEntity<?> itemPostPage() {
        return ResponseEntity.ok("상품 등록 페이지");
    }

    @PostMapping("/item/post")
    public ResponseEntity<?> itemPost(
            @RequestPart MultipartFile uploadFile,
            @RequestPart("itemRequest") ItemRequest itemRequest,
            Principal principal,
            HttpServletRequest request
    ) throws IllegalStateException, IOException {

        if (uploadFile.isEmpty()) {
            log.info("파일이 없어서 포스팅 실패했습니다.");
            return ResponseEntity
                    .ok("파일이 존재하지 않아 포스팅이 실패했습니다. \n파일을 넣고 다시 등록해주세요");
        }

        Long itemId = itemService.saveItem(
                itemRequest,
                principal.getName()
        );
        log.info("포스팅 성공");

        uploadFileService.saveFile(uploadFile, itemId);
        log.info("파일 저장 성공");

        String url = "/item/" + itemId;
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<?> itemDetail(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        Item itemEntity = itemService.getItemEntity(id);

        if (CommonUtils.isNull(itemEntity)) {
            log.info("상품이 존재하지 않음. 잘못된 경로.");
            return ResponseEntity.ok("해당 상품이 없어 조회가 불가능합니다.");
        }

        Map<String, Object> map = new HashMap<>();
        String user = principal.getName();
        String writer = itemEntity.getUsers().getEmail();
        ItemResponse item = ItemMapper.entityToDtoDetail(itemEntity);
        String saveFileName = uploadFileService.getSaveFileName(id);

        map.put("user", user);
        map.put("body", item);
        map.put("writer", writer);
        map.put("saveFileName", saveFileName);

        return ResponseEntity.ok(map);
    }

    @PutMapping("/item/good/{id}")
    public ResponseEntity<?> updateGood(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ) {
        Item item = itemService.getItemEntity(id);

        if (CommonUtils.isNull(item)) {
            log.info("상품이 존재하지 않음. 잘못된 경로.");
            return ResponseEntity.ok("해당 상품이 없어 좋아요가 불가능합니다.");
        }

        itemService.updateGood(id);
        log.info("좋아요 업데이트");

        String url = "/item/" + id;
        return CommonUtils.makeResponseEntityForRedirect(url, request);
    }

    @GetMapping("/item/edit/{id}")
    public ResponseEntity<?> editItemPage(@PathVariable("id") Long id) {
        ItemResponse item = itemService.getItemDto(id);

        if (CommonUtils.isNull(item)) {
            log.info("상품이 존재하지 않음. 잘못된 경로.");
            return ResponseEntity.ok("해당 상품이 없어 수정이 불가능합니다.");
        }

        return ResponseEntity.ok(item);
    }

    /*
    * 상품 수정
    * 조건1 : 기존 사진을 유지하며 게시글 수정
    * 조건2 : 사진을 수정하면서 게시글 수정
     */
    @PutMapping("/item/edit/{id}")
    public ResponseEntity<?> editItem(
            @PathVariable("id") Long id,
            @RequestPart("itemRequest") ItemRequest itemRequest,
            @RequestPart MultipartFile uploadFile,
            Principal principal,
            HttpServletRequest request
    ) throws IllegalStateException, IOException {
        String url = "/item/" + id;
        ResponseEntity<String> response = CommonUtils
                .makeResponseEntityForRedirect(url, request);

        Item item = itemService.getItemEntity(id);

        if (CommonUtils.isNull(item)) {
            log.info("상품이 존재하지 않음. 잘못된 경로.");
            return ResponseEntity.ok("해당 상품이 없어 수정이 불가능합니다.");
        }

        String writer = item.getUsers().getEmail();
        String currentUserEmail = principal.getName();
        if (!Objects.equals(writer, currentUserEmail)) {
            log.info("작성자와 현재 유저가 달라 수정 불가능.");
            return ResponseEntity.ok("작성자가 아니라서 수정이 불가능합니다.");
        }

        if (uploadFile.isEmpty()) {
            itemService.editItem(item, itemRequest);
            log.info("게시글 수정 완료(파일 수정X)");

            return response;
        }

        itemService.editItem(item, itemRequest);
        log.info("게시글 수정 완료(파일 수정O)");

        uploadFileService.editFile(uploadFile, id);
        log.info("파일 교체 완료");

        return response;
    }
}
