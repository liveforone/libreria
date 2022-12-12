package libreria.libreria.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
