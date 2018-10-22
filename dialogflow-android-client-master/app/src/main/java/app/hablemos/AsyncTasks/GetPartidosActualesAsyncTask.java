package app.hablemos.asynctasks;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import app.hablemos.model.Partido;
import app.hablemos.model.PartidoActual;

public class GetPartidosActualesAsyncTask extends AsyncTask<Void, Void, List<PartidoActual>> {

    @Override
    protected List<PartidoActual> doInBackground(Void... voids) {
        Document document = null;
        try {
            document = Jsoup.connect("http://www.promiedos.com.ar/").get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<PartidoActual> partidos = new ArrayList<>();
        if(document != null) {
            Elements blocks = document.select("#fixturein > div.tituloin");
            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i).text().trim().equalsIgnoreCase("superliga")) {
                    Elements tableRows = blocks.get(i).nextElementSibling().children().get(0).children();
                    for (int j = 0; j < tableRows.size(); j+=2) {
                        PartidoActual partidoActual = new PartidoActual();
                        String estadoPartido = tableRows.get(j).children().get(0).className().trim();
                        String equipoLocal = tableRows.get(j).children().get(1).text().trim();
                        String equipoVisitante = tableRows.get(j).children().get(4).text().trim();
                        partidoActual.setEstado(estadoPartido);
                        partidoActual.setEquipoLocal(equipoLocal);
                        partidoActual.setEquipoVisitante(equipoVisitante);
                        if (estadoPartido.equalsIgnoreCase("finaliza") ||
                                estadoPartido.equalsIgnoreCase("jugandose")) {
                            int golesEquipoLocal = Integer.parseInt(tableRows.get(j).children().get(2).text().trim());
                            int golesEquipoVisitante = Integer.parseInt(tableRows.get(j).children().get(3).text().trim());
                            partidoActual.setGolesEquipoLocal(golesEquipoLocal);
                            partidoActual.setGolesEquipoVisitante(golesEquipoVisitante);
                            if (golesEquipoLocal > 0) {
                                String golesLocal = tableRows.get(j+1).children().get(0).text().trim();
                                partidoActual.setGolesLocal(golesLocal);
                            }
                            if (golesEquipoVisitante > 0) {
                                String golesVisitante = tableRows.get(j+1).children().get(1).text().trim();
                                partidoActual.setGolesVisitante(golesVisitante);
                            }
                            if (estadoPartido.equalsIgnoreCase("jugandose")) {
                                String tiempoJuego = tableRows.get(j).children().get(0).text().trim();
                                partidoActual.setTiempoJuego(tiempoJuego);
                            }
                        } else if (estadoPartido.equalsIgnoreCase("falta")) {
                            String horaJuego = tableRows.get(j).children().get(0).text().trim();
                            partidoActual.setHoraJuego(horaJuego);
                        }
                        partidos.add(partidoActual);
                    }
                    i = blocks.size();
                }
            }
        }
        return partidos;
    }
}