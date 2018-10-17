package app.hablemos.model;

public class DatosEquipo {

    private String fundacion;
    private String apodos;
    private String ubicacion;
    private String estadio;
    private String capacidad;
    private String dt;

    public DatosEquipo() {

    }

    public DatosEquipo(String fundacion, String apodos, String ubicacion, String estadio, String capacidad, String dt) {
        this.fundacion = fundacion;
        this.apodos = apodos;
        this.ubicacion = ubicacion;
        this.estadio = estadio;
        this.capacidad = capacidad;
        this.dt = dt;
    }

    public String getFundacion() {
        return fundacion;
    }

    public void setFundacion(String fundacion) {
        this.fundacion = fundacion;
    }

    public String getApodos() {
        return apodos;
    }

    public void setApodos(String apodos) {
        this.apodos = apodos;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstadio() {
        return estadio;
    }

    public void setEstadio(String estadio) {
        this.estadio = estadio;
    }

    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }
}
