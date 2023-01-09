package libreria.libreria.user.util;

import libreria.libreria.user.model.Users;
import libreria.libreria.utility.CommonUtils;

public class UserEmail {

    public static boolean isDuplicateEmail(Users users) {

        return !CommonUtils.isNull(users);
    }
}
