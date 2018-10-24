package app.hablemos.util;

import java.util.Calendar;

public class DateUtils {

    public static String getDiaSemanaActual() {
        return DateUtils.getDiaSemana(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
    }

    private static String getDiaSemana(int i) {
        String dia = "";
        switch (i) {
            case 1:
                dia = "domingo";
                break;
            case 2:
                dia = "lunes";
                break;
            case 3:
                dia = "martes";
                break;
            case 4:
                dia = "miercoles";
                break;
            case 5:
                dia = "jueves";
                break;
            case 6:
                dia = "viernes";
                break;
            case 7:
                dia = "sabado";
                break;
        }
        return dia;
    }

    public static String getNowString() {
        Calendar calendar = Calendar.getInstance();
        return (calendar.get(Calendar.DATE)) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR);
    }
}
