package app.hablemos.services;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import app.hablemos.R;
import app.hablemos.asynctasks.GetComparacionAsyncTask;
import app.hablemos.asynctasks.GetEquiposAsyncTask;
import app.hablemos.model.football.Comparators;
import app.hablemos.model.football.ConversionMaps;
import app.hablemos.model.football.DatosEquipo;
import app.hablemos.model.football.Equipo;
import app.hablemos.model.football.EquipoPosicionado;
import app.hablemos.model.football.Partido;
import app.hablemos.model.football.PartidoActual;
import app.hablemos.util.AsyncUtil;
import app.hablemos.util.DateUtils;
import app.hablemos.util.FootballUtil;

public class FootballService {

    private List<Equipo> equiposDePrimera;
    private List<EquipoPosicionado> equiposPosicionados;
    private HashMap<String, String> mapaEquipos;
    private HashMap<String, List<String>> jugadoresPrimera;
    private String bullet;

    public FootballService(Context context) {
        this.bullet = context.getString(R.string.bullet);
        this.equiposPosicionados = new ArrayList<>();
        this.equiposDePrimera = new ArrayList<>();
        this.mapaEquipos = ConversionMaps.getMapaEquipos();
        try {
            this.equiposDePrimera = new GetEquiposAsyncTask().execute().get();
            HashMap<String, String> mapaConversion = ConversionMaps.getMapaConversionVisual();
            for(Equipo equipo : this.equiposDePrimera) {
                if (mapaConversion.keySet().contains(equipo.getNombre())){
                    equipo.setNombre(mapaConversion.get(equipo.getNombre()));
                }
            }
            this.jugadoresPrimera = FootballUtil.getJugadoresPrimera(this.equiposDePrimera);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* SERVICIOS EXPUESTOS */
    public String getTablaPosiciones() {
        this.equiposPosicionados = AsyncUtil.llenarEquiposPosicionados();
        List<EquipoPosicionado> topN = equiposPosicionados.subList(0,this.equiposPosicionados.size());
        return FootballUtil.getStringTablaPosiciones(this.equiposDePrimera, topN, "La tabla de posiciones se encuentra de esta manera: ");
    }

    public String getTopNEquipos(int n) {
        this.equiposPosicionados = AsyncUtil.llenarEquiposPosicionados();
        if (n < 1 || n > this.equiposPosicionados.size()) {
            return "La cantidad deseada no es correcta, hay " + this.equiposPosicionados.size() + " equipos actualmente.";
        }
        List<EquipoPosicionado> topN = equiposPosicionados.subList(0,n);
        if(n == 1) {
            return "El equipo que está primero en la tabla es: " + FootballUtil.getNombreRealFromTabla(this.equiposDePrimera, topN.get(0).getNombre(), false);
        }
        return FootballUtil.getStringTablaPosiciones(this.equiposDePrimera, topN, "Los equipos que están entre los mejores " + n + " son: ");
    }

    public String getBottomNEquipos(int n) {
        this.equiposPosicionados = AsyncUtil.llenarEquiposPosicionados();
        if (n < 1 || n > this.equiposPosicionados.size()) {
            return "La cantidad deseada no es correcta, hay " + this.equiposPosicionados.size() + " equipos actualmente.";
        }
        List<EquipoPosicionado> bottomN = equiposPosicionados.subList(this.equiposPosicionados.size() - n, this.equiposPosicionados.size());
        if(n == 1) {
            return "El equipo que está último en la tabla es: " + FootballUtil.getNombreRealFromTabla(this.equiposDePrimera, bottomN.get(0).getNombre(), false);
        }
        return FootballUtil.getStringTablaPosiciones(this.equiposDePrimera, bottomN, "Los últimos " + n + " equipos de la tabla son: ");
    }

    public String getPosicionEquipo(String equipo) {
        this.equiposPosicionados = AsyncUtil.llenarEquiposPosicionados();
        Equipo equipoVisual = FootballUtil.obtenerEquipoVisual(this.equiposDePrimera, equipo);
        int posicion = FootballUtil.obtenerEquipoPosicionado(this.equiposPosicionados, equipo).getPosicion();
        if (posicion != 0)
            return equipoVisual.getNombre() + " está en la posición " + posicion + ".";
        else
            return "El equipo solicitado no pudo ser encontrado.";
    }

    public String getEquipoEnPosicion(int posicion) {
        this.equiposPosicionados = AsyncUtil.llenarEquiposPosicionados();
        if (posicion < 1 || posicion > this.equiposPosicionados.size()) {
            return "La posicion deseada no es correcta, hay " + this.equiposPosicionados.size() + " equipos actualmente.";
        }
        String equipo = "";
        for(EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.getPosicion() == posicion){
                equipo = ep.getNombre();
            }
        }
        return "El equipo que está en la posición " + posicion + " es " + FootballUtil.obtenerEquipoVisual(this.equiposDePrimera, equipo).getNombre() + ".";
    }

    public String getDatosEquipo(String equipo) {
        Equipo equipoVisual = FootballUtil.obtenerEquipoVisual(this.equiposDePrimera, equipo);
        if (equipoVisual != null) {
            DatosEquipo datos = AsyncUtil.getDatosAsync(equipoVisual.getPagina());
            return equipoVisual.getNombre() + datos.toString();
        } else
            return "El equipo solicitado no pudo ser encontrado.";
    }

    public String getEstadisticasEquipo(String equipo) {
        this.equiposPosicionados = AsyncUtil.llenarEquiposPosicionados();
        String equipoVisual = FootballUtil.obtenerEquipoVisual(this.equiposDePrimera, equipo).getNombre();
        EquipoPosicionado equipoPosicionado = FootballUtil.obtenerEquipoPosicionado(this.equiposPosicionados, equipo);
        if (equipoPosicionado != null) return equipoVisual + equipoPosicionado.toString();
        else return "No pudieron encontrarse estadísticas para " + equipoVisual + ".";
    }

    public String getComparacionEquipos(String equipo1, String equipo2) {
        String equipoVisual1 = FootballUtil.obtenerEquipoVisual(this.equiposDePrimera, equipo1).getNombre();
        String equipoVisual2 = FootballUtil.obtenerEquipoVisual(this.equiposDePrimera, equipo2).getNombre();
        String comparacion;
        try {
            comparacion = new GetComparacionAsyncTask(mapaEquipos.get(equipo1), mapaEquipos.get(equipo2)).execute().get();
        } catch (Exception e) {
            return "Al menos uno de los equipos ingresados no es correcto.";
        }
        if (!comparacion.equals("")) return ConversionMaps.modificarNombresEquiposPrimera(comparacion);
        else return "La comparación entre " + equipoVisual1 + " y " + equipoVisual2 + " no pudo ser realizada.";
    }

    public String getProximoPartido(String equipo) {
        Equipo equipoVisual = FootballUtil.obtenerEquipoVisual(this.equiposDePrimera, equipo);
        if (equipoVisual != null) {
            List<Partido> partidos = AsyncUtil.getPartidosAsync(equipoVisual.getPagina());
            List<Partido> partidosFiltrados = new ArrayList<>();
            for (Partido partido : partidos) {
                if (partido.getResultado().equals("-")) {
                    partidosFiltrados.add(partido);
                }
            }
            Collections.sort(partidosFiltrados, Comparators.comparadorDeFecha);
            StringBuilder retorno = new StringBuilder();
            int posEnLista = 0;
            if (partidosFiltrados.get(0).getDia().contains("Post")) {
                retorno.append(equipoVisual.getNombre()).append(" tiene un partido postergado con ").append(FootballUtil.getNombreRealFromTabla(this.equiposDePrimera, partidosFiltrados.get(0).getRival(), false)).append(" sin fecha asignada. En el siguiente encuentro,").append(partidosFiltrados.get(1).toString(FootballUtil.getNombreRealFromTabla(this.equiposDePrimera, partidosFiltrados.get(1).getRival(), false)));
                posEnLista = 1;
            } else {
                retorno.append(equipoVisual.getNombre()).append(partidosFiltrados.get(0).toString(FootballUtil.getNombreRealFromTabla(this.equiposDePrimera, partidosFiltrados.get(0).getRival(), false)));
            }
            List<PartidoActual> partidosActuales;
            if (partidosFiltrados.get(posEnLista).getDia().equals(DateUtils.getNowString())) {
                partidosActuales = AsyncUtil.getPartidosActuales();
            } else {
                partidosActuales = AsyncUtil.getPartidosProximaFecha(partidosFiltrados.get(posEnLista).getFecha());
            }
            for(PartidoActual partidoActual : partidosActuales) {
                if (partidoActual.getEquipoLocal().equalsIgnoreCase(mapaEquipos.get(equipo)) ||
                        partidoActual.getEquipoVisitante().equalsIgnoreCase(mapaEquipos.get(equipo))) {
                    retorno.append(" a las ").append(partidoActual.getHoraJuego());
                }
            }
            return retorno.toString();
        } else
            return "El equipo solicitado no pudo ser encontrado.";
    }

    public String getUltimoPartido(String equipo) {
        Equipo equipoVisual = FootballUtil.obtenerEquipoVisual(this.equiposDePrimera, equipo);
        if (equipoVisual != null) {
            List<Partido> partidos = AsyncUtil.getPartidosAsync(equipoVisual.getPagina());
            List<Partido> partidosFiltrados = new ArrayList<>();
            for (Partido partido : partidos) {
                if (!partido.getResultado().equals("-")) {
                    partidosFiltrados.add(partido);
                }
            }
            Collections.sort(partidosFiltrados, Comparators.comparadorDeFecha);
            StringBuilder retorno = new StringBuilder(equipoVisual.getNombre());
            List<PartidoActual> partidosActuales;
            if (partidosFiltrados.get(partidosFiltrados.size()-1).getDia().equals(DateUtils.getNowString())) {
                partidosActuales = AsyncUtil.getPartidosActuales();
            } else {
                partidosActuales = AsyncUtil.getPartidosPasados(partidosFiltrados.get(partidosFiltrados.size()-1).getFecha());
            }
            String nombreEquipo = mapaEquipos.get(equipo).replace("(","").replace(")","");
            for(PartidoActual partidoActual : partidosActuales) {
                if (partidoActual.getEquipoLocal().replace("(","").replace(")","").equalsIgnoreCase(nombreEquipo) ||
                        partidoActual.getEquipoVisitante().equalsIgnoreCase(nombreEquipo)) {
                    if(partidoActual.getEstado().equalsIgnoreCase("jugandose")) {
                        retorno.append(partidosFiltrados.get(partidosFiltrados.size() - 1).toStringEnCurso(FootballUtil.getNombreRealFromTabla(this.equiposDePrimera, partidosFiltrados.get(partidosFiltrados.size() - 1).getRival(), false)));
                        if (partidoActual.getTiempoJuego().equalsIgnoreCase("e.t.") || partidoActual.getTiempoJuego().equalsIgnoreCase("e. t.")) {
                            retorno.append(". En este momento están en el entretiempo.");
                        } else {
                            retorno.append(" a los ").append(Integer.parseInt(partidoActual.getTiempoJuego().replace("'", ""))).append(" minutos.");
                        }
                    } else if(partidoActual.getEstado().equalsIgnoreCase("finaliza")) {
                        retorno.append(partidosFiltrados.get(partidosFiltrados.size() - 1).toStringUltimo(FootballUtil.getNombreRealFromTabla(this.equiposDePrimera, partidosFiltrados.get(partidosFiltrados.size() - 1).getRival(), false)));
                    }
                    if (partidoActual.getGolesEquipoLocal()==1){
                        retorno.append(FootballUtil.obtenerStringGolUnico(this.equiposDePrimera, this.jugadoresPrimera, partidoActual.getEquipoLocal(),"del local", partidoActual.getGolesLocal()));
                    } else if (partidoActual.getGolesEquipoLocal()>1){
                        retorno.append("\nLos goles del equipo local fueron marcados por ").append(FootballUtil.obtenerMarcadores(this.equiposDePrimera, this.jugadoresPrimera, partidoActual.getEquipoLocal(), partidoActual.getGolesLocal()));
                    }
                    if (partidoActual.getGolesEquipoVisitante()==1){
                        retorno.append(FootballUtil.obtenerStringGolUnico(this.equiposDePrimera, this.jugadoresPrimera, partidoActual.getEquipoVisitante(),"de la visita", partidoActual.getGolesVisitante()));
                    } else if (partidoActual.getGolesEquipoVisitante()>1){
                        retorno.append("\nLos goles de la visita fueron marcados por ").append(FootballUtil.obtenerMarcadores(this.equiposDePrimera, this.jugadoresPrimera, partidoActual.getEquipoVisitante(),partidoActual.getGolesVisitante()));
                    }
                }
            }
            return retorno.toString();
        } else
            return "El equipo solicitado no pudo ser encontrado";
    }

    public String getLibertadores() {
        this.equiposPosicionados = AsyncUtil.llenarEquiposPosicionados();
        List<EquipoPosicionado> libertadores = new ArrayList<>();
        for (EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.isLibertadores()) {
                libertadores.add(ep);
            }
        }
        return FootballUtil.armarStringConLista(this.equiposDePrimera, "Los equipos que están en zona de Copa Libertadores son: ", libertadores, bullet);
    }

    public String getSudamericana() {
        this.equiposPosicionados = AsyncUtil.llenarEquiposPosicionados();
        List<EquipoPosicionado> sudamericana = new ArrayList<>();
        for (EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.isSudamericana()) {
                sudamericana.add(ep);
            }
        }
        return FootballUtil.armarStringConLista(this.equiposDePrimera, "Los equipos que están en zona de Copa Sudamericana son: ", sudamericana, bullet);
    }

    public String getDescienden() {
        this.equiposPosicionados = AsyncUtil.llenarEquiposPosicionados();
        List<EquipoPosicionado> descienden = new ArrayList<>();
        for (EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.isDesciende()) {
                descienden.add(ep);
            }
        }
        Collections.sort(descienden, Comparators.comparadorDePromedios);
        Collections.reverse(descienden);
        return FootballUtil.armarStringConLista(this.equiposDePrimera, "Los equipos que están en zona de descenso son: ", descienden, bullet);
    }

    public String getEfemerides() {
        List<String> efemerides = AsyncUtil.getEfemerides();
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
        return ConversionMaps.modificarNombresEquiposPrimera(respuesta.toString()).replace(")", ",").replace(" (", ", de ");
    }

    public String getGoleador() {
        TreeMap<Integer, List<String>> goleadores = AsyncUtil.getGoleadores();
        StringBuilder respuesta = new StringBuilder();
        int contador = 1;
        for (Integer cantGoles : goleadores.keySet()) {
            if (contador == 1) {
                if (goleadores.get(cantGoles).size() > 1) {
                    respuesta.append("Los goleadores del torneo, con ").append(cantGoles).append(" goles son ");
                    respuesta.append(FootballUtil.getGoleadoresString(goleadores.get(cantGoles)));
                } else {
                    respuesta.append("El goleador del torneo, con ").append(cantGoles).append(" goles es ").append(goleadores.get(cantGoles).get(0).replace(". ", " ").replace(".", " "));
                }
            } else if (contador == 2) {
                if (goleadores.get(cantGoles).size() > 1) {
                    respuesta.append("\nLe siguen, con ").append(cantGoles).append(" goles ");
                    respuesta.append(FootballUtil.getGoleadoresString(goleadores.get(cantGoles)));
                } else {
                    respuesta.append("\nLe sigue, con ").append(cantGoles).append(" goles es ").append(goleadores.get(cantGoles).get(0).replace(". ", " ").replace(".", " "));
                }
            } else if (contador == 3){
                respuesta.append("\nEn tercer lugar, con ").append(cantGoles);
                if (goleadores.get(cantGoles).size() > 1) {
                    respuesta.append(" goles, están ");
                    respuesta.append(FootballUtil.getGoleadoresString(goleadores.get(cantGoles)));
                } else {
                    respuesta.append(" goles está ").append(goleadores.get(cantGoles).get(0).replace(". ", " ").replace(".", " "));
                }
            }
            contador++;
        }
        respuesta.append(".");
        return ConversionMaps.modificarNombresEquiposPrimera(respuesta.toString()).replace(")", "").replace(" (", ", de ");
    }

    public String getEquipoMasGoleador() {
        List<EquipoPosicionado> equiposPosicionados = FootballUtil.getEquiposFiltrados(Comparators.comparadorGolesAFavor);
        int goles = equiposPosicionados.get(0).getGolesAFavor();
        List<EquipoPosicionado> equiposFiltrados = new ArrayList<>();
        for (EquipoPosicionado equipo : equiposPosicionados) {
            if (equipo.getGolesAFavor() == goles) {
                equiposFiltrados.add(equipo);
            }
        }
        return FootballUtil.generarRespuesta(this.equiposDePrimera, "Los equipos que más goles hicieron son: ", equiposFiltrados, goles, "El equipo más goleador es ");
    }

    public String getEquipoMasGoleado() {
        List<EquipoPosicionado> equiposPosicionados = FootballUtil.getEquiposFiltrados(Comparators.comparadorGolesEnContra);
        int goles = equiposPosicionados.get(0).getGolesEnContra();
        List<EquipoPosicionado> equiposFiltrados = new ArrayList<>();
        for (EquipoPosicionado equipo : equiposPosicionados) {
            if (equipo.getGolesEnContra() == goles) {
                equiposFiltrados.add(equipo);
            }
        }
        return FootballUtil.generarRespuesta(this.equiposDePrimera, "Los equipos a los que más goles les hicieron son: ", equiposFiltrados, goles, "El equipo al que más goles le convirtieron es ");
    }

    public String getEquipoMayorDiferenciaDeGol() {
        List<EquipoPosicionado> equiposPosicionados = FootballUtil.getEquiposFiltrados(Comparators.comparadorDiferenciaGol);
        int goles = equiposPosicionados.get(0).getDiferencia();
        List<EquipoPosicionado> equiposFiltrados = new ArrayList<>();
        for (EquipoPosicionado equipo : equiposPosicionados) {
            if (equipo.getDiferencia() == goles) {
                equiposFiltrados.add(equipo);
            }
        }
        return FootballUtil.generarRespuesta(this.equiposDePrimera, "Los equipos con más diferencia de gol son ", equiposFiltrados, goles, "El equipo con más diferencia de gol es ");
    }

    public String getEquipoMasPartidosGanados() {
        List<EquipoPosicionado> equiposPosicionados = FootballUtil.getEquiposFiltrados(Comparators.comparadorPartidosGanados);
        int partidosGanados = equiposPosicionados.get(0).getPartidosGanados();
        List<EquipoPosicionado> equiposFiltrados = new ArrayList<>();
        for (EquipoPosicionado equipo : equiposPosicionados) {
            if (equipo.getPartidosGanados() == partidosGanados) {
                equiposFiltrados.add(equipo);
            }
        }
        return FootballUtil.generarRespuesta(this.equiposDePrimera, "Los equipos que más partidos ganaron son ", equiposFiltrados, partidosGanados, "El equipo con más partidos ganados es ");
    }

    public String getEquipoMasPartidosEmpatados() {
        List<EquipoPosicionado> equiposPosicionados = FootballUtil.getEquiposFiltrados(Comparators.comparadorPartidosEmpatados);
        int partidosEmpatados = equiposPosicionados.get(0).getPartidosEmpatados();
        List<EquipoPosicionado> equiposFiltrados = new ArrayList<>();
        for (EquipoPosicionado equipo : equiposPosicionados) {
            if (equipo.getPartidosEmpatados() == partidosEmpatados) {
                equiposFiltrados.add(equipo);
            }
        }
        return FootballUtil.generarRespuesta(this.equiposDePrimera, "Los equipos que más partidos empataron son ", equiposFiltrados, partidosEmpatados, "El equipo que empató mayor cantidad de partidos es ");
    }

    public String getEquipoMasPartidosPerdidos() {
        List<EquipoPosicionado> equiposPosicionados = FootballUtil.getEquiposFiltrados(Comparators.comparadorPartidosPerdidos);
        int partidosPerdidos = equiposPosicionados.get(0).getPartidosPerdidos();
        List<EquipoPosicionado> equiposFiltrados = new ArrayList<>();
        for (EquipoPosicionado equipo : equiposPosicionados) {
            if (equipo.getPartidosPerdidos() == partidosPerdidos) {
                equiposFiltrados.add(equipo);
            }
        }
        return FootballUtil.generarRespuesta(this.equiposDePrimera, "Los equipos que más partidos perdieron son ", equiposFiltrados, partidosPerdidos, "El equipo que perdió mayor cantidad de partidos es ");
    }

    public String getClasicoEquipo(String equipo) {
        return ConversionMaps.getClasicos().get(equipo) + "\n¿Querés saber el historial entre ellos?";
    }

    public String getHistorialClasico(String equipo) {
        return this.getComparacionEquipos(equipo, ConversionMaps.getDerby().get(equipo));
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
       return FootballUtil.obtenerEquipoVisual(this.equiposDePrimera, equipo);
    }
}