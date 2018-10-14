package app.hablemos.model;

public class Recordatorio {
    public String recordatorioID;
    public String dias;
    public String email;
    public String turno;

    public Recordatorio(){}

    public Recordatorio(String recordatorioID,String email,String  dias,String turno){
        this.recordatorioID = recordatorioID;
        this.email=email;
        this.dias=dias;
        this.turno=turno;
    }
}
