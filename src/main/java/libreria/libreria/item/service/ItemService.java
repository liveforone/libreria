package libreria.libreria.item.service;

import libreria.libreria.item.model.Item;
import libreria.libreria.item.dto.ItemRequest;
import libreria.libreria.item.dto.ItemResponse;
import libreria.libreria.item.repository.ItemRepository;
import libreria.libreria.user.model.Users;
import libreria.libreria.user.repository.UserRepository;
import libreria.libreria.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {


    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    //== ItemResponse builder method ==//
    public ItemResponse dtoBuilder(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .content(item.getContent())
                .author(item.getAuthor())
                .saveFileName(item.getSaveFileName())
                .category(item.getCategory())
                .remaining(item.getRemaining())
                .year(item.getYear())
                .good(item.getGood())
                .build();
    }

    //== dto -> entity ==//
    public Item dtoToEntity(ItemRequest item) {
        return Item.builder()
                .id(item.getId())
                .title(item.getTitle())
                .content(item.getContent())
                .users(item.getUsers())
                .author(item.getAuthor())
                .saveFileName(item.getSaveFileName())
                .remaining(item.getRemaining())
                .category(item.getCategory())
                .year(item.getYear())
                .good(item.getGood())
                .build();
    }

    //== entity -> dto 편의 메소드1 - 리스트형식 ==//
    public List<ItemResponse> entityToDtoList(List<Item> itemList) {
        List<ItemResponse> dtoList = new ArrayList<>();

        for (Item item : itemList) {
            dtoList.add(dtoBuilder(item));
        }

        return dtoList;
    }

    //== entity ->  dto 편의메소드2 - 페이징 형식 ==//
    public Page<ItemResponse> entityToDtoPage(Page<Item> itemList) {
        return itemList.map(this::dtoBuilder);
    }

    //== entity -> dto 편의메소드3 - 엔티티 하나 ==//
    public ItemResponse entityToDtoDetail(Item item) {

        if (CommonUtils.isNull(item)) {
            return null;
        }
        return dtoBuilder(item);
    }


    //== 마이페이지 itemList ==//
    public List<ItemResponse> getItemListForMyPage(String email) {
        return entityToDtoList(
                itemRepository.findItemListByEmail(email)
        );
    }

    //== 상품 홈 itemList ==//
    public Page<ItemResponse> getItemList(Pageable pageable) {
        return entityToDtoPage(
                itemRepository.findAll(pageable)
        );
    }

    //== 상품 검색 ==//
    public Page<ItemResponse> getSearchListByTitle(String keyword, Pageable pageable) {
        return entityToDtoPage(
                itemRepository.searchItemByTitle(
                        keyword,
                        pageable
                )
        );
    }

    //== 카테고리 게시판 ==//
    public Page<ItemResponse> getCategoryList(String category, Pageable pageable) {
        return entityToDtoPage(
                itemRepository.findCategoryListByCategory(
                    category,
                    pageable
                )
        );
    }

    //== 연관관계인 작성자(user)를 뽑아주는 경우에만 사용한다. ==//
    public Item getItemEntity(Long id) {
        return itemRepository.findOneById(id);
    }

    //== 상품 등록 ==//
    @Transactional
    public Long saveItem(
            MultipartFile uploadFile,
            ItemRequest itemRequest,
            String user
    ) throws IOException {
        Users users = userRepository.findByEmail(user);
        UUID uuid = UUID.randomUUID();
        String saveFileName = uuid + "_" + uploadFile.getOriginalFilename();

        itemRequest.setSaveFileName(saveFileName);
        itemRequest.setUsers(users);
        uploadFile.transferTo(new File(saveFileName));

        return itemRepository.save(
                dtoToEntity(itemRequest)).getId();
    }

    @Transactional
    public void updateGood(Long id) {
        itemRepository.updateGood(id);
    }

    //== 파일 수정1 - 기존 파일 유지하며 ==//
    @Transactional
    public void editItemNoFileChange(Long id, ItemRequest itemRequest) {
        Item item = itemRepository.findOneById(id);

        itemRequest.setId(id);
        itemRequest.setUsers(item.getUsers());
        itemRequest.setSaveFileName(item.getSaveFileName());
        itemRequest.setGood(item.getGood());

        itemRepository.save(
                dtoToEntity(itemRequest)
        );
    }

    //== 파일 수정2 - 파일 교체하며 ==//
    @Transactional
    public void editItemWithFile(
            Long id,
            ItemRequest itemRequest,
            MultipartFile uploadFile
    ) throws IOException {
        UUID uuid = UUID.randomUUID();
        String saveFileName = uuid + "_" + uploadFile.getOriginalFilename();
        Item item = itemRepository.findOneById(id);

        itemRequest.setId(id);
        itemRequest.setUsers(item.getUsers());
        itemRequest.setGood(item.getGood());
        itemRequest.setSaveFileName(saveFileName);

        uploadFile.transferTo(new File(saveFileName));
        itemRepository.save(
                dtoToEntity(itemRequest)
        );
    }
}
