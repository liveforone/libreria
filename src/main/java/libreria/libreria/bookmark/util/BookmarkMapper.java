package libreria.libreria.bookmark.util;

import libreria.libreria.bookmark.model.Bookmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookmarkMapper {

    //== entity -> map id & title ==//
    public static Map<String, Object> entityToMap(List<Bookmark> bookmarkList) {
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
}
