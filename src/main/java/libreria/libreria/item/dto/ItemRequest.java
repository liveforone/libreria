package libreria.libreria.item.dto;

import libreria.libreria.user.model.Users;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequest {

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
}
