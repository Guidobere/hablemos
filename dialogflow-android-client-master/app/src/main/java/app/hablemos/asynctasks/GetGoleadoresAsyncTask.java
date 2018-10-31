package app.hablemos.asynctasks;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class GetGoleadoresAsyncTask extends AsyncTask<Void, Void, TreeMap<Integer,List<String>>> {

    @Override
    protected TreeMap<Integer,List<String>> doInBackground(Void... voids) {
        Document document = null;
        try {
            document = Jsoup.connect("http://www.promiedos.com.ar/primera").get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<Integer,List<String>> goleadores = new HashMap<>();
        if (document != null) {
            Elements rows = document.select("#goleadores table tbody tr");//$("#goleadores table tbody tr")
            for (int i = 1; i < rows.size(); i++) {
                String jugador = rows.get(i).children().get(0).text();
                int goles = Integer.parseInt(rows.get(i).children().get(1).text());
                if (!goleadores.containsKey(goles)) {
                    goleadores.put(goles, new ArrayList<String>());
                }
                goleadores.get(goles).add(jugador);
            }
        }
        TreeMap<Integer, List<String>> goleadoresOrdenado = new TreeMap<>(Collections.reverseOrder());
        goleadoresOrdenado.putAll(goleadores);
        return goleadoresOrdenado;
    }
}