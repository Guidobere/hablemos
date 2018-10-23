package app.hablemos.model.football;

public class Equipo {

    private String nombre;
    private String nombreReferencia;
    private String pagina;

    public Equipo() {

    }

    public Equipo(String nombre, String nombreReferencia, String pagina) {
        this.nombre = nombre;
        this.nombreReferencia = nombreReferencia;
        this.pagina = pagina;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPagina() {
        return pagina;
    }

    public void setPagina(String pagina) {
        this.pagina = pagina;
    }

    public String getNombreReferencia() {
        return nombreReferencia;
    }

    public void setNombreReferencia(String nombreReferencia) {
        this.nombreReferencia = nombreReferencia;
    }
}
