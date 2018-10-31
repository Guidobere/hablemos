package app.hablemos.model.football;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import app.hablemos.asynctasks.GetDatosAsyncTask;
import app.hablemos.asynctasks.GetEfemeridesAsyncTask;
import app.hablemos.asynctasks.GetGoleadoresAsyncTask;
import app.hablemos.asynctasks.GetPartidosActualesAsyncTask;
import app.hablemos.asynctasks.GetPartidosAsyncTask;
import app.hablemos.asynctasks.GetPartidosProximaFechaAsyncTask;
import app.hablemos.asynctasks.GetPlantelAsyncTask;
import app.hablemos.asynctasks.GetPosicionesAsyncTask;
import app.hablemos.asynctasks.GetResultadoUltimoPartidoAsyncTask;
import app.hablemos.model.football.DatosEquipo;
import app.hablemos.model.football.EquipoPosicionado;
import app.hablemos.model.football.Partido;
import app.hablemos.model.football.PartidoActual;

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