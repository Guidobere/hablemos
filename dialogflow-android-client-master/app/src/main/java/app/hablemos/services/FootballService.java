package app.hablemos.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import app.hablemos.asynctasks.GetComparacionAsyncTask;
import app.hablemos.asynctasks.GetEquiposAsyncTask;
import app.hablemos.asynctasks.GetPosicionesAsyncTask;
import app.hablemos.model.football.DatosEquipo;
import app.hablemos.model.football.Equipo;
import app.hablemos.model.football.EquipoPosicionado;
import app.hablemos.model.football.Partido;
import app.hablemos.model.football.PartidoActual;
import app.hablemos.util.DateUtils;
import app.hablemos.util.FootballUtil;

public class FootballService {

    private List<Equipo> equiposDePrimera;
    private List<EquipoPosicionado> equiposPosicionados;
    private HashMap<String, String> mapaEquipos;

    public FootballService() {
        this.equiposPosicionados = new ArrayList<>();
        this.equiposDePrimera = new ArrayList<>();
        this.mapaEquipos = FootballUtil.getMapaEquipos();
        try {
            this.equiposDePrimera = new GetEquiposAsyncTask().execute().get();
            HashMap<String, String> mapaConversion = FootballUtil.getMapaConversionVisual();
            for(Equipo equipo : this.equiposDePrimera) {
                if (mapaConversion.keySet().contains(equipo.getNombre())){
                    equipo.setNombre(mapaConversion.get(equipo.getNombre()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* SERVICIOS EXPUESTOS */
    public String getTablaPosiciones() {
        llenarEquiposPosicionados();
        List<EquipoPosicionado> topN = equiposPosicionados.subList(0,this.equiposPosicionados.size());
        return getStringTablaPosiciones(topN, "La tabla de posiciones se encuentra de esta manera: ");
    }

    public String getTopNEquipos(int n) {
        llenarEquiposPosicionados();
        if (n < 1 || n > this.equiposPosicionados.size()) {
            return "La cantidad deseada no es correcta, hay " + this.equiposPosicionados.size() + " equipos actualmente.";
        }
        List<EquipoPosicionado> topN = equiposPosicionados.subList(0,n);
        if(n == 1) {
            return "El equipo que está primero en la tabla es: " + getNombreRealFromTabla(topN.get(0).getNombre());
        }
        return getStringTablaPosiciones(topN, "Los equipos que están entre los mejores " + n + " son: ");
    }

    public String getBottomNEquipos(int n) {
        llenarEquiposPosicionados();
        if (n < 1 || n > this.equiposPosicionados.size()) {
            return "La cantidad deseada no es correcta, hay " + this.equiposPosicionados.size() + " equipos actualmente.";
        }
        List<EquipoPosicionado> bottomN = equiposPosicionados.subList(this.equiposPosicionados.size() - n, this.equiposPosicionados.size());
        if(n == 1) {
            return "El equipo que está último en la tabla es: " + getNombreRealFromTabla(bottomN.get(0).getNombre());
        }
        return getStringTablaPosiciones(bottomN, "Los últimos " + n + " equipos de la tabla son: ");
    }

    public String getPosicionEquipo(String equipo) {
        llenarEquiposPosicionados();
        Equipo equipoVisual = obtenerEquipoVisual(equipo);
        int posicion = obtenerEquipoPosicionado(equipo).getPosicion();
        if (posicion != 0)
            return equipoVisual.getNombre() + " está en la posición " + posicion + ".";
        else
            return "El equipo solicitado no pudo ser encontrado.";
    }

    public String getEquipoEnPosicion(int posicion) {
        llenarEquiposPosicionados();
        if (posicion < 1 || posicion > this.equiposPosicionados.size()) {
            return "La posicion deseada no es correcta, hay " + this.equiposPosicionados.size() + " equipos actualmente.";
        }
        String equipo = "";
        for(EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.getPosicion() == posicion){
                equipo = ep.getNombre();
            }
        }
        return "El equipo que está en la posición " + posicion + " es " + obtenerEquipoVisual(equipo).getNombre() + ".";
    }

    public String getDatosEquipo(String equipo) {
        Equipo equipoVisual = obtenerEquipoVisual(equipo);
        if (equipoVisual != null) {
            DatosEquipo datos = FootballUtil.getDatosAsync(equipoVisual.getPagina());
            return equipoVisual.getNombre() + datos.toString();
        } else
            return "El equipo solicitado no pudo ser encontrado.";
    }

    public String getEstadisticasEquipo(String equipo) {
        llenarEquiposPosicionados();
        String equipoVisual = obtenerEquipoVisual(equipo).getNombre();
        EquipoPosicionado equipoPosicionado = obtenerEquipoPosicionado(equipo);
        if (equipoPosicionado != null) return equipoVisual + equipoPosicionado.toString();
        else return "No pudieron encontrarse estadísticas para " + equipoVisual + ".";
    }

    public String getComparacionEquipos(String equipo1, String equipo2) {
        String equipoVisual1 = obtenerEquipoVisual(equipo1).getNombre();
        String equipoVisual2 = obtenerEquipoVisual(equipo2).getNombre();
        String comparacion;
        try {
            comparacion = new GetComparacionAsyncTask(mapaEquipos.get(equipo1), mapaEquipos.get(equipo2)).execute().get();
        } catch (Exception e) {
            return "Al menos uno de los equipos ingresados no es correcto.";
        }
        if (!comparacion.equals("")) return FootballUtil.modificarNombresEquiposPrimera(comparacion);
        else return "La comparación entre " + equipoVisual1 + " y " + equipoVisual2 + " no pudo ser realizada.";
    }

    public String getProximoPartido(String equipo) {
        Equipo equipoVisual = obtenerEquipoVisual(equipo);
        if (equipoVisual != null) {
            List<Partido> partidos = FootballUtil.getPartidosAsync(equipoVisual.getPagina());
            List<Partido> partidosFiltrados = new ArrayList<>();
            for (Partido partido : partidos) {
                if (partido.getResultado().equals("-")) {
                    partidosFiltrados.add(partido);
                }
            }
            Collections.sort(partidosFiltrados, FootballUtil.comparadorDeFecha);
            StringBuilder retorno = new StringBuilder();
            int posEnLista = 0;
            if (partidosFiltrados.get(0).getDia().contains("Post")) {
                retorno.append(equipoVisual.getNombre()).append(" tiene un partido postergado con ").append(getNombreRealFromTabla(partidosFiltrados.get(0).getRival())).append(" sin fecha asignada, en el siguiente encuentro").append(partidosFiltrados.get(1).toString(getNombreRealFromTabla(partidosFiltrados.get(1).getRival())));
                posEnLista = 1;
            } else {
                retorno.append(equipoVisual.getNombre()).append(partidosFiltrados.get(0).toString(getNombreRealFromTabla(partidosFiltrados.get(0).getRival())));
            }
            if (partidosFiltrados.get(posEnLista).getDia().equals(DateUtils.getNowString())) {
                List<PartidoActual> partidosActuales = FootballUtil.getPartidosActuales();
                for(PartidoActual partidoActual : partidosActuales) {
                    if (partidoActual.getEquipoLocal().equalsIgnoreCase(mapaEquipos.get(equipo)) ||
                            partidoActual.getEquipoVisitante().equalsIgnoreCase(mapaEquipos.get(equipo))) {
                        retorno.append(" a las ").append(partidoActual.getHoraJuego());
                    }
                }
            }
            return retorno.toString();
        } else
            return "El equipo solicitado no pudo ser encontrado.";
    }

    public String getUltimoPartido(String equipo) {
        Equipo equipoVisual = obtenerEquipoVisual(equipo);
        if (equipoVisual != null) {
            List<Partido> partidos = FootballUtil.getPartidosAsync(equipoVisual.getPagina());
            List<Partido> partidosFiltrados = new ArrayList<>();
            for (Partido partido : partidos) {
                if (!partido.getResultado().equals("-")) {
                    partidosFiltrados.add(partido);
                }
            }
            Collections.sort(partidosFiltrados, FootballUtil.comparadorDeFecha);
            StringBuilder retorno = new StringBuilder(equipoVisual.getNombre());
            List<PartidoActual> partidosActuales;
            if (partidosFiltrados.get(partidosFiltrados.size()-1).getDia().equals(DateUtils.getNowString())) {
                partidosActuales = FootballUtil.getPartidosActuales();
            } else {
                partidosActuales = FootballUtil.getPartidosPasados(partidosFiltrados.get(partidosFiltrados.size()-1).getFecha());
            }
            for(PartidoActual partidoActual : partidosActuales) {
                if (partidoActual.getEquipoLocal().equalsIgnoreCase(mapaEquipos.get(equipo)) ||
                        partidoActual.getEquipoVisitante().equalsIgnoreCase(mapaEquipos.get(equipo))) {
                    if(partidoActual.getEstado().equalsIgnoreCase("jugandose")) {
                        retorno.append(partidosFiltrados.get(partidosFiltrados.size() - 1).toStringEnCurso(getNombreRealFromTabla(partidosFiltrados.get(partidosFiltrados.size() - 1).getRival())));
                        if (partidoActual.getTiempoJuego().equalsIgnoreCase("e.t.") || partidoActual.getTiempoJuego().equalsIgnoreCase("e. t.")) {
                            retorno.append(". En este momento están en el entretiempo.");
                        } else {
                            retorno.append(" a los ").append(Integer.parseInt(partidoActual.getTiempoJuego().replace("'", ""))).append(" minutos.");
                        }
                    } else if(partidoActual.getEstado().equalsIgnoreCase("finaliza")) {
                        retorno.append(partidosFiltrados.get(partidosFiltrados.size() - 1).toStringUltimo(getNombreRealFromTabla(partidosFiltrados.get(partidosFiltrados.size() - 1).getRival())));
                    }
                    if (partidoActual.getGolesEquipoLocal()==1){
                        retorno.append(FootballUtil.obtenerStringGolUnico("del local", partidoActual.getGolesLocal()));
                    } else if (partidoActual.getGolesEquipoLocal()>1){
                        retorno.append("\nLos goles del equipo local fueron marcados por ").append(FootballUtil.obtenerMarcadores(partidoActual.getGolesLocal()));
                    }
                    if (partidoActual.getGolesEquipoVisitante()==1){
                        retorno.append(FootballUtil.obtenerStringGolUnico("de la visita", partidoActual.getGolesVisitante()));
                    } else if (partidoActual.getGolesEquipoVisitante()>1){
                        retorno.append("\nLos goles de la visita fueron marcados por ").append(FootballUtil.obtenerMarcadores(partidoActual.getGolesVisitante()));
                    }
                }
            }
            return retorno.toString();
        } else
            return "El equipo solicitado no pudo ser encontrado";
    }

    public String getLibertadores() {
        llenarEquiposPosicionados();
        List<EquipoPosicionado> libertadores = new ArrayList<>();
        for (EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.isLibertadores()) {
                libertadores.add(ep);
            }
        }
        return armarStringConLista("Los equipos que están en zona de Copa Libertadores son: ", libertadores);
    }

    public String getSudamericana() {
        llenarEquiposPosicionados();
        List<EquipoPosicionado> sudamericana = new ArrayList<>();
        for (EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.isSudamericana()) {
                sudamericana.add(ep);
            }
        }
        return armarStringConLista("Los equipos que están en zona de Copa Sudamericana son: ", sudamericana);
    }

    public String getDescienden() {
        llenarEquiposPosicionados();
        List<EquipoPosicionado> descienden = new ArrayList<>();
        for (EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.isDesciende()) {
                descienden.add(ep);
            }
        }
        Collections.sort(descienden, FootballUtil.comparadorDePromedios);
        Collections.reverse(descienden);
        return armarStringConLista("Los equipos que están en zona de descenso son: ", descienden);
    }

    public String getEfemerides() {
        List<String> efemerides = FootballUtil.getEfemerides();
        StringBuilder respuesta = new StringBuilder("Estas son las efemérides de hoy: ");
        String separador = "";
        int contador = 1;
        if (efemerides.size() == 1) {
            respuesta.append(efemerides.get(0));
        } else {
            for (String efemeride : efemerides) {
                if (contador == efemerides.size()) {
                    respuesta.append(" y ");
                } else {
                    respuesta.append(separador);
                    separador = "; ";
                }
                respuesta.append(efemeride);
                contador++;
            }
        }
        respuesta.append(".");
        return FootballUtil.modificarNombresEquiposPrimera(respuesta.toString()).replace(")", ",").replace(" (", ", de ");
    }

    public String getGoleador() {
        TreeMap<Integer, List<String>> goleadores = FootballUtil.getGoleadores();
        StringBuilder respuesta = new StringBuilder();
        int contador = 1;
        for (Integer cantGoles : goleadores.keySet()) {
            if (contador == 1) {
                if (goleadores.get(cantGoles).size() > 1) {
                    respuesta.append("Los goleadores del torneo, con ").append(cantGoles).append(" goles son ");
                    respuesta.append(FootballUtil.getGoleadores(goleadores.get(cantGoles)));
                } else {
                    respuesta.append("El goleador del torneo, con ").append(cantGoles).append(" goles es ").append(goleadores.get(cantGoles).get(0).replace(". ", " ").replace(".", " "));
                }
            } else if (contador == 2) {
                if (goleadores.get(cantGoles).size() > 1) {
                    respuesta.append("\nLe siguen, con ").append(cantGoles).append(" goles ");
                    respuesta.append(FootballUtil.getGoleadores(goleadores.get(cantGoles)));
                } else {
                    respuesta.append("\nLe sigue, con ").append(cantGoles).append(" goles es ").append(goleadores.get(cantGoles).get(0).replace(". ", " ").replace(".", " "));
                }
            } else if (contador == 3){
                respuesta.append("\nEn tercer lugar, con ").append(cantGoles);
                if (goleadores.get(cantGoles).size() > 1) {
                    respuesta.append(" goles, están ");
                    respuesta.append(FootballUtil.getGoleadores(goleadores.get(cantGoles)));
                } else {
                    respuesta.append(" goles está ").append(goleadores.get(cantGoles).get(0).replace(". ", " ").replace(".", " "));
                }
            }
            contador++;
        }
        respuesta.append(".");
        return FootballUtil.modificarNombresEquiposPrimera(respuesta.toString()).replace(")", "").replace(" (", ", de ");
    }

    public String getEquipoMasGoleador() {
        List<EquipoPosicionado> equiposPosicionados = getEquiposFiltrados(FootballUtil.comparadorGolesAFavor);
        int goles = equiposPosicionados.get(0).getGolesAFavor();
        List<EquipoPosicionado> equiposFiltrados = new ArrayList<>();
        for (EquipoPosicionado equipo : equiposPosicionados) {
            if (equipo.getGolesAFavor() == goles) {
                equiposFiltrados.add(equipo);
            }
        }
        return generarRespuesta("Los equipos que más goles hicieron son: ", equiposFiltrados, goles, "El equipo más goleador es ");
    }

    public String getEquipoMasGoleado() {
        List<EquipoPosicionado> equiposPosicionados = getEquiposFiltrados(FootballUtil.comparadorGolesEnContra);
        int goles = equiposPosicionados.get(0).getGolesEnContra();
        List<EquipoPosicionado> equiposFiltrados = new ArrayList<>();
        for (EquipoPosicionado equipo : equiposPosicionados) {
            if (equipo.getGolesEnContra() == goles) {
                equiposFiltrados.add(equipo);
            }
        }
        return generarRespuesta("Los equipos a los que más goles les hicieron son: ", equiposFiltrados, goles, "El equipo al que más goles le convirtieron es ");
    }

    public String getEquipoMayorDiferenciaDeGol() {
        List<EquipoPosicionado> equiposPosicionados = getEquiposFiltrados(FootballUtil.comparadorDiferenciaGol);
        int goles = equiposPosicionados.get(0).getDiferencia();
        List<EquipoPosicionado> equiposFiltrados = new ArrayList<>();
        for (EquipoPosicionado equipo : equiposPosicionados) {
            if (equipo.getDiferencia() == goles) {
                equiposFiltrados.add(equipo);
            }
        }
        return generarRespuesta("Los equipos con más diferencia de gol son ", equiposFiltrados, goles, "El equipo con más diferencia de gol es ");
    }

    public String getEquipoMasPartidosGanados() {
        List<EquipoPosicionado> equiposPosicionados = getEquiposFiltrados(FootballUtil.comparadorPartidosGanados);
        int partidosGanados = equiposPosicionados.get(0).getPartidosGanados();
        List<EquipoPosicionado> equiposFiltrados = new ArrayList<>();
        for (EquipoPosicionado equipo : equiposPosicionados) {
            if (equipo.getPartidosGanados() == partidosGanados) {
                equiposFiltrados.add(equipo);
            }
        }
        return generarRespuesta("Los equipos que más partidos ganaron son ", equiposFiltrados, partidosGanados, "El equipo con más partidos ganados es ");
    }

    public String getEquipoMasPartidosEmpatados() {
        List<EquipoPosicionado> equiposPosicionados = getEquiposFiltrados(FootballUtil.comparadorPartidosEmpatados);
        int partidosEmpatados = equiposPosicionados.get(0).getPartidosEmpatados();
        List<EquipoPosicionado> equiposFiltrados = new ArrayList<>();
        for (EquipoPosicionado equipo : equiposPosicionados) {
            if (equipo.getPartidosEmpatados() == partidosEmpatados) {
                equiposFiltrados.add(equipo);
            }
        }
        return generarRespuesta("Los equipos que más partidos empataron son ", equiposFiltrados, partidosEmpatados, "El equipo que empató mayor cantidad de partidos es ");
    }

    public String getEquipoMasPartidosPerdidos() {
        List<EquipoPosicionado> equiposPosicionados = getEquiposFiltrados(FootballUtil.comparadorPartidosPerdidos);
        int partidosPerdidos = equiposPosicionados.get(0).getPartidosPerdidos();
        List<EquipoPosicionado> equiposFiltrados = new ArrayList<>();
        for (EquipoPosicionado equipo : equiposPosicionados) {
            if (equipo.getPartidosPerdidos() == partidosPerdidos) {
                equiposFiltrados.add(equipo);
            }
        }
        return generarRespuesta("Los equipos que más partidos perdieron son ", equiposFiltrados, partidosPerdidos, "El equipo que perdió mayor cantidad de partidos es ");
    }

    /* SPINNER EQUIPOS REGISTRO */
    public List<String> getEquiposDePrimera() {
        List<String> equipos = new ArrayList<>();
        for (Equipo equipo : this.equiposDePrimera) {
            equipos.add(equipo.getNombre());
        }
        return equipos;
    }

    public String getNombreReferencia(String nombreReal) {
        if (nombreReal.equalsIgnoreCase("Ninguno")) return "ninguno";
        String nombreReferencia = "";
        for(Equipo eq : this.equiposDePrimera) {
            if (eq.getNombre().equalsIgnoreCase(nombreReal)) {
                nombreReferencia = eq.getNombreReferencia();
            }
        }
        return nombreReferencia;
    }

    public Equipo obtenerEquipoVisual(String equipo) {
        Equipo equipoVisual = null;
        for(Equipo eq : this.equiposDePrimera) {
            if (eq.getNombreReferencia().equalsIgnoreCase(equipo)) {
                equipoVisual = eq;
            }
        }
        return equipoVisual;
    }

    /* SERVICIOS INTERNOS DE SCRAPPING */
    private void llenarEquiposPosicionados() {
        try {
            this.equiposPosicionados = new GetPosicionesAsyncTask().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* UTILS INTERNOS */
    private EquipoPosicionado obtenerEquipoPosicionado(String equipo) {
        EquipoPosicionado equipoPos = null;
        for(EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.getNombre().equalsIgnoreCase(mapaEquipos.get(equipo))){
                equipoPos = ep;
            }
        }
        return equipoPos;
    }

    private String getNombreRealFromTabla(String nombre) {
        HashMap<String, String> mapaEquipos = FootballUtil.getMapaEquipos();
        String nombreReal = "";
        for (String key : mapaEquipos.keySet()) {
            if (mapaEquipos.get(key).equalsIgnoreCase(nombre)) {
                nombreReal = obtenerEquipoVisual(key).getNombre();
            }
        }
        return nombreReal;
    }

    private String armarStringConLista(String mensajeInicial, List<EquipoPosicionado> lista) {
        StringBuilder respuesta = new StringBuilder(mensajeInicial);
        String separador = "";
        int contador = 1;
        for (EquipoPosicionado ep : lista) {
            if (contador == lista.size()) {
                respuesta.append(" y ");
            } else {
                respuesta.append(separador);
                separador = ", ";
            }
            respuesta.append(getNombreRealFromTabla(ep.getNombre()));
            contador++;
        }
        return respuesta.append(".").toString();
    }

    private String getStringTablaPosiciones(List<EquipoPosicionado> lista, String mensajeInicial) {
        StringBuilder respuesta = new StringBuilder(mensajeInicial);
        String separador = "";
        int contador = 1;
        for (EquipoPosicionado ep : lista) {
            if (contador == lista.size()) {
                respuesta.append(" y ");
            } else {
                respuesta.append(separador);
                separador = ", ";
            }
            respuesta.append(getNombreRealFromTabla(ep.getNombre()));
            contador++;
        }
        return respuesta.toString();
    }

    private List<EquipoPosicionado> getEquiposFiltrados(Comparator<EquipoPosicionado> comparador) {
        llenarEquiposPosicionados();
        List<EquipoPosicionado> equipos = new ArrayList<>(this.equiposPosicionados);
        Collections.sort(equipos, comparador);
        Collections.reverse(equipos);
        return equipos;
    }

    private String generarRespuesta(String mensajeInicial, List<EquipoPosicionado> equiposFiltrados, int varComparacion, String inicioUnico) {
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
                respuesta.append(getNombreRealFromTabla(eq.getNombre()));
                contador++;
            }
            respuesta.append(" con ").append(varComparacion).append(".");
            return respuesta.toString();
        } else {
            EquipoPosicionado equipoPosicionado = equiposFiltrados.get(0);
            String nombre = getNombreRealFromTabla(equipoPosicionado.getNombre());
            return inicioUnico + nombre + " con " + varComparacion + ".";
        }
    }
}