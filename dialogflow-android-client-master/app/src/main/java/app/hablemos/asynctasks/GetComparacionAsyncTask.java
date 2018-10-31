package app.hablemos.asynctasks;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class GetComparacionAsyncTask extends AsyncTask<Void, Void, String> {

    private String equipo1;
    private String equipo2;

    public GetComparacionAsyncTask(String equipo1, String equipo2) {
        this.equipo1 = equipo1;
        this.equipo2 = equipo2;
    }

    @Override
    protected String doInBackground(Void... voids) {
        Document document = null;
        try {
            document = Jsoup.connect("http://www.promiedos.com.ar/historialpartidos.php?equipo1=" + equipo1 + "&equipo2=" + equipo2).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (document != null) {
            String result = document.select("#infoequipo").get(0).text().replace("Historial de " + equipo1, "").replace("Historial de " + equipo2, "").trim(); //$("#infoequipo")[0].innerText.replace("Historial de " + eq1, "").replace("Historial de " + eq2, "").trim();
            if (result.contains("Jugaron 0 veces")) {
                return "No existe historial entre " + equipo1 + " y " + equipo2;
            }
            return result.replace("en Primera", "entre sí.");
        } else
            return "";
    }
}