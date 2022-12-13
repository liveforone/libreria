package libreria.libreria.user.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserConstants {

    NOT_DUPLICATE(1),
    PASSWORD_MATCH(1),
    PASSWORD_NOT_MATCH(0),
    DUPLICATE(0);

    private int value;
}
