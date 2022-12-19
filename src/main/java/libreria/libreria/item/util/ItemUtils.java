package libreria.libreria.item.util;

import java.util.UUID;

public class ItemUtils {

    public static String makeSaveFileName(String originalFileName) {
        UUID uuid = UUID.randomUUID();

        return uuid + "_" + originalFileName;
    }
}
