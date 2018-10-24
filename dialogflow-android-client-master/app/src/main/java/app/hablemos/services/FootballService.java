package app.hablemos.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import app.hablemos.asynctasks.GetComparacionAsyncTask;
import app.hablemos.asynctasks.GetDatosAsyncTask;
import app.hablemos.asynctasks.GetEquiposAsyncTask;
import app.hablemos.asynctasks.GetPartidosActualesAsyncTask;
import app.hablemos.asynctasks.GetPartidosAsyncTask;
import app.hablemos.asynctasks.GetPosicionesAsyncTask;
import app.hablemos.asynctasks.GetResultadoUltimoPartidoAsyncTask;
import app.hablemos.model.football.DatosEquipo;
import app.hablemos.model.football.Equipo;
import app.hablemos.model.football.EquipoPosicionado;
import app.hablemos.model.football.Partido;
import app.hablemos.model.football.PartidoActual;

public class FootballService {

    private List<Equipo> equiposDePrimera;
    private List<EquipoPosicionado> equiposPosicionados;
    private HashMap<String, String> mapaEquipos;

    public FootballService() {
        this.equiposPosicionados = new ArrayList<>();
        this.equiposDePrimera = new ArrayList<>();
        this.mapaEquipos = llenarMapaEquipos();
        try {
            this.equiposDePrimera = new GetEquiposAsyncTask().execute().get();
            HashMap<String, String> mapaConversion = getMapaConversionVisual();
            for(Equipo equipo : this.equiposDePrimera) {
                if (mapaConversion.keySet().contains(equipo.getNombre())){
                    equipo.setNombre(mapaConversion.get(equipo.getNombre()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String,String> getMapaConversionVisual() {
        HashMap<String, String> mapa = new HashMap<>();
        mapa.put("Argentinos", "Argentinos Juniors");
        mapa.put("Atletico Tucuman", "Atlético Tucumán");
        mapa.put("Belgrano (Cba)", "Belgrano de Córdoba");
        mapa.put("Colon (SF)", "Colón de Santa Fe");
        mapa.put("Estudiantes (La Plata)", "Estudiantes de La Plata");
        mapa.put("Gimnasia (La Plata)", "Gimnasia y Esgrima de La Plata");
        mapa.put("Godoy Cruz (Mendoza)", "Godoy Cruz");
        mapa.put("Huracan", "Huracán");
        mapa.put("Lanus", "Lanús");
        mapa.put("San Martin (San Juan)", "San Martín de San Juan");
        mapa.put("San Martin (Tucuman)", "San Martín de Tucumán");
        mapa.put("Talleres (Cordoba)", "Talleres de Córdoba");
        mapa.put("Union (Santa Fe)", "Unión de Santa Fe");
        return mapa;
    }

    public List<String> getEquiposDePrimera() {
        List<String> equipos = new ArrayList<>();
        for (Equipo equipo : this.equiposDePrimera) {
            equipos.add(equipo.getNombre());
        }
        return equipos;
    }

    public String getNombreReferencia(String nombreReal) {
        String nombreReferencia = "";
        for(Equipo eq : this.equiposDePrimera) {
            if (eq.getNombre().equalsIgnoreCase(nombreReal)) {
                nombreReferencia = eq.getNombreReferencia();
            }
        }
        return nombreReferencia;
    }

    private HashMap<String,String> llenarMapaEquipos() {
        HashMap<String, String> mapa = new HashMap<>();
        mapa.put("aldosivi", "Aldosivi");
        mapa.put("argentinos", "Argentinos");
        mapa.put("atltucuman", "Atl Tucuman");
        mapa.put("banfield", "Banfield");
        mapa.put("belgrano", "Belgrano");
        mapa.put("bocajuniors", "Boca Juniors");
        mapa.put("colon", "Colon");
        mapa.put("defyjusticia", "Def y Justicia");
        mapa.put("estudianteslp", "Estudiantes (LP)");
        mapa.put("gimnasialp", "Gimnasia (LP)");
        mapa.put("godoycruz", "Godoy Cruz");
        mapa.put("huracan", "Huracan");
        mapa.put("independiente", "Independiente");
        mapa.put("lanus", "Lanus");
        mapa.put("newells", "Newells");
        mapa.put("patronato", "Patronato");
        mapa.put("racingclub", "Racing Club");
        mapa.put("riverplate", "River Plate");
        mapa.put("rosariocentral", "Rosario Central");
        mapa.put("sanlorenzo", "San Lorenzo");
        mapa.put("sanmartinsj", "San Martin (SJ)");
        mapa.put("sanmartint", "San Martin (T)");
        mapa.put("talleresc", "Talleres (C)");
        mapa.put("tigre", "Tigre");
        mapa.put("union", "Union");
        mapa.put("velez", "Velez");
        mapa.put("ninguno", "Ninguno");
        return mapa;
    }

    public String getTopNEquipos(int n) {
        llenarEquiposPosicionados();
        StringBuilder respuesta = new StringBuilder("Los equipos que están entre los mejores " + n + " son: ");
        String separador = "";
        List<EquipoPosicionado> topN = equiposPosicionados.subList(0,n);
        for (EquipoPosicionado ep : topN) {
            respuesta.append(separador).append(getNombreRealFromTabla(ep.getNombre()));
            separador = ", ";
        }
        return modificarNombresEquiposPrimera(respuesta.toString());
    }

    private String getNombreRealFromTabla(String nombre) {
        String nombreReal = "";
        for (String key : mapaEquipos.keySet()) {
            if (mapaEquipos.get(key).equalsIgnoreCase(nombre)) {
                nombreReal = obtenerEquipoVisual(key);
            }
        }
        return nombreReal;
    }

    public String getBottomNEquipos(int n) {
        llenarEquiposPosicionados();
        StringBuilder respuesta = new StringBuilder("Los últimos " + n + " equipos de la tabla son: ");
        String separador = "";
        List<EquipoPosicionado> bottomN = equiposPosicionados.subList(this.equiposPosicionados.size() - n, this.equiposPosicionados.size());
        for (EquipoPosicionado ep : bottomN) {
            respuesta.append(separador).append(getNombreRealFromTabla(ep.getNombre()));
            separador = ", ";
        }
        return modificarNombresEquiposPrimera(respuesta.toString());
    }

    private void llenarEquiposPosicionados() {
        try {
            this.equiposPosicionados = new GetPosicionesAsyncTask().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPosicionEquipo(String equipo) {
        llenarEquiposPosicionados();
        String equipoVisual = obtenerEquipoVisual(equipo);
        int posicion = 0;
        for(EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.getNombre().equalsIgnoreCase(mapaEquipos.get(equipo))){
                posicion = ep.getPosicion();
            }
        }
        if (posicion != 0)
            return equipoVisual + " está en la posición " + posicion;
        else
            return "El equipo solicitado no pudo ser encontrado";
    }

    public String obtenerEquipoVisual(String equipo) {
        String equipoVisual = "";
        for(Equipo eq : this.equiposDePrimera) {
            if (eq.getNombreReferencia().equalsIgnoreCase(equipo)) {
                equipoVisual = eq.getNombre();
            }
        }
        return equipoVisual;
    }

    public String getEquipoEnPosicion(int posicion) {
        llenarEquiposPosicionados();
        if (posicion < 1 || posicion > this.equiposPosicionados.size()) {
            return "La posicion deseada no es correcta, hay " + this.equiposPosicionados.size() + " equipos actualmente";
        }
        String equipo = "";
        for(EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.getPosicion() == posicion){
                equipo = ep.getNombre();
            }
        }
        return "El equipo que está en la posición " + posicion + " es " + obtenerEquipoVisual(equipo);
    }

    public String getDatosEquipo(String equipo) {
        String equipoVisual = obtenerEquipoVisual(equipo);
        String pagina = getPagina(equipo);
        if (!pagina.equals("")) {
            DatosEquipo datos = new DatosEquipo();
            try {
                datos = new GetDatosAsyncTask(pagina).execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return equipoVisual + datos.toString();
        } else
            return "El equipo solicitado no pudo ser encontrado";
    }

    private String getPagina(String equipo) {
        String pagina = "";
        for(Equipo eq : this.equiposDePrimera) {
            if (eq.getNombreReferencia().equalsIgnoreCase(equipo)) {
                pagina = eq.getPagina();
            }
        }
        return pagina;
    }

    public String getEstadisticasEquipo(String equipo) {
        llenarEquiposPosicionados();
        String equipoVisual = obtenerEquipoVisual(equipo);
        String estadisticas = "";
        for(EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.getNombre().equalsIgnoreCase(mapaEquipos.get(equipo))){
                estadisticas = ep.toString();
            }
        }
        if (!estadisticas.equals("")) return equipoVisual + estadisticas;
        else return "No pudieron encontrarse estadísticas para " + equipoVisual;
    }

    public String getComparacionEquipos(String equipo1, String equipo2) {
        String equipoVisual1 = obtenerEquipoVisual(equipo1);
        String equipoVisual2 = obtenerEquipoVisual(equipo2);
        String comparacion;
        try {
            comparacion = new GetComparacionAsyncTask(mapaEquipos.get(equipo1), mapaEquipos.get(equipo2)).execute().get();
        } catch (Exception e) {
            return "Al menos uno de los equipos ingresados no es correcto";
        }
        if (!comparacion.equals("")) return modificarNombresEquiposPrimera(comparacion);
        else return "La comparación entre " + equipoVisual1 + " y " + equipoVisual2 + " no pudo ser realizada";
    }

    public String getProximoPartido(String equipo) {
        String equipoVisual = obtenerEquipoVisual(equipo);
        String pagina = getPagina(equipo);
        if (!pagina.equals("")) {
            List<Partido> partidos = getPartidosAsync(pagina);
            List<Partido> partidosFiltrados = new ArrayList<>();
            for (Partido partido : partidos) {
                if (partido.getResultado().equals("-")) {
                    partidosFiltrados.add(partido);
                }
            }
            Collections.sort(partidosFiltrados, comparadorDeFecha);
            StringBuilder retorno = new StringBuilder();
            int posEnLista = 0;
            if (partidosFiltrados.get(0).getDia().contains("Post")) {
                retorno.append(equipoVisual).append(" tiene un partido postergado con ").append(getNombreRealFromTabla(partidosFiltrados.get(0).getRival())).append(" sin fecha asignada, en el siguiente encuentro").append(partidosFiltrados.get(1).toString(getNombreRealFromTabla(partidosFiltrados.get(1).getRival())));
                posEnLista = 1;
            } else {
                retorno.append(equipoVisual).append(partidosFiltrados.get(0).toString(getNombreRealFromTabla(partidosFiltrados.get(0).getRival())));
            }
            Calendar calendar = Calendar.getInstance();
            String now = (calendar.get(Calendar.DATE)) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR);
            if (partidosFiltrados.get(posEnLista).getDia().equals(now)) {
                List<PartidoActual> partidosActuales = getPartidosActuales();
                for(PartidoActual partidoActual : partidosActuales) {
                    if (partidoActual.getEquipoLocal().equalsIgnoreCase(mapaEquipos.get(equipo)) ||
                            partidoActual.getEquipoVisitante().equalsIgnoreCase(mapaEquipos.get(equipo))) {
                        retorno.append(" a las ").append(partidoActual.getHoraJuego());
                    }
                }
            }
            return modificarNombresEquiposPrimera(retorno.toString());
        } else
            return "El equipo solicitado no pudo ser encontrado";
    }

    private List<Partido> getPartidosAsync(String pagina) {
        List<Partido> partidos = new ArrayList<>();
        try {
            partidos = new GetPartidosAsyncTask(pagina).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partidos;
    }

    public String getUltimoPartido(String equipo) {
        String equipoVisual = obtenerEquipoVisual(equipo);
        String pagina = getPagina(equipo);
        if (!pagina.equals("")) {
            List<Partido> partidos = getPartidosAsync(pagina);
            List<Partido> partidosFiltrados = new ArrayList<>();
            for (Partido partido : partidos) {
                if (!partido.getResultado().equals("-")) {
                    partidosFiltrados.add(partido);
                }
            }
            Collections.sort(partidosFiltrados, comparadorDeFecha);
            StringBuilder retorno = new StringBuilder(equipoVisual);
            Calendar calendar = Calendar.getInstance();
            String now = (calendar.get(Calendar.DATE)) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR);
            List<PartidoActual> partidosActuales;
            if (partidosFiltrados.get(partidosFiltrados.size()-1).getDia().equals(now)) {
                partidosActuales = getPartidosActuales();
            } else {
                partidosActuales = getPartidosPasados(partidosFiltrados.get(partidosFiltrados.size()-1).getFecha());
            }
            for(PartidoActual partidoActual : partidosActuales) {
                if (partidoActual.getEquipoLocal().equalsIgnoreCase(mapaEquipos.get(equipo)) ||
                        partidoActual.getEquipoVisitante().equalsIgnoreCase(mapaEquipos.get(equipo))) {
                    if(partidoActual.getEstado().equalsIgnoreCase("jugandose")) {
                        if (partidoActual.getTiempoJuego().equalsIgnoreCase("e.t.")) {
                            retorno.append(partidosFiltrados.get(partidosFiltrados.size() - 1).toStringEnCurso(getNombreRealFromTabla(partidosFiltrados.get(partidosFiltrados.size() - 1).getRival()))).append(", están en el entretiempo.");
                        } else {
                            retorno.append(partidosFiltrados.get(partidosFiltrados.size() - 1).toStringEnCurso(getNombreRealFromTabla(partidosFiltrados.get(partidosFiltrados.size() - 1).getRival()))).append(" a los ").append(Integer.parseInt(partidoActual.getTiempoJuego().replace("'", ""))).append(" minutos.");
                        }
                    } else if(partidoActual.getEstado().equalsIgnoreCase("finaliza")) {
                        retorno.append(partidosFiltrados.get(partidosFiltrados.size() - 1).toStringUltimo(getNombreRealFromTabla(partidosFiltrados.get(partidosFiltrados.size() - 1).getRival())));
                    }
                    if (partidoActual.getGolesEquipoLocal()==1){
                        retorno.append("\nEl gol del local lo marcó ").append(partidoActual.getGolesLocal().split("'")[1].replace(";", "").replace(".", "").trim()).append(" a los ").append(partidoActual.getGolesLocal().split("'")[0]).append(" minutos.");
                    } else if (partidoActual.getGolesEquipoLocal()>1){
                        retorno.append("\nLos goles del equipo local fueron marcados por ").append(obtenerMarcadores(partidoActual.getGolesLocal()));
                    }
                    if (partidoActual.getGolesEquipoVisitante()==1){
                        retorno.append("\nEl gol de la visita lo marcó ").append(partidoActual.getGolesVisitante().split("'")[1].replace(";", "").replace(".", "").trim()).append(" a los ").append(partidoActual.getGolesVisitante().split("'")[0]).append(" minutos.");
                    } else if (partidoActual.getGolesEquipoVisitante()>1){
                        retorno.append("\nLos goles de la visita fueron marcados por ").append(obtenerMarcadores(partidoActual.getGolesVisitante()));
                    }
                }
            }
            return modificarNombresEquiposPrimera(retorno.toString());
        } else
            return "El equipo solicitado no pudo ser encontrado";
    }

    private String obtenerMarcadores(String golesLocal) {
        String separador = "";
        StringBuilder marcadores = new StringBuilder();
        String[] marcadoresArray = golesLocal.split(";");
        for (String str : marcadoresArray) {
            String[] submarcadoresArray = str.split("'");
            String tiempo = submarcadoresArray[0].trim();
            String marcador = submarcadoresArray[1].trim().replace("e.c.", "en contra").replace(".", "");
            marcadores.append(separador).append(marcador).append(" a los ").append(tiempo).append(" minutos");
            separador = ", ";
        }
        return marcadores + ".";
    }

    private List<PartidoActual> getPartidosActuales() {
        List<PartidoActual> partidosActuales = new ArrayList<>();
        try {
            partidosActuales = new GetPartidosActualesAsyncTask().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partidosActuales;
    }

    private List<PartidoActual> getPartidosPasados(String fecha) {
        List<PartidoActual> partidosActuales = new ArrayList<>();
        try {
            partidosActuales = new GetResultadoUltimoPartidoAsyncTask(fecha).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partidosActuales;
    }

    private Comparator<Partido> comparadorDeFecha = new Comparator<Partido>() {
        @Override
        public int compare(Partido partido1, Partido partido2) {
            return partido1.getDiaDePartido().compareTo(partido2.getDiaDePartido());
        }
    };

    private Comparator<EquipoPosicionado> comparadorDePromedios = new Comparator<EquipoPosicionado>() {
        @Override
        public int compare(EquipoPosicionado eq1, EquipoPosicionado eq2) {
            return Float.compare(eq1.getPromedio(), eq2.getPromedio());
        }
    };

    public String getLibertadores() {
        llenarEquiposPosicionados();
        StringBuilder respuesta = new StringBuilder("Los equipos que están en zona de Copa Libertadores son: ");
        String separador = "";
        for (EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.isLibertadores()) {
                respuesta.append(separador).append(getNombreRealFromTabla(ep.getNombre()));
                separador = ", ";
            }
        }
        return respuesta.toString();
    }

    public String getSudamericana() {
        llenarEquiposPosicionados();
        StringBuilder respuesta = new StringBuilder("Los equipos que están en zona de Copa Sudamericana son: ");
        String separador = "";
        for (EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.isSudamericana()) {
                respuesta.append(separador).append(getNombreRealFromTabla(ep.getNombre()));
                separador = ", ";
            }
        }
        return respuesta.toString();
    }

    public String getDescienden() {
        llenarEquiposPosicionados();
        List<EquipoPosicionado> descienden = new ArrayList<>();
        for (EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.isDesciende()) {
                descienden.add(ep);
            }
        }
        Collections.sort(descienden, comparadorDePromedios);
        Collections.reverse(descienden);
        StringBuilder respuesta = new StringBuilder("Los equipos que están en zona de descenso son: ");
        String separador = "";
        int contador = 0;
        for (EquipoPosicionado ep : descienden) {
            if (contador == descienden.size()) {
                respuesta.append(" y ").append(getNombreRealFromTabla(ep.getNombre()));
            } else {
                respuesta.append(separador).append(getNombreRealFromTabla(ep.getNombre()));
                separador = ", ";
            }
        }
        return respuesta.toString();
    }

    public String getEfemerides() {
        return "";
        //$("#cajadia div.eldia[style='background: #5ac382']").parent().children("div.cumplejuga").text()
    }

    public String getGoleador() {
        return "";
    }

    private String modificarNombresEquiposPrimera(String input){
        input = input
        .replace("Argentinos Juniors", "Argentinos")
        .replace("Argentinos", "Argentinos Juniors")
        .replace("Atletico Tucuman", "Atl Tucumán")
        .replace("Atl Tucuman", "Atlético Tucumán")
        .replace("Belgrano (Cba)", "Belgrano")
        .replace("Belgrano", "Belgrano de Córdoba")
        .replace("Colon (SF)", "Colon")
        .replace("Colon", "Colón de Santa Fe")
        .replace("Def y Justicia", "Defensa y Justicia")
        .replace("Estudiantes (La Plata)", "Estudiantes (LP)")
        .replace("Estudiantes (LP)", "Estudiantes de La Plata")
        .replace("Gimnasia (La Plata)", "Gimnasia (LP)")
        .replace("Gimnasia (LP)", "Gimnasia y Esgrima de La Plata")
        .replace("Godoy Cruz (Mendoza)", "Godoy Cruz")
        .replace("Huracan", "Huracán")
        .replace("Lanus", "Lanús")
        .replace("Newells Old Boys", "Newells")
        .replace("Newells", "Newells Old Boys")
        .replace("San Martin (San Juan)", "San Martin (SJ)")
        .replace("San Martin (SJ)", "San Martín de San Juan")
        .replace("San Martin (Tucuman)", "San Martin (T)")
        .replace("San Martin (T)", "San Martín de Tucumán")
        .replace("Talleres (Cordoba)", "Talleres (C)")
        .replace("Talleres (C)", "Talleres de Córdoba")
        .replace("Union (Santa Fe)", "Union")
        .replace("Union", "Unión de Santa Fe")
        .replace("Velez Sarsfield", "Velez")
        .replace("Velez", "Velez Sarsfield");
        return input;
    }
}