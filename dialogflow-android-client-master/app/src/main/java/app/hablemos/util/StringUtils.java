package app.hablemos.util;

public class StringUtils {

    public static String replaceSpecialChar(String s) {
        return s.replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U")
                .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u");
    }
}
