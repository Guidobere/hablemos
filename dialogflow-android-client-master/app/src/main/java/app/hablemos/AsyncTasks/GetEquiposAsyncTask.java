package app.hablemos.asyncTasks;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import app.hablemos.model.football.Equipo;

public class GetEquiposAsyncTask extends AsyncTask<Void, Void, List<Equipo>> {

    @Override
    protected List<Equipo> doInBackground(Void... voids) {
        Document document = null;
        try {
            document = Jsoup.connect("http://www.promiedos.com.ar/primera").get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Equipo> eqs = new ArrayList<>();
        if(document != null) {
            Elements equipos = document.select(".expand-down ul li");
            for (int i = 0; i < equipos.size(); i++) {
                String nombre = document.select(".expand-down ul li a img").get(i).attributes().get("onmouseover").replace("equipo('", "").replace("');", "");
                String pagina = document.select(".expand-down ul li a").get(i).attributes().get("href");
                eqs.add(new Equipo(nombre, pagina.replace(".html", ""), "http://www.promiedos.com.ar/" + pagina));
            }
        }
        return eqs;
    }
}