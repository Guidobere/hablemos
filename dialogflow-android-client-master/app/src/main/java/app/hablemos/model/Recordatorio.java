package app.hablemos.model;

public class Recordatorio {
    public String recordatorioID;
    public String dias;
    public String email;
    public String turno;

    public Recordatorio(){}

    public Recordatorio(String email,String  dias,String turno){
        this.email=email;
        this.dias=dias;
        this.turno=turno;
    }
}
