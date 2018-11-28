package app.hablemos.model.football;

import java.util.Comparator;

public class Comparators {

    public static Comparator<Partido> comparadorDeFecha = new Comparator<Partido>() {
        @Override
        public int compare(Partido partido1, Partido partido2) {
            return partido1.getDiaDePartido().compareTo(partido2.getDiaDePartido());
        }
    };

    public static Comparator<EquipoPosicionado> comparadorDePromedios = new Comparator<EquipoPosicionado>() {
        @Override
        public int compare(EquipoPosicionado eq1, EquipoPosicionado eq2) {
            return Float.compare(eq1.getPromedio(), eq2.getPromedio());
        }
    };

    public static Comparator<EquipoPosicionado> comparadorGolesAFavor = new Comparator<EquipoPosicionado>() {
        @Override
        public int compare(EquipoPosicionado eq1, EquipoPosicionado eq2) {
            return Float.compare(eq1.getGolesAFavor(), eq2.getGolesAFavor());
        }
    };

    public static Comparator<EquipoPosicionado> comparadorGolesEnContra = new Comparator<EquipoPosicionado>() {
        @Override
        public int compare(EquipoPosicionado eq1, EquipoPosicionado eq2) {
            return Float.compare(eq1.getGolesEnContra(), eq2.getGolesEnContra());
        }
    };

    public static Comparator<EquipoPosicionado> comparadorDiferenciaGol = new Comparator<EquipoPosicionado>() {
        @Override
        public int compare(EquipoPosicionado eq1, EquipoPosicionado eq2) {
            return Float.compare(eq1.getDiferencia(), eq2.getDiferencia());
        }
    };

    public static Comparator<EquipoPosicionado> comparadorPartidosGanados = new Comparator<EquipoPosicionado>() {
        @Override
        public int compare(EquipoPosicionado eq1, EquipoPosicionado eq2) {
            return Float.compare(eq1.getPartidosGanados(), eq2.getPartidosGanados());
        }
    };

    public static Comparator<EquipoPosicionado> comparadorPartidosEmpatados = new Comparator<EquipoPosicionado>() {
        @Override
        public int compare(EquipoPosicionado eq1, EquipoPosicionado eq2) {
            return Float.compare(eq1.getPartidosEmpatados(), eq2.getPartidosEmpatados());
        }
    };

    public static Comparator<EquipoPosicionado> comparadorPartidosPerdidos = new Comparator<EquipoPosicionado>() {
        @Override
        public int compare(EquipoPosicionado eq1, EquipoPosicionado eq2) {
            return Float.compare(eq1.getPartidosPerdidos(), eq2.getPartidosPerdidos());
        }
    };
}