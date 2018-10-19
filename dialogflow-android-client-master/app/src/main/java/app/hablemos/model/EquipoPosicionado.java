package app.hablemos.model;

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
        return nombre + " está en la posición " + posicion + " con " + puntos +
                " puntos.\nJugó " + partidosJugados + " partidos, ganó " + partidosGanados +
                ", empató " + partidosEmpatados + " y perdió " + partidosPerdidos +
                ".\nHizo " + golesAFavor + " goles, le convirtieron " + golesEnContra +
                " y su diferencia de gol es " + diferencia + ".";
    }
}