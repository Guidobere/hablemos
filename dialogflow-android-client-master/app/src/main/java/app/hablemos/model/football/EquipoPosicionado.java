package app.hablemos.model.football;

public class EquipoPosicionado {

    private String nombre;
    private int posicion;
    private int puntos;
    private int partidosJugados;
    private int partidosGanados;
    private int partidosEmpatados;
    private int partidosPerdidos;
    private int golesAFavor;
    private int golesEnContra;
    private int diferencia;
    private boolean libertadores;
    private boolean sudamericana;
    private boolean desciende;
    private float promedio;

    public EquipoPosicionado() {

    }

    public EquipoPosicionado(String nombre, int posicion, int puntos, int partidosJugados, int partidosGanados, int partidosEmpatados, int partidosPerdidos, int golesAFavor, int golesEnContra, int diferencia) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.puntos = puntos;
        this.partidosJugados = partidosJugados;
        this.partidosGanados = partidosGanados;
        this.partidosEmpatados = partidosEmpatados;
        this.partidosPerdidos = partidosPerdidos;
        this.golesAFavor = golesAFavor;
        this.golesEnContra = golesEnContra;
        this.diferencia = diferencia;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPosicion() {
        return posicion;
    }

    @Override
    public String toString() {
        return " está en la posición " + posicion + " con " + puntos +
                " puntos.\nJugó " + partidosJugados + " partidos, ganó " + partidosGanados +
                ", empató " + partidosEmpatados + " y perdió " + partidosPerdidos +
                ".\nHizo " + golesAFavor + " goles, le convirtieron " + golesEnContra +
                " y su diferencia de gol es " + diferencia + ".";
    }

    public boolean isLibertadores() {
        return libertadores;
    }

    public void setLibertadores(boolean libertadores) {
        this.libertadores = libertadores;
    }

    public boolean isSudamericana() {
        return sudamericana;
    }

    public void setSudamericana(boolean sudamericana) {
        this.sudamericana = sudamericana;
    }

    public boolean isDesciende() {
        return desciende;
    }

    public void setDesciende(boolean desciende) {
        this.desciende = desciende;
    }

    public float getPromedio() {
        return promedio;
    }

    public void setPromedio(float promedio) {
        this.promedio = promedio;
    }
}