package libreria.libreria.uploadFile.util;

import libreria.libreria.item.model.Item;
import libreria.libreria.uploadFile.model.UploadFile;

public class UploadFileMapper {

    public static UploadFile makeEntityForSave(String saveFileName, Item item) {
        return UploadFile.builder()
                .saveFileName(saveFileName)
                .item(item)
                .build();
    }

    public static UploadFile makeEntityForEdit(Long id, String saveFileName, Item item) {
        return UploadFile.builder()
                .id(id)
                .saveFileName(saveFileName)
                .item(item)
                .build();
    }
}
