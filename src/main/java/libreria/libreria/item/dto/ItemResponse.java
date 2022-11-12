package libreria.libreria.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemResponse {

    private Long id;
    private String title;
    private String content;
    private String author;
    private String saveFileName;
    private int remaining;
    private String category;
    private String year;
    private int good;

    @Builder
    public ItemResponse(Long id, String title, String content, String author, String saveFileName, int remaining, String category, String year, int good) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.saveFileName = saveFileName;
        this.remaining = remaining;
        this.category = category;
        this.year = year;
        this.good = good;
    }
}
