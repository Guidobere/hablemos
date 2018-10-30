package app.hablemos.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static String getDiaSemanaActual() {
        return DateUtils.getDiaSemana(Calendar.getInstance().get(Calendar.DAY_OF_WEEK), false);
    }

    public static String getDiaSemanaFromDate(Date dia){
        Calendar cDia = Calendar.getInstance();
        cDia.setTime(dia);
        return DateUtils.getDiaSemana(cDia.get(Calendar.DAY_OF_WEEK), true);
    }

    private static String getDiaSemana(int i, boolean conTilde) {
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
                dia = conTilde?"miércoles":"miercoles";
                break;
            case 5:
                dia = "jueves";
                break;
            case 6:
                dia = "viernes";
                break;
            case 7:
                dia = conTilde?"sábado":"sabado";
                break;
        }
        return dia;
    }

    public static String getNowString() {
        Calendar calendar = Calendar.getInstance();
        return (calendar.get(Calendar.DATE)) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR);
    }
}
