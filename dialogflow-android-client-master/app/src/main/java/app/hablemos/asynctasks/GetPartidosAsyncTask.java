package app.hablemos.asyncTasks;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import app.hablemos.model.football.Partido;

public class GetPartidosAsyncTask extends AsyncTask<Void, Void, List<Partido>> {

    private String pagina;

    public GetPartidosAsyncTask(String pagina) {
        this.pagina = pagina;
    }

    @Override
    protected List<Partido> doInBackground(Void... voids) {
        Document document = null;
        try {
            document = Jsoup.connect(pagina).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Partido> partidos = new ArrayList<>();
        if(document != null) {
            Elements rows = document.select("#tablapartidos tbody tr");
            for (int i = 1; i < rows.size(); i++) {
                String fecha = rows.get(i).children().get(0).text();
                String rival = rows.get(i).children().get(1).text();
                String localia = rows.get(i).children().get(2).text();
                String resultado = rows.get(i).children().get(3).text();
                String dia = rows.get(i).children().get(4).text();
                partidos.add(new Partido(fecha, rival, localia, resultado, dia));
            }
        }
        return partidos;
    }
}