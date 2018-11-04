package app.hablemos.model;

import java.io.Serializable;

public class HorariosRecordatorios implements Serializable{
    public String manianaHora;
    public String manianaMinutos;
    public String tardeHora;
    public String tardeMinutos;
    public String nocheHora;
    public String nocheMinutos;

    public HorariosRecordatorios() {
    }

    public HorariosRecordatorios(String manianaHora, String manianaMinutos, String tardeHora,
                                 String tardeMinutos, String nocheHora, String nocheMinutos){
        this.manianaHora = manianaHora;
        this.manianaMinutos = manianaMinutos;
        this.tardeHora = tardeHora;
        this.tardeMinutos = tardeMinutos;
        this.nocheHora = nocheHora;
        this.nocheMinutos = nocheMinutos;
    }
}
