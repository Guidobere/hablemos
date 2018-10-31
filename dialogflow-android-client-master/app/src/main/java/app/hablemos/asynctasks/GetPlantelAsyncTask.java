package app.hablemos.asynctasks;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import app.hablemos.model.football.DatosEquipo;

public class GetPlantelAsyncTask extends AsyncTask<Void, Void, List<String>> {

    private String pagina;

    public GetPlantelAsyncTask(String pagina) {
        this.pagina = pagina;
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        Document document = null;
        try {
            document = Jsoup.connect(this.pagina).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> jugadores = new ArrayList<>();
        if (document != null) {
            Elements nombres = document.select("#plantel tbody tr td.nom");
            for (int i = 0; i < nombres.size() - 1; i++) { //es -1 porque el ultimo es el DT y no lo necesitamos
                String nombre = nombres.get(i).text().trim();
                jugadores.add(nombre);
            }
        }
        return jugadores;
    }
}