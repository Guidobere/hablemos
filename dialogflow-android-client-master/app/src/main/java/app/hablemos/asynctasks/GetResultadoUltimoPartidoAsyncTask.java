package app.hablemos.asynctasks;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import app.hablemos.model.football.PartidoActual;

public class GetResultadoUltimoPartidoAsyncTask extends AsyncTask<Void, Void, List<PartidoActual>> {

    private String fecha;

    public GetResultadoUltimoPartidoAsyncTask(String fecha) {
        this.fecha = fecha;
    }

    @Override
    protected List<PartidoActual> doInBackground(Void... voids) {
        Document document = null;
        try {
            document = Jsoup.connect("http://www.promiedos.com.ar/fecha.php?fecha=" + fecha + "_1").get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<PartidoActual> partidos = new ArrayList<>();
        if(document != null) {
            Elements tableRows = document.select("table tbody").get(0).children();
                for (int i = 1; i < tableRows.size(); i+=3) {
                    if (tableRows.get(i).attributes().get("style").contains("background:#092B1D")) {
                        i++;
                    }
                    PartidoActual partidoActual = new PartidoActual();
                    String estadoPartido = tableRows.get(i).children().get(0).className().trim();
                    String equipoLocal = tableRows.get(i).children().get(1).text().trim();
                    String equipoVisitante = tableRows.get(i).children().get(4).text().trim();
                    partidoActual.setEstado(estadoPartido);
                    partidoActual.setEquipoLocal(equipoLocal);
                    partidoActual.setEquipoVisitante(equipoVisitante);
                    if (estadoPartido.equalsIgnoreCase("finaliza") ||
                            estadoPartido.equalsIgnoreCase("jugandose")) {
                        int golesEquipoLocal = -1;
                        int golesEquipoVisitante = -1;
                        try {
                            golesEquipoLocal = Integer.parseInt(tableRows.get(i).children().get(2).text().trim());
                            golesEquipoVisitante = Integer.parseInt(tableRows.get(i).children().get(3).text().trim());
                        } catch (Exception ignored){}
                        partidoActual.setGolesEquipoLocal(golesEquipoLocal);
                        partidoActual.setGolesEquipoVisitante(golesEquipoVisitante);
                        if (golesEquipoLocal > 0) {
                            String golesLocal = tableRows.get(i+2).children().get(0).text().trim();
                            partidoActual.setGolesLocal(golesLocal);
                        }
                        if (golesEquipoVisitante > 0) {
                            String golesVisitante = tableRows.get(i+2).children().get(1).text().trim();
                            partidoActual.setGolesVisitante(golesVisitante);
                        }
                        if (estadoPartido.equalsIgnoreCase("jugandose")) {
                            String tiempoJuego = tableRows.get(i).children().get(0).text().trim();
                            partidoActual.setTiempoJuego(tiempoJuego);
                        }
                    } else if (estadoPartido.equalsIgnoreCase("falta")) {
                        String horaJuego = tableRows.get(i).children().get(0).text().trim();
                        partidoActual.setHoraJuego(horaJuego);
                    }
                    partidos.add(partidoActual);
                }
        }
        return partidos;
    }
}