package app.hablemos.model;

public class Interaccion {

    private String hora;
    private String tipo;
    private String respuesta;
    private String observaciones;

    public Interaccion() {

    }

    public Interaccion(String hora, String tipo, String respuesta, String observaciones) {
        this.hora = hora;
        this.tipo = tipo;
        this.respuesta = respuesta;
        this.observaciones = observaciones;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}