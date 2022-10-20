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
//
//    //== 상품 검색 ==//
//    @Transactional(readOnly = true)
//    public Page<Item> getSearchList(String keyword, Pageable pageable) {
//        return itemRepository.findByTitleContaining(keyword, pageable);
//    }
//
//    //== 카테고리 게시판 ==//
//    @Transactional(readOnly = true)
//    public Page<Item> getCategoryList(String category, Pageable pageable) {
//        return itemRepository.findByCategory(category, pageable);
//    }

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
}
