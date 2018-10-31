package app.hablemos.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import app.hablemos.model.football.Equipo;
import app.hablemos.model.football.EquipoPosicionado;
import app.hablemos.model.football.Partido;
import app.hablemos.model.football.PartidoActual;

public class AsyncUtil {

    public static DatosEquipo getDatosAsync(String pagina) {
        DatosEquipo datos = new DatosEquipo();
        try {
            datos = new GetDatosAsyncTask(pagina).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datos;
    }

    public static List<Partido> getPartidosAsync(String pagina) {
        List<Partido> partidos = new ArrayList<>();
        try {
            partidos = new GetPartidosAsyncTask(pagina).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partidos;
    }

    public static List<PartidoActual> getPartidosActuales() {
        List<PartidoActual> partidosActuales = new ArrayList<>();
        try {
            partidosActuales = new GetPartidosActualesAsyncTask().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partidosActuales;
    }

    public static List<PartidoActual> getPartidosPasados(String fecha) {
        List<PartidoActual> partidosActuales = new ArrayList<>();
        try {
            partidosActuales = new GetResultadoUltimoPartidoAsyncTask(fecha).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partidosActuales;
    }

    public static List<PartidoActual> getPartidosProximaFecha(String fecha) {
        List<PartidoActual> partidosActuales = new ArrayList<>();
        try {
            partidosActuales = new GetPartidosProximaFechaAsyncTask(fecha).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partidosActuales;
    }

    public static List<String> getEfemerides() {
        List<String> cumples = new ArrayList<>();
        try {
            cumples = new GetEfemeridesAsyncTask().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cumples;
    }

    public static List<String> getPlantel(String pagina) {
        List<String> jugadores = new ArrayList<>();
        try {
            jugadores = new GetPlantelAsyncTask(pagina).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jugadores;
    }

    public static TreeMap<Integer,List<String>> getGoleadores() {
        TreeMap<Integer, List<String>> goleadores = new TreeMap<>();
        try {
            goleadores = new GetGoleadoresAsyncTask().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goleadores;
    }

    public static List<EquipoPosicionado> llenarEquiposPosicionados() {
        List<EquipoPosicionado> equiposPosicionados = new ArrayList<>();
        try {
            equiposPosicionados = new GetPosicionesAsyncTask().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return equiposPosicionados;
    }
}