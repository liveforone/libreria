package libreria.libreria.item.service;

import libreria.libreria.item.model.Item;
import libreria.libreria.item.model.ItemDto;
import libreria.libreria.item.repository.ItemRepository;
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
    public List<Item> getItemListForMyPage(String email) {
        return itemRepository.findItemWithJoinByEmail(email);
    }

    //== 상품 홈 itemList ==//
    public Page<Item> getItemList(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    //== 상품 검색 ==//
    public Page<Item> getSearchList(String keyword, Pageable pageable) {
        return itemRepository.searchByTitle(keyword, pageable);
    }

    //== 카테고리 게시판 ==//
    public Page<Item> getCategoryList(String category, Pageable pageable) {
        return itemRepository.findCategoryListByCategory(category, pageable);
    }

    public Item getDetail(Long id) {
        return itemRepository.findOneById(id);
    }

    //== 상품 등록 ==//
    @Transactional
    public void saveItem(MultipartFile uploadFile, ItemDto itemDto, String user) throws IOException {
        Users users = userRepository.findByEmail(user);
        UUID uuid = UUID.randomUUID();
        String saveFileName = uuid + "_" + uploadFile.getOriginalFilename();

        itemDto.setSaveFileName(saveFileName);
        itemDto.setUsers(users);
        uploadFile.transferTo(new File(saveFileName));

        itemRepository.save(itemDto.toEntity());
    }

    @Transactional
    public void updateGood(Long id) {
        itemRepository.updateGood(id);
    }

    //== 파일 수정1 - 기존 파일 유지하며 ==//
    @Transactional
    public void editItemNoFileChange(Long id, ItemDto itemDto) {
        Item item = itemRepository.findOneById(id);

        itemDto.setId(id);
        itemDto.setUsers(item.getUsers());
        itemDto.setSaveFileName(item.getSaveFileName());
        itemDto.setGood(item.getGood());

        itemRepository.save(itemDto.toEntity());
    }

    //== 파일 수정2 - 파일 교체하며 ==//
    @Transactional
    public void editItemWithFile(Long id, ItemDto itemDto, MultipartFile uploadFile) throws IOException {
        UUID uuid = UUID.randomUUID();
        String saveFileName = uuid + "_" + uploadFile.getOriginalFilename();
        Item item = itemRepository.findOneById(id);

        itemDto.setId(id);
        itemDto.setUsers(item.getUsers());
        itemDto.setGood(item.getGood());
        itemDto.setSaveFileName(saveFileName);

        uploadFile.transferTo(new File(saveFileName));
        itemRepository.save(itemDto.toEntity());
    }
}
