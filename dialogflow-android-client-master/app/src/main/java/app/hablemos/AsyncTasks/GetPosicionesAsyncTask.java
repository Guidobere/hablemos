package app.hablemos.asynctasks;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import app.hablemos.model.EquipoPosicionado;

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
                eqs.add(new EquipoPosicionado(cells.get(1).text(), Integer.parseInt(cells.get(0).text()), Integer.parseInt(cells.get(2).text()),
                        Integer.parseInt(cells.get(3).text()), Integer.parseInt(cells.get(4).text()), Integer.parseInt(cells.get(5).text()),
                        Integer.parseInt(cells.get(6).text()), Integer.parseInt(cells.get(7).text()), Integer.parseInt(cells.get(8).text()),
                        Integer.parseInt(cells.get(9).text())));
            }
        }
        return eqs;
    }
}