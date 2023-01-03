package libreria.libreria.bookmark.service;

import libreria.libreria.bookmark.model.Bookmark;
import libreria.libreria.bookmark.repository.BookmarkRepository;
import libreria.libreria.bookmark.util.BookmarkMapper;
import libreria.libreria.item.model.Item;
import libreria.libreria.user.model.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public Bookmark getBookmarkDetail(Item item, Users users) {

        return bookmarkRepository.findOneBookmark(users, item);
    }

    public Map<String, Object> getBookmarks(String email) {
        return BookmarkMapper.entityToMap(
                bookmarkRepository.findBookmarksByUserEmail(email)
        );
    }

    @Transactional
    public void saveBookmark(Users users, Item item) {
        Bookmark bookmark = Bookmark.builder()
                .users(users)
                .item(item)
                .build();

        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void cancelBookmark(Bookmark bookmark) {
        bookmarkRepository.deleteById(bookmark.getId());
    }
}
