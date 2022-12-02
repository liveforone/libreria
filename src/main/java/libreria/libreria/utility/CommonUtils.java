package libreria.libreria.utility;

import java.util.List;

public class CommonUtils {

    /*
    * Null 체크 함수
    * Null 이면 true 를 반환
    * Null 이 아니면 false 를 반환
     */
    public static boolean isNull(Object obj) {

        //== 일반 객체 체크 ==//
        if(obj == null) {
            return true;
        }

        //== 문자열 체크 ==//
        if ((obj instanceof String) && (((String)obj).trim().length() == 0)) {
            return true;
        }

        //== 리스트 체크 ==//
        if (obj instanceof List) {
            return ((List<?>)obj).isEmpty();
        }

        return false;
    }
}
