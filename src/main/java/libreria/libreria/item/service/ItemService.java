package libreria.libreria.item.service;

import libreria.libreria.item.model.Item;
import libreria.libreria.item.dto.ItemRequest;
import libreria.libreria.item.dto.ItemResponse;
import libreria.libreria.item.repository.ItemRepository;
import libreria.libreria.item.util.ItemMapper;
import libreria.libreria.user.model.Users;
import libreria.libreria.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    //== 마이페이지 itemList ==//
    public List<ItemResponse> getItemListForMyPage(String email) {
        return ItemMapper.entityToDtoList(
                itemRepository.findItemListByEmail(email)
        );
    }

    //== 상품 홈 itemList ==//
    public Page<ItemResponse> getItemList(Pageable pageable) {
        return ItemMapper.entityToDtoPage(
                itemRepository.findAll(pageable)
        );
    }

    //== 상품 검색 ==//
    public Page<ItemResponse> getSearchListByTitle(String keyword, Pageable pageable) {
        return ItemMapper.entityToDtoPage(
                itemRepository.searchItemByTitle(
                        keyword,
                        pageable
                )
        );
    }

    //== 카테고리 게시판 ==//
    public Page<ItemResponse> getCategoryList(String category, Pageable pageable) {
        return ItemMapper.entityToDtoPage(
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

    public ItemResponse getItemResponse(Long id) {
        return itemRepository.findOneDtoById(id);
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
                ItemMapper.dtoToEntity(itemRequest)).getId();
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
                ItemMapper.dtoToEntity(itemRequest)
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
                ItemMapper.dtoToEntity(itemRequest)
        );
    }
}
