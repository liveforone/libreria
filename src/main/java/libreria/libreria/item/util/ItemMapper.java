package libreria.libreria.item.util;

import libreria.libreria.item.dto.ItemRequest;
import libreria.libreria.item.dto.ItemResponse;
import libreria.libreria.item.model.Item;
import libreria.libreria.utility.CommonUtils;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    /*
    * dto ->  entity 변환 편의 메소드
     */
    public static Item dtoToEntity(ItemRequest item) {
        return Item.builder()
                .id(item.getId())
                .title(item.getTitle())
                .content(item.getContent())
                .users(item.getUsers())
                .author(item.getAuthor())
                .saveFileName(item.getSaveFileName())
                .remaining(item.getRemaining())
                .category(item.getCategory())
                .year(item.getYear())
                .good(item.getGood())
                .build();
    }

    /*
    * ItemResponse builder 편의 메소드
     */
    private static ItemResponse dtoBuilder(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .content(item.getContent())
                .author(item.getAuthor())
                .saveFileName(item.getSaveFileName())
                .category(item.getCategory())
                .remaining(item.getRemaining())
                .year(item.getYear())
                .good(item.getGood())
                .build();
    }

    /*
    * entity -> dto 편의 메소드1
    * 반환 타입 : 리스트형식
     */
    public static List<ItemResponse> entityToDtoList(List<Item> itemList) {
        return itemList
                .stream()
                .map(ItemMapper::dtoBuilder)
                .collect(Collectors.toList());
    }

    /*
    * entity ->  dto 편의 메소드2
    * 반환 타입 : 페이징 형식
     */
    public static Page<ItemResponse> entityToDtoPage(Page<Item> itemList) {
        return itemList.map(ItemMapper::dtoBuilder);
    }

    /*
    * entity -> dto 편의 메소드3
    * 반환 타입 : 엔티티 하나
     */
    public static ItemResponse entityToDtoDetail(Item item) {

        if (CommonUtils.isNull(item)) {
            return null;
        }
        return ItemMapper.dtoBuilder(item);
    }
}
