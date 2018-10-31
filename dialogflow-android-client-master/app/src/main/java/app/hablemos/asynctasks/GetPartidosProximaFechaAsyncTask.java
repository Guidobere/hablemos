package app.hablemos.asyncTasks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.hablemos.model.football.PartidoActual;
import app.hablemos.util.FootballUtil;

public class GetPartidosProximaFechaAsyncTask extends AsyncTask<Void, Void, List<PartidoActual>> {

    private String fecha;

    public GetPartidosProximaFechaAsyncTask(String fecha) {
        this.fecha = fecha;
    }

    @Override
    protected List<PartidoActual> doInBackground(Void... params) {
        List<PartidoActual> partidosActuales = new ArrayList<>();
        try {
            URL url = new URL("http://www.saf.com.ar/rest/fechas/" + fecha + "/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder json = new StringBuilder("{\"partidos\":");
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();
            json.append("}");

            JSONObject jsnobject = new JSONObject(json.toString());
            JSONArray jsonArray = jsnobject.getJSONArray("partidos");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject explrObject = jsonArray.getJSONObject(i);
                String fecha_corta = explrObject.getString("fecha_corta").split("/")[1].trim();
                JSONArray equipos = explrObject.getJSONArray("equipos");
                PartidoActual partidoActual = new PartidoActual();
                for (int j = 0; j < 2; j++) {
                    JSONObject equipo = equipos.getJSONObject(j);
                    String abreviacion = equipo.getString("abreviacion");
                    if (j == 0) {
                        partidoActual.setEquipoLocal(FootballUtil.convertirSafReferencia(abreviacion));
                    } else {
                        partidoActual.setEquipoVisitante(FootballUtil.convertirSafReferencia(abreviacion));
                    }
                }
                partidoActual.setHoraJuego(fecha_corta);
                partidosActuales.add(partidoActual);
            }
        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage());
            return new ArrayList<>();
        }
        return partidosActuales;
    }
}