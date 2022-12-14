package libreria.libreria.item.util;

import libreria.libreria.item.dto.ItemRequest;
import libreria.libreria.item.dto.ItemResponse;
import libreria.libreria.item.model.Item;
import libreria.libreria.utility.CommonUtils;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static Item dtoToEntity(ItemRequest itemRequest) {
        return Item.builder()
                .id(itemRequest.getId())
                .title(itemRequest.getTitle())
                .content(itemRequest.getContent())
                .users(itemRequest.getUsers())
                .author(itemRequest.getAuthor())
                .remaining(itemRequest.getRemaining())
                .category(itemRequest.getCategory())
                .publishedYear(itemRequest.getPublishedYear())
                .good(itemRequest.getGood())
                .build();
    }

    private static ItemResponse dtoBuilder(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .content(item.getContent())
                .author(item.getAuthor())
                .category(item.getCategory())
                .remaining(item.getRemaining())
                .publishedYear(item.getPublishedYear())
                .good(item.getGood())
                .build();
    }

    public static List<ItemResponse> entityToDtoList(List<Item> items) {
        return items
                .stream()
                .map(ItemMapper::dtoBuilder)
                .collect(Collectors.toList());
    }

    public static Page<ItemResponse> entityToDtoPage(Page<Item> items) {
        return items.map(ItemMapper::dtoBuilder);
    }

    public static ItemResponse entityToDtoDetail(Item item) {

        if (CommonUtils.isNull(item)) {
            return null;
        }
        return ItemMapper.dtoBuilder(item);
    }
}
