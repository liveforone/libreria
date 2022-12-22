package libreria.libreria.bookmark.service;

import libreria.libreria.bookmark.model.Bookmark;
import libreria.libreria.bookmark.repository.BookmarkRepository;
import libreria.libreria.bookmark.util.BookmarkMapper;
import libreria.libreria.item.model.Item;
import libreria.libreria.item.repository.ItemRepository;
import libreria.libreria.user.model.Users;
import libreria.libreria.user.repository.UserRepository;
import libreria.libreria.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public Bookmark getBookmarkDetail(Long itemId, String email) {
        Users users = userRepository.findByEmail(email);
        Item item = itemRepository.findOneById(itemId);

        return bookmarkRepository.findOneBookmark(users, item);
    }

    public Map<String, Object> getBookmarks(String email) {
        return BookmarkMapper.entityToMap(
                bookmarkRepository.findBookmarksByUserEmail(email)
        );
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
