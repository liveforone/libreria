package libreria.libreria.item.model;

import libreria.libreria.user.model.Users;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemDto {

    private Long id;
    private String title;
    private String content;
    private String author;
    private String saveFileName;
    private int remaining;
    private Users users;
    private String category;
    private String year;
    private int good;

    public Item toEntity() {
        return Item.builder()
                .id(id)
                .title(title)
                .content(content)
                .users(users)
                .author(author)
                .saveFileName(saveFileName)
                .remaining(remaining)
                .category(category)
                .year(year)
                .good(good)
                .build();
    }
}
