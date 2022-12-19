package libreria.libreria.bookmark.util;

import libreria.libreria.bookmark.model.Bookmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookmarkMapper {

    /*
    * entity -> map
    * 타입 : String id, String title
     */
    public static Map<String, Object> entityToMap(List<Bookmark> bookmarks) {
        Map<String, Object> map = new HashMap<>();
        List<Long> itemId = new ArrayList<>();
        List<String> itemTitle = new ArrayList<>();

        for (Bookmark bookmark : bookmarks) {
            itemId.add(bookmark.getItem().getId());
            itemTitle.add(bookmark.getItem().getTitle());
        }

        map.put("itemId", itemId);
        map.put("itemTitle", itemTitle);

        return map;
    }
}
