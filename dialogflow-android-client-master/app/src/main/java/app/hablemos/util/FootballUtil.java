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
import app.hablemos.model.football.ConversionMaps;
import app.hablemos.model.football.DatosEquipo;
import app.hablemos.model.football.Equipo;
import app.hablemos.model.football.EquipoPosicionado;
import app.hablemos.model.football.Partido;
import app.hablemos.model.football.PartidoActual;

public class FootballUtil {

    public static String convertirSafReferencia(String abreviacion) {
        return ConversionMaps.getMapaEquipos().get(ConversionMaps.getReferenciaEquipo(abreviacion));
    }

    public static EquipoPosicionado obtenerEquipoPosicionado(List<EquipoPosicionado> equiposPosicionados, String equipo) {
        EquipoPosicionado equipoPos = null;
        for(EquipoPosicionado ep : equiposPosicionados) {
            if (ep.getNombre().equalsIgnoreCase(ConversionMaps.getMapaEquipos().get(equipo))){
                equipoPos = ep;
            }
        }
        return equipoPos;
    }

    public static String getNombreRealFromTabla(List<Equipo> equiposDePrimera, String nombre, boolean reemplazar) {
        HashMap<String, String> mapaEquipos = ConversionMaps.getMapaEquipos();
        String nombreReal = "";
        for (String key : mapaEquipos.keySet()) {
            String nombreDeMapa = mapaEquipos.get(key);
            if (reemplazar) {
                nombreDeMapa = nombreDeMapa.replace("(", "").replace(")", "");
            }
            if (nombreDeMapa.equalsIgnoreCase(nombre)) {
                nombreReal = obtenerEquipoVisual(equiposDePrimera, key).getNombre();
            }
        }
        return nombreReal;
    }

    public static String armarStringConLista(List<Equipo> equiposDePrimera, String mensajeInicial, List<EquipoPosicionado> lista, String bullet) {
        StringBuilder respuesta = new StringBuilder(mensajeInicial);
        for (EquipoPosicionado ep : lista) {
            respuesta.append("\n").append(bullet).append(" ").append(getNombreRealFromTabla(equiposDePrimera, ep.getNombre(), false));
        }
        return respuesta.toString();
    }

    public static String getStringTablaPosiciones(List<Equipo> equiposDePrimera, List<EquipoPosicionado> lista, String mensajeInicial) {
        StringBuilder respuesta = new StringBuilder(mensajeInicial);
        for (EquipoPosicionado ep : lista) {
            respuesta.append("\n").append(ep.getPosicion()).append("º ").append(getNombreRealFromTabla(equiposDePrimera, ep.getNombre(), false));
        }
        return respuesta.toString();
    }

    public static List<EquipoPosicionado> getEquiposFiltrados(Comparator<EquipoPosicionado> comparador) {
        List<EquipoPosicionado> equiposPosicionados = AsyncUtil.llenarEquiposPosicionados();
        List<EquipoPosicionado> equipos = new ArrayList<>(equiposPosicionados);
        Collections.sort(equipos, comparador);
        Collections.reverse(equipos);
        return equipos;
    }

    public static String generarRespuesta(List<Equipo> equiposDePrimera, String mensajeInicial, List<EquipoPosicionado> equiposFiltrados, int varComparacion, String inicioUnico) {
        if (equiposFiltrados.size() > 1) {
            StringBuilder respuesta = new StringBuilder(mensajeInicial);
            String separador = "";
            int contador = 1;
            for (EquipoPosicionado eq : equiposFiltrados) {
                if (contador == equiposFiltrados.size()) {
                    respuesta.append(" y ");
                } else {
                    respuesta.append(separador);
                    separador = ", ";
                }
                respuesta.append(getNombreRealFromTabla(equiposDePrimera, eq.getNombre(), false));
                contador++;
            }
            respuesta.append(" con ").append(varComparacion).append(".");
            return respuesta.toString();
        } else {
            EquipoPosicionado equipoPosicionado = equiposFiltrados.get(0);
            String nombre = getNombreRealFromTabla(equiposDePrimera, equipoPosicionado.getNombre(), false);
            return inicioUnico + nombre + " con " + varComparacion + ".";
        }
    }

    public static HashMap<String, List<String>> getJugadoresPrimera(List<Equipo> equiposDePrimera) {
        HashMap<String, List<String>> jugadoresPorEquipo = new HashMap<>();
        if (equiposDePrimera != null && equiposDePrimera.size() != 0) {
            for (Equipo equipo : equiposDePrimera) {
                jugadoresPorEquipo.put(equipo.getNombre(), AsyncUtil.getPlantel(equipo.getPagina()));
            }
        }
        return jugadoresPorEquipo;
    }

    public static String getGoleadoresString(List<String> goleadores) {
        String separador = "";
        StringBuilder respuesta = new StringBuilder();
        for (int i = 0; i < goleadores.size(); i++) {
            if (i == (goleadores.size() - 1)) {
                respuesta.append(" y ");
            } else {
                respuesta.append(separador);
                separador = ", ";
            }
            respuesta.append(goleadores.get(i).replace(". ", " ").replace(".", " "));
        }
        return respuesta.toString();
    }

    private static String getNombreRealJugador(List<Equipo> equiposDePrimera, HashMap<String, List<String>> jugadoresPrimera, String equipo, String nombre1) {
        String nombreRealEquipo = getNombreRealFromTabla(equiposDePrimera, equipo, true);
        List<String> jugadoresFiltrados = new ArrayList<>();
        for (int i = 0; i < jugadoresPrimera.get(nombreRealEquipo).size()-1; i++){
            String[] splitted = nombre1.split(" ");
            StringBuilder nombrefinal = new StringBuilder();
            String separador = "";
            for (int j = 1; j < splitted.length; j++){
                nombrefinal.append(separador).append(splitted[j]);
                separador = " ";
            }
            String nom = StringUtils.replaceSpecialChar(jugadoresPrimera.get(nombreRealEquipo).get(i));
            if (nom.contains(StringUtils.replaceSpecialChar(nombrefinal.toString()))){
                String nomComparar = splitted[0].substring(0, 1);
                if (nom.startsWith(nomComparar)){
                    jugadoresFiltrados.add(jugadoresPrimera.get(nombreRealEquipo).get(i));
                }
            }
        }
        if (jugadoresFiltrados.size() == 1) {
            return jugadoresFiltrados.get(0);
        } else {
            return nombre1;
        }
    }

    public static String obtenerStringGolUnico(List<Equipo> equiposDePrimera, HashMap<String, List<String>> jugadoresPrimera, String equipo, String marcador, String goles) {
        return "\nEl gol " + marcador + " lo marcó " +
                getNombreRealJugador(equiposDePrimera, jugadoresPrimera, equipo, goles.split("'")[1].replace(";", "").replace(".", "").trim()) +
                " a los " + goles.split("'")[0] + " minutos.";
    }

    public static String obtenerMarcadores(List<Equipo> equiposDePrimera, HashMap<String, List<String>> jugadoresPrimera, String equipo, String goles) {
        String separador = "";
        StringBuilder marcadores = new StringBuilder();
        String[] marcadoresArray = goles.split(";");
        int contador = 1;
        for (String str : marcadoresArray) {
            String[] submarcadoresArray = str.split("'");
            String tiempo = submarcadoresArray[0].trim();
            String marcador = "";
            String nombreJugador = "";
            if (submarcadoresArray[1].trim().contains("(")) {
                nombreJugador = submarcadoresArray[1].trim().split("\\(")[0].trim();
                String nombreRealJugador = getNombreRealJugador(equiposDePrimera, jugadoresPrimera, equipo, nombreJugador.replace(".", ""));
                String[] splitted = submarcadoresArray[1].trim().split(nombreJugador);
                marcador = nombreRealJugador + " " + splitted[1].trim().replace("e.c.", "en contra").replace("(pen.)", "(de penal)").replace(".", "");
            } else {
                nombreJugador = getNombreRealJugador(equiposDePrimera, jugadoresPrimera, equipo, submarcadoresArray[1].trim().replace(".", ""));
                marcador = nombreJugador;
            }
            if (contador == marcadoresArray.length) {
                marcadores.append(" y ");
            } else {
                marcadores.append(separador);
                separador = ", ";
            }
            if (tiempo.startsWith("45(+")) {
                String minutosAdicionales = tiempo.split("\\+")[1].replace(")", "").trim();
                if (minutosAdicionales.equalsIgnoreCase("1")) {
                    marcadores.append(marcador).append(" al minuto de adicional del primer tiempo");
                } else {
                    marcadores.append(marcador).append(" a los ").append(tiempo.split("\\+")[1].replace(")", "").trim()).append(" minutos de adicional del primer tiempo");
                }
            } else if (tiempo.startsWith("90(+")) {
                String minutosAdicionales = tiempo.split("\\+")[1].replace(")", "").trim();
                if (minutosAdicionales.equalsIgnoreCase("1")) {
                    marcadores.append(marcador).append(" al minuto de adicional del segundo tiempo");
                } else {
                    marcadores.append(marcador).append(" a los ").append(minutosAdicionales).append(" minutos de adicional del segundo tiempo");
                }
            } else {
                marcadores.append(marcador).append(" a los ").append(tiempo).append(" minutos");
            }
            contador++;
        }
        return marcadores.append(".").toString();
    }

    public static Equipo obtenerEquipoVisual(List<Equipo> equiposDePrimera, String equipo) {
        Equipo equipoVisual = null;
        for(Equipo eq : equiposDePrimera) {
            if (eq.getNombreReferencia().equalsIgnoreCase(equipo)) {
                equipoVisual = eq;
            }
        }
        return equipoVisual;
    }
}