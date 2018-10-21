package app.hablemos.model;

public class PartidoActual {

    private String estado;
    private String equipoLocal;
    private String equipoVisitante;
    private String golesLocal;
    private String golesVisitante;
    private int golesEquipoLocal;
    private int golesEquipoVisitante;
    private String tiempoJuego;
    private String horaJuego;

    public PartidoActual() {
        this.golesEquipoLocal = 0;
        this.golesEquipoVisitante = 0;
        this.golesLocal = "";
        this.golesVisitante = "";
        this.tiempoJuego = "";
        this.horaJuego = "";
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEquipoLocal() {
        return equipoLocal;
    }

    public void setEquipoLocal(String equipoLocal) {
        this.equipoLocal = equipoLocal;
    }

    public String getEquipoVisitante() {
        return equipoVisitante;
    }

    public void setEquipoVisitante(String equipoVisitante) {
        this.equipoVisitante = equipoVisitante;
    }

    public String getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(String golesLocal) {
        this.golesLocal = golesLocal;
    }

    public String getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(String golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    public int getGolesEquipoLocal() {
        return golesEquipoLocal;
    }

    public void setGolesEquipoLocal(int golesEquipoLocal) {
        this.golesEquipoLocal = golesEquipoLocal;
    }

    public int getGolesEquipoVisitante() {
        return golesEquipoVisitante;
    }

    public void setGolesEquipoVisitante(int golesEquipoVisitante) {
        this.golesEquipoVisitante = golesEquipoVisitante;
    }

    public String getTiempoJuego() {
        return tiempoJuego;
    }

    public void setTiempoJuego(String tiempoJuego) {
        this.tiempoJuego = tiempoJuego;
    }

    public String getHoraJuego() {
        return horaJuego;
    }

    public void setHoraJuego(String horaJuego) {
        this.horaJuego = horaJuego;
    }
}
