package libreria.libreria.bookmark.service;

import libreria.libreria.bookmark.model.Bookmark;
import libreria.libreria.bookmark.repository.BookmarkRepository;
import libreria.libreria.item.model.Item;
import libreria.libreria.item.repository.ItemRepository;
import libreria.libreria.user.model.Users;
import libreria.libreria.user.repository.UserRepository;
import libreria.libreria.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    //== entity -> map id & title ==//
    public Map<String, Object> entityToMap(List<Bookmark> bookmarkList) {
        Map<String, Object> map = new HashMap<>();
        List<Long> itemId = new ArrayList<>();
        List<String> itemTitle = new ArrayList<>();

        for (Bookmark bookmark : bookmarkList) {
            itemId.add(bookmark.getItem().getId());
            itemTitle.add(bookmark.getItem().getTitle());
        }

        map.put("boardId", itemId);
        map.put("boardTitle", itemTitle);

        return map;
    }

    public Map<String, Object> getBookmarkList(String email) {
        return entityToMap(
                bookmarkRepository.findByUserEmail(email)
        );
    }

    public Bookmark getBookmarkDetail(Long itemId, String email) {
        Users users = userRepository.findByEmail(email);
        Item item = itemRepository.findOneById(itemId);

        return bookmarkRepository.findOneBookmark(users, item);
    }

    @Transactional
    public void saveBookmark(String email, Long itemId) {
        Users users = userRepository.findByEmail(email);
        Item item = itemRepository.findOneById(itemId);

        Bookmark bookmark = Bookmark.builder()
                .users(users)
                .item(item)
                .build();

        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void cancelBookmark(String email, Long itemId) {
        Users users = userRepository.findByEmail(email);
        Item item = itemRepository.findOneById(itemId);

        Bookmark bookmark = bookmarkRepository.findOneBookmark(users, item);

        if (!CommonUtils.isNull(bookmark)) {
            bookmarkRepository.deleteById(bookmark.getId());
        }
    }
}
