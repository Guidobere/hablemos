package app.hablemos.model;

public class EquipoSpinner {


    private String nombre;
    private String nombreReferencia;

    public EquipoSpinner() {

    }

    public EquipoSpinner(String nombre, String nombreReferencia) {
        this.nombre = nombre;
        this.nombreReferencia = nombreReferencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreReferencia() {
        return nombreReferencia;
    }

    public void setNombreReferencia(String nombreReferencia) {
        this.nombreReferencia = nombreReferencia;
    }
}
