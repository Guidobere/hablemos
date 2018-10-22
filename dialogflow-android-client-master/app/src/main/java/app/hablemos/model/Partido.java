package app.hablemos.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Partido {

    private String fecha;
    private String rival;
    private String localia;
    private String resultado;
    private String dia;
    private Date diaDePartido;

    public Partido(String fecha, String rival, String localia, String resultado, String dia) {
        this.fecha = fecha;
        this.rival = rival;
        if (localia.equalsIgnoreCase("L")) this.localia = "local";
        else this.localia = "visitante";
        this.resultado = resultado;
        this.dia = dia;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            this.diaDePartido = formatter.parse(this.dia);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getFecha() {
        return fecha;
    }

    public String getResultado() {
        return resultado;
    }

    public String getDia() {
        return dia;
    }

    public String getRival() {
        return rival;
    }

    public Date getDiaDePartido() {
        if (diaDePartido == null) {
            return new Date();
        }
        return diaDePartido;
    }

    public String toString(String rival) {
        return " jugará de " + localia + " contra " + rival + " el día " + dia + " por la fecha " + fecha;
    }

    public String toStringUltimo(String rival) {
        return " jugó de " + localia + " contra " + rival + " el día " + dia + " por la fecha " + fecha + " y terminó " + resultado;
    }

    public String toStringEnCurso(String rival) {
        return " está jugando de " + localia + " contra " + rival + " por la fecha " + fecha + " y va " + resultado;
    }
}