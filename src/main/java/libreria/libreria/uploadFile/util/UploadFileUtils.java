package libreria.libreria.uploadFile.util;

import java.util.UUID;

public class UploadFileUtils {

    public static String makeSaveFileName(String originalFileName) {
        UUID uuid = UUID.randomUUID();

        return uuid + "_" + originalFileName;
    }
}
