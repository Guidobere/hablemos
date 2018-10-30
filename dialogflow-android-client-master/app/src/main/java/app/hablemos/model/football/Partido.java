package app.hablemos.model.football;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.hablemos.util.DateUtils;

public class Partido {

    private String fecha;
    private String rival;
    private String localia;
    private String resultado;
    private String dia;
    private Date diaDePartido;
    private String diaSemana;

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
            this.diaSemana = DateUtils.getDiaSemanaFromDate(diaDePartido);
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
        return " jugará de " + localia + " contra " + rival + " el día " + this.getDiaSemana() + dia  + " por la fecha " + fecha;
    }

    public String toStringUltimo(String rival) {
        String retorno = " jugó de " + localia + " contra " + rival + " el día " + this.getDiaSemana() + dia + " por la fecha " + fecha;
        int aFavor = Integer.parseInt(resultado.split("-")[0].trim());
        int enContra = Integer.parseInt(resultado.split("-")[1].trim());
        if (aFavor > enContra) {
            retorno += " y ganó ";
        } else if (aFavor < enContra) {
            retorno += " y perdió ";
        } else if (aFavor == enContra) {
            retorno += " y empató ";
        } else {
            retorno += " y terminó ";
        }
        return retorno + resultado;
    }

    public String toStringEnCurso(String rival) {
        return " está jugando de " + localia + " contra " + rival + " por la fecha " + fecha + " y va " + resultado;
    }

    private String getDiaSemana(){
        return TextUtils.isEmpty(diaSemana)?"":diaSemana + " ";
    }
}