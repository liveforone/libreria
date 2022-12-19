package libreria.libreria.item.service;

import libreria.libreria.item.model.Item;
import libreria.libreria.item.dto.ItemRequest;
import libreria.libreria.item.dto.ItemResponse;
import libreria.libreria.item.repository.ItemRepository;
import libreria.libreria.item.util.ItemMapper;
import libreria.libreria.item.util.ItemUtils;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    /*
    * item list
    * when : my-page
     */
    public List<ItemResponse> getItemsForMyPage(String email) {
        return ItemMapper.entityToDtoList(
                itemRepository.findItemsByEmail(email)
        );
    }

    /*
    * item list
    * when : 상품 home
     */
    public Page<ItemResponse> getAllItems(Pageable pageable) {
        return ItemMapper.entityToDtoPage(
                itemRepository.findAll(pageable)
        );
    }

    /*
    * 상품 검색
     */
    public Page<ItemResponse> searchItemsByTitle(String keyword, Pageable pageable) {
        return ItemMapper.entityToDtoPage(
                itemRepository.searchItemsByTitle(
                        keyword,
                        pageable
                )
        );
    }

    public Page<ItemResponse> getCategories(String category, Pageable pageable) {
        return ItemMapper.entityToDtoPage(
                itemRepository.findCategoriesByCategory(
                    category,
                    pageable
                )
        );
    }

    public Item getItemEntity(Long id) {
        return itemRepository.findOneById(id);
    }

    public ItemResponse getItemDto(Long id) {
        return itemRepository.findOneDtoById(id);
    }

    @Transactional
    public Long saveItem(
            MultipartFile uploadFile,
            ItemRequest itemRequest,
            String user
    ) throws IOException {
        Users users = userRepository.findByEmail(user);
        String saveFileName = ItemUtils.makeSaveFileName(uploadFile.getOriginalFilename());

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

    /*
    * 파일 수정 1
    * 조건 : 기존 파일 유지
     */
    @Transactional
    public void editItemWithNoFile(Long id, ItemRequest itemRequest) {
        Item item = itemRepository.findOneById(id);

        itemRequest.setId(id);
        itemRequest.setUsers(item.getUsers());
        itemRequest.setSaveFileName(item.getSaveFileName());
        itemRequest.setGood(item.getGood());

        itemRepository.save(
                ItemMapper.dtoToEntity(itemRequest)
        );
    }

    /*
    * 파일 수정 2
    * 조건 : 기존 파일 변경
     */
    @Transactional
    public void editItemWithFile(
            Long id,
            ItemRequest itemRequest,
            MultipartFile uploadFile
    ) throws IOException {
        Item item = itemRepository.findOneById(id);
        String saveFileName = ItemUtils.makeSaveFileName(uploadFile.getOriginalFilename());

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
