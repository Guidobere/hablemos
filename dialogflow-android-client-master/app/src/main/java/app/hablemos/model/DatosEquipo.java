package app.hablemos.model;

public class DatosEquipo {

    private String fundacion;
    private String edad;
    private String apodos;
    private String ubicacion;
    private String estadio;
    private String capacidad;
    private String dt;

    public DatosEquipo() {

    }

    public DatosEquipo(String fundacion, String apodos, String ubicacion, String estadio, String capacidad, String dt) {
        this.fundacion = fundacion.split("\\(")[0].trim();
        this.edad = fundacion.split("\\(")[1].split("\\)")[0].trim();
        this.apodos = apodos;
        this.ubicacion = ubicacion;
        this.estadio = estadio;
        this.capacidad = capacidad;
        this.dt = dt;
    }

    @Override
    public String toString() {
        return " es un equipo de primera división argentina ubicado en " + ubicacion + ". Tiene " + edad + ", habiéndose fundado el " + fundacion +
                ". Su apodo es " + apodos + ". Su estadio es " + estadio + ", el cual tiene capacidad para " + capacidad +
                " y actualmente está siendo dirigido por " + dt + ".";
    }
}