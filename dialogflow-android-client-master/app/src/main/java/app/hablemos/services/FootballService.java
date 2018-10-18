package app.hablemos.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.hablemos.asynctasks.GetComparacionAsyncTask;
import app.hablemos.asynctasks.GetDatosAsyncTask;
import app.hablemos.asynctasks.GetEquiposAsyncTask;
import app.hablemos.asynctasks.GetPosicionesAsyncTask;
import app.hablemos.model.DatosEquipo;
import app.hablemos.model.Equipo;
import app.hablemos.model.EquipoPosicionado;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getMapaEquiposDePrimera() {
        HashMap<String, String> mapaEquiposPrimera = new HashMap<>();
        for (Equipo equipo : this.equiposDePrimera) {
            mapaEquiposPrimera.put(equipo.getNombreReferencia(), equipo.getNombre());
        }
        return mapaEquiposPrimera;
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
        return mapa;
    }

    public String getTopNEquipos(int n) {
        llenarEquiposPosicionados();
        StringBuilder respuesta = new StringBuilder("Los equipos que estan entre los mejores " + n + " son: ");
        String separador = "";
        List<EquipoPosicionado> topN = equiposPosicionados.subList(0,n);
        for (EquipoPosicionado ep : topN) {
            respuesta.append(separador).append(ep.getNombre());
            separador = ", ";
        }
        return respuesta.toString();
    }

    private void llenarEquiposPosicionados() {
        if (this.equiposPosicionados.size() == 0) {
            try {
                this.equiposPosicionados = new GetPosicionesAsyncTask().execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    private String obtenerEquipoVisual(String equipo) {
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
        return "El equipo que está en la posición " + posicion + " es " + equipo;
    }

    public String getDatosEquipo(String equipo) {
        String pagina = "";
        String equipoVisual = obtenerEquipoVisual(equipo);
        for(Equipo eq : this.equiposDePrimera) {
            if (eq.getNombreReferencia().equalsIgnoreCase(equipo)) {
                pagina = eq.getPagina();
            }
        }
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

    public String getEstadisticasEquipo(String equipo) {
        llenarEquiposPosicionados();
        String estadisticas = "";
        for(EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.getNombre().equalsIgnoreCase(mapaEquipos.get(equipo))){
                estadisticas = ep.toString();
            }
        }
        if (!estadisticas.equals("")) return estadisticas;
        else return "No pudieron encontrarse estadisticas para " + equipo;
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
        if (!comparacion.equals("")) return comparacion;
        else return "La comparación entre " + equipoVisual1 + " y " + equipoVisual2 + " no pudo ser realizada";
    }
}
