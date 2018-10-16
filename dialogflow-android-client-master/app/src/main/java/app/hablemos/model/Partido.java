package app.hablemos.model;

import java.util.Date;

public class Partido {

    private Equipo local;
    private Equipo visitante;
    private Date fechahora;
    private int golesLocal;
    private int golesVisitante;

    public Partido() {

    }

    public Partido(Equipo local, Equipo visitante, Date fechahora, int golesLocal, int golesVisitante) {
        this.local = local;
        this.visitante = visitante;
        this.fechahora = fechahora;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
    }

    public Equipo getLocal() {
        return local;
    }

    public void setLocal(Equipo local) {
        this.local = local;
    }

    public Equipo getVisitante() {
        return visitante;
    }

    public void setVisitante(Equipo visitante) {
        this.visitante = visitante;
    }

    public Date getFechahora() {
        return fechahora;
    }

    public void setFechahora(Date fechahora) {
        this.fechahora = fechahora;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(int golesLocal) {
        this.golesLocal = golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(int golesVisitante) {
        this.golesVisitante = golesVisitante;
    }
}
