package libreria.libreria.item.service;

import libreria.libreria.item.model.Item;
import libreria.libreria.item.dto.ItemRequest;
import libreria.libreria.item.dto.ItemResponse;
import libreria.libreria.item.repository.ItemRepository;
import libreria.libreria.item.util.ItemMapper;
import libreria.libreria.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<ItemResponse> getItemsForMyPage(String email) {
        return ItemMapper.entityToDtoList(
                itemRepository.findItemsByEmail(email)
        );
    }

    public Page<ItemResponse> getAllItems(Pageable pageable) {
        return ItemMapper.entityToDtoPage(
                itemRepository.findAll(pageable)
        );
    }

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
    public Long saveItem(ItemRequest itemRequest, String email) {
        itemRequest.setUsers(userRepository.findByEmail(email));

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
            ItemRequest itemRequest
    ) {
        Item item = itemRepository.findOneById(id);

        itemRequest.setId(id);
        itemRequest.setUsers(item.getUsers());
        itemRequest.setGood(item.getGood());

        itemRepository.save(
                ItemMapper.dtoToEntity(itemRequest)
        );
    }

    @Transactional
    public void plusItemRemaining(int count, Long itemId) {
        itemRepository.plusRemaining(count, itemId);
    }

    @Transactional
    public void minusItemRemaining(int count, Long itemId) {
        itemRepository.minusRemaining(count, itemId);
    }
}
