package app.hablemos.asynctasks;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.hablemos.model.football.EquipoPosicionado;

public class GetPosicionesAsyncTask extends AsyncTask<Void, Void, List<EquipoPosicionado>> {

    @Override
    protected List<EquipoPosicionado> doInBackground(Void... voids) {
        Document document = null;
        try {
            document = Jsoup.connect("http://www.promiedos.com.ar/primera").get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<EquipoPosicionado> eqs = new ArrayList<>();
        if(document != null) {
            Elements rows = document.select("#posiciones tbody tr");
            for (int i = 0; i < rows.size(); i++) {
                Elements cells = rows.get(i).children();
                String nombre = cells.get(1).text();
                int posicion = Integer.parseInt(cells.get(0).text());
                int puntos = Integer.parseInt(cells.get(2).text());
                int jugados = Integer.parseInt(cells.get(3).text());
                int ganados = Integer.parseInt(cells.get(4).text());
                int empatados = Integer.parseInt(cells.get(5).text());
                int perdidos = Integer.parseInt(cells.get(6).text());
                int aFavor = Integer.parseInt(cells.get(7).text());
                int enContra = Integer.parseInt(cells.get(8).text());
                int diferencia = Integer.parseInt(cells.get(9).text());
                EquipoPosicionado equipoPosicionado = new EquipoPosicionado(nombre, posicion,
                        puntos, jugados, ganados, empatados, perdidos, aFavor, enContra, diferencia);
                if (cells.get(0).attributes().get("style").contains("background: #7dbc6d")) {
                    equipoPosicionado.setLibertadores(true);
                } else if (cells.get(0).attributes().get("style").contains("background: yellow")) {
                    equipoPosicionado.setSudamericana(true);
                }
                eqs.add(equipoPosicionado);
            }
            HashMap<String, Float> descienden = new HashMap<>();
            Elements rowsPromedios = document.select("#tablapromactual tbody tr");
            for (int j = 0; j < rowsPromedios.size(); j++) {
                if (rowsPromedios.get(j).attributes().get("style").contains("background: #fb817b")) {
                    String nombreEquipo = rowsPromedios.get(j).children().get(1).text();
                    float promedio = Float.parseFloat(rowsPromedios.get(j).children().get(7).text());
                    descienden.put(nombreEquipo, promedio);
                }
            }
            for (EquipoPosicionado equipoPosicionado : eqs) {
                if (descienden.keySet().contains(equipoPosicionado.getNombre())) {
                    equipoPosicionado.setDesciende(true);
                    equipoPosicionado.setPromedio(descienden.get(equipoPosicionado.getNombre()));
                }
            }
        }
        return eqs;
    }
}