package app.hablemos.model;

public class SacadorDeAcentos {
    private static final String ORIGINAL
            = "ÁáÉéÍíÓóÚúÜü";
    private static final String REPLACEMENT
            = "AaEeIiOoUuUu";
    public static String stripAccents(String str) {
        if (str == null) {
            return null;
        }
        char[] array = str.toCharArray();
        for (int index = 0; index < array.length; index++) {
            int pos = ORIGINAL.indexOf(array[index]);
            if (pos > -1) {
                array[index] = REPLACEMENT.charAt(pos);
            }
        }
        return new String(array);
    }
}
