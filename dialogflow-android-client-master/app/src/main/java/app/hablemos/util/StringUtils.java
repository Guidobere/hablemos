package app.hablemos.util;

public class StringUtils {

    public static String replaceSpecialChar(String s) {
        return s.replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U")
                .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u");
    }

    public static String ponerMayusculas(String nombre) {
        String ret = "";
        try{
            String [] nombres = nombre.trim().split(" ");
            for (int i=0; i<nombres.length; i++) {
                String s1 = nombres[i].substring(0, 1).toUpperCase();
                ret += " " + s1 + nombres[i].substring(1).toLowerCase();
            }
        } catch (Exception e) {
            ret = nombre;
        }
        return ret.trim();
    }
}
