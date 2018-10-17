package app.hablemos.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        this.equiposPosicionados = new ArrayList<EquipoPosicionado>();
        this.equiposDePrimera = new ArrayList<Equipo>();
        this.mapaEquipos = llenarMapaEquipos();
        try {
            this.equiposDePrimera = new GetEquiposAsyncTask().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String,String> llenarMapaEquipos() {
        HashMap<String, String> mapa = new HashMap<String, String>();
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
        if (this.equiposPosicionados.size() == 0) {
            try {
                this.equiposPosicionados = new GetPosicionesAsyncTask().execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String respuesta = "Los equipos que estan entre los mejores " + n + " son: ";
        String separador = "";
        List<EquipoPosicionado> topN = equiposPosicionados.subList(0,n);
        for (EquipoPosicionado ep : topN) {
            respuesta += separador + ep.getNombre();
            separador = ", ";
        }
        return respuesta;
    }

    public String getPosicionEquipo(String equipo) {
        if (this.equiposPosicionados.size() == 0) {
            getTopNEquipos(1);
        }
        String equipoVisual = "";
        for(Equipo eq : this.equiposDePrimera) {
            if (eq.getNombreReferencia().equalsIgnoreCase(equipo)) {
                equipoVisual = eq.getNombre();
            }
        }
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

    public String getEquipoEnPosicion(int posicion) {
        if (this.equiposPosicionados.size() == 0) {
            getTopNEquipos(1);
        }
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
        String equipoVisual = "";
        for(Equipo eq : this.equiposDePrimera) {
            if (eq.getNombreReferencia().equalsIgnoreCase(equipo)) {
                pagina = eq.getPagina();
                equipoVisual = eq.getNombre();
            }
        }
        if (!pagina.equals("")) {
            DatosEquipo datos = new DatosEquipo();
            try {
                datos = new GetDatosAsyncTask(pagina).execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return equipoVisual + " es un equipo de primera división argentina ubicado en " + datos.getUbicacion() + ". Su fundación fue el " + datos.getFundacion() +
                    ". Su apodo es " + datos.getApodos() + ". Su estadio se llama " + datos.getEstadio() + " con capacidad para " + datos.getCapacidad() +
                    " y actualmente está siendo dirigido por " + datos.getDt();
        } else
            return "El equipo solicitado no pudo ser encontrado";
    }
}
