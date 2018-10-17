package app.hablemos.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.hablemos.AsyncTasks.GetEquiposAsyncTask;
import app.hablemos.AsyncTasks.GetPosicionesAsyncTask;
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

    public List<EquipoPosicionado> getTopNEquipos(int n) {
        if (this.equiposPosicionados.size() == 0) {
            try {
                this.equiposPosicionados = new GetPosicionesAsyncTask().execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return equiposPosicionados.subList(0,n);
    }

    public int getPosicionEquipo(String equipo) {
        if (this.equiposPosicionados.size() == 0) {
            getTopNEquipos(1);
        }
        int posicion = 0;
        for(EquipoPosicionado ep : this.equiposPosicionados) {
            if (ep.getNombre().equalsIgnoreCase(mapaEquipos.get(equipo))){
                posicion = ep.getPosicion();
            }
        }
        return posicion;
        //Si retorna 0 es porque no lo encontró
    }
}
