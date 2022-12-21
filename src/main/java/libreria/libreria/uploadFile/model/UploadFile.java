package libreria.libreria.uploadFile.model;

import jakarta.persistence.*;
import libreria.libreria.item.model.Item;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String saveFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @Builder
    public UploadFile(Long id, String saveFileName, Item item) {
        this.id = id;
        this.saveFileName = saveFileName;
        this.item = item;
    }
}
