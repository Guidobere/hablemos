package app.hablemos.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import app.hablemos.model.football.ConversionMaps;
import app.hablemos.model.football.Equipo;
import app.hablemos.model.football.EquipoPosicionado;

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
        if(reemplazar){
            nombre = nombre.replace("(", "").replace(")", "");
        }
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
        List<EquipoPosicionado> equiposPosicionados = AsyncUtil.obtenerEquiposPosicionados();
        List<EquipoPosicionado> equipos = new ArrayList<>(equiposPosicionados);
        Collections.sort(equipos, comparador);
        Collections.reverse(equipos);
        return equipos;
    }

    public static String generarRespuesta(List<Equipo> equiposDePrimera, String mensajeInicial, List<EquipoPosicionado> equiposFiltrados, int varComparacion, String inicioUnico, boolean deTantos) {
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
                if (deTantos) {
                    respuesta.append(" con ").append(varComparacion).append(" sobre ").append(eq.getPartidosJugados()).append(" jugados");
                } else {
                    respuesta.append(" con ").append(varComparacion);
                }
                contador++;
            }
            return respuesta.append(".").toString();
        } else {
            EquipoPosicionado equipoPosicionado = equiposFiltrados.get(0);
            String nombre = getNombreRealFromTabla(equiposDePrimera, equipoPosicionado.getNombre(), false);
            if (deTantos) {
                return inicioUnico + nombre + " con " + varComparacion + " sobre " + equipoPosicionado.getPartidosJugados() + " jugados.";
            } else {
                return inicioUnico + nombre + " con " + varComparacion + ".";
            }
        }
    }

    private static List<String> getJugadoresPrimera(List<Equipo> equiposDePrimera, String equipo) {
        List<String> jugadores = new ArrayList<>();
        if (equiposDePrimera != null && equiposDePrimera.size() != 0) {
            String nombreEquipo = getNombreRealFromTabla(equiposDePrimera, equipo, true);
            for (Equipo eq : equiposDePrimera) {
                if (eq.getNombre().equalsIgnoreCase(nombreEquipo)) {
                    jugadores.addAll(AsyncUtil.getPlantel(eq.getPagina()));
                }
            }
        }
        return jugadores;
    }

    public static String getGoleadoresString(List<Equipo> equiposDePrimera, List<String> goleadores) {
        String separador = "";
        StringBuilder respuesta = new StringBuilder();
        for (int i = 0; i < goleadores.size(); i++) {
            if (i == (goleadores.size() - 1)) {
                respuesta.append(" y ");
            } else {
                respuesta.append(separador);
                separador = ", ";
            }
            String nombreJugador = goleadores.get(i).split("\\(")[0].trim();
            String nombreEquipo = goleadores.get(i).split(nombreJugador)[1].trim();
            String nombreEquipoReplaced = nombreEquipo.replace("(", "").replace(")", "");
            nombreJugador = FootballUtil.getNombreRealJugador(equiposDePrimera, nombreEquipoReplaced, nombreJugador);
            String marcador = nombreJugador + " " + nombreEquipo;
            respuesta.append(marcador);
        }
        return respuesta.append(".").toString();
    }

    public static String getNombreRealJugador(List<Equipo> equiposDePrimera, String equipo, String nombre) {
        List<String> jugadores = getJugadoresPrimera(equiposDePrimera, equipo);
        List<String> jugadoresFiltrados = new ArrayList<>();
        for (int i = 0; i < jugadores.size(); i++){
            String[] splitted = nombre.split(" ");
            StringBuilder nombrefinal = new StringBuilder();
            String separador = "";
            for (int j = 1; j < splitted.length; j++){
                nombrefinal.append(separador).append(splitted[j]);
                separador = " ";
            }
            String nom = StringUtils.replaceSpecialChar(jugadores.get(i));
            if (nom.contains(StringUtils.replaceSpecialChar(nombrefinal.toString()))){
                String nomComparar = splitted[0].substring(0, 1);
                if (nom.startsWith(nomComparar)){
                    jugadoresFiltrados.add(jugadores.get(i));
                }
            }
        }
        if (jugadoresFiltrados.size() == 1) {
            return jugadoresFiltrados.get(0);
        } else {
            return nombre;
        }
    }

    public static String obtenerStringGolUnico(List<Equipo> equiposDePrimera, String equipo, String marcador, String goles, String rival) {
        String nombre = goles.split("'")[1].replace(";", "").replace(".", "").trim();
        String nombreJugador;
        if (nombre.contains("(")) {
            nombreJugador = nombre.split("\\(")[0].trim();
            String nombreRealJugador;
            if (goles.contains("(e.c.)") || goles.contains("(ec)")) {
                nombreRealJugador = getNombreRealJugador(equiposDePrimera, rival, nombreJugador.replace(".", ""));
            } else {
                nombreRealJugador = getNombreRealJugador(equiposDePrimera, equipo, nombreJugador.replace(".", ""));
            }
            String[] splitted = nombre.split(nombreJugador);
            nombreJugador = nombreRealJugador + " " + splitted[1].trim().replace("e.c.", "en contra").replace("(ec)", "(en contra)").replace("(pen.)", "(de penal)").replace("(pen)", "(de penal)").replace(".", "");
        } else {
            nombreJugador = getNombreRealJugador(equiposDePrimera, equipo, nombre.replace(".", ""));
        }
        StringBuilder marcadores = new StringBuilder("\nEl gol " + marcador + " lo marcó " + nombreJugador);
        String tiempo = goles.split("'")[0];
        if (tiempo.startsWith("45(+")) {
            String minutosAdicionales = tiempo.split("\\+")[1].replace(")", "").trim();
            if (minutosAdicionales.equalsIgnoreCase("1")) {
                marcadores.append(" al minuto de adicional del primer tiempo");
            } else {
                marcadores.append(" a los ").append(tiempo.split("\\+")[1].replace(")", "").trim()).append(" minutos de adicional del primer tiempo");
            }
        } else if (tiempo.startsWith("90(+")) {
            String minutosAdicionales = tiempo.split("\\+")[1].replace(")", "").trim();
            if (minutosAdicionales.equalsIgnoreCase("1")) {
                marcadores.append(" al minuto de adicional del segundo tiempo");
            } else {
                marcadores.append(" a los ").append(minutosAdicionales).append(" minutos de adicional del segundo tiempo");
            }
        } else {
            marcadores.append(" a los ").append(tiempo).append(" minutos.");
        }
        return marcadores.toString();
    }

    public static String obtenerMarcadores(List<Equipo> equiposDePrimera, String equipo, String goles, String rival) {
        String separador = "";
        StringBuilder marcadores = new StringBuilder();
        String[] marcadoresArray = goles.split(";");
        int contador = 1;
        for (String str : marcadoresArray) {
            String[] submarcadoresArray = str.split("'");
            String tiempo = submarcadoresArray[0].trim();
            String marcador;
            String nombreJugador;
            if (submarcadoresArray[1].trim().contains("(")) {
                nombreJugador = submarcadoresArray[1].trim().split("\\(")[0].trim();
                String nombreRealJugador;
                if (submarcadoresArray[1].trim().contains("(e.c.)") || submarcadoresArray[1].trim().contains("(ec)")) {
                    nombreRealJugador = getNombreRealJugador(equiposDePrimera, rival, nombreJugador.replace(".", ""));
                } else {
                    nombreRealJugador = getNombreRealJugador(equiposDePrimera, equipo, nombreJugador.replace(".", ""));
                }
                String[] splitted = submarcadoresArray[1].trim().split(nombreJugador);
                marcador = nombreRealJugador + " " + splitted[1].trim().replace("e.c.", "en contra").replace("(ec)", "(en contra)").replace("(pen.)", "(de penal)").replace("(pen)", "(de penal)").replace(".", "");
            } else {
                nombreJugador = getNombreRealJugador(equiposDePrimera, equipo, submarcadoresArray[1].trim().replace(".", ""));
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