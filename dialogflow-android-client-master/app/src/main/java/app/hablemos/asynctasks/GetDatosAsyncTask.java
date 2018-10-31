package app.hablemos.asynctasks;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import app.hablemos.model.football.DatosEquipo;

public class GetDatosAsyncTask extends AsyncTask<Void, Void, DatosEquipo> {

    private String pagina;

    public GetDatosAsyncTask(String pagina) {
        this.pagina = pagina;
    }

    @Override
    protected DatosEquipo doInBackground(Void... voids) {
        Document document = null;
        try {
            document = Jsoup.connect(this.pagina).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (document != null) {
            String fundacion = document.select(".datosequipo").get(0).text().replace("Fundación: ", ""); //$(".datosequipo")[0].innerText
            String apodos = document.select(".datosequipo").get(1).text().replace("Apodo: ", ""); //$(".datosequipo")[1].innerText
            String ubicacion = document.select(".datosequipo").get(2).text().replace("Club de ", ""); //$(".datosequipo")[2].innerText
            if (ubicacion.contains("Ciudad A.")) ubicacion = ubicacion.replace("Ciudad A.", "Ciudad Autónoma");
            String estadio = document.select("#estadio strong").get(1).text().replace("Estadio ", ""); //$("#estadio strong")[1].innerText
            String capacidad = document.select("#estadio").get(0).childNode(9).toString().replace("Capacidad:", "").trim();//$("#estadio")[0].childNodes[9].data.replace("Capacidad:", "").trim()
            String dt = document.select("#plantel tbody tr").last().children().get(1).text().trim(); //$("#plantel tbody tr:last")[0].children[1].innerText.trim()
            return new DatosEquipo(fundacion, apodos, ubicacion, estadio, capacidad, dt);
        } else
            return new DatosEquipo();
    }
}