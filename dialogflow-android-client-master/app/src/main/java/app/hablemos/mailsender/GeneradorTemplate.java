package app.hablemos.mailsender;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import app.hablemos.R;
import app.hablemos.model.Interaccion;

public class GeneradorTemplate {

    private final Context context;
    private final AssetManager assetManager;

    public GeneradorTemplate(Context context, AssetManager assetManager) {
        this.context = context;
        this.assetManager = assetManager;
    }

    public void generarYEnviarMail(String nombreAbuelo, final String emailsDestino, List<Interaccion> interacciones) {
        final String bodyFinal = generarBodyHTML(nombreAbuelo, interacciones, assetManager);
        new Thread(new Runnable() {
            public void run() {
                try {
                    GmailSender sender = new GmailSender(getContext().getString(R.string.mail), getContext().getString(R.string.pass));
                    sender.sendMail("Reporte del " + getDate(), bodyFinal, getContext().getString(R.string.remitente), emailsDestino);
                } catch (Exception e) {
                    //TODO: logguear correspondientemente
                }
            }
        }).start();
    }

    private String generarBodyHTML(String nombreAbuelo, List<Interaccion> interacciones, AssetManager assetManager) {
        String body = "";
        try {
            InputStream is = assetManager.open(getContext().getString(R.string.nombreReporte));
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            body = new String(buffer);
        } catch (Exception e){
            e.printStackTrace();
        }
        String bodyFinal = body;

        //reemplazo nombre abuelo, interacciones y el logo
        bodyFinal = bodyFinal.replace("@Abuelo@", nombreAbuelo);
        bodyFinal = bodyFinal.replace("@ListaInteracciones@", generarListaInteraccionesHTML(interacciones));
        bodyFinal = bodyFinal.replace("@urlImagen@", getContext().getString(R.string.urlImagen));
        return bodyFinal;
    }

    private String generarListaInteraccionesHTML(List<Interaccion> interacciones) {
        String bodyTablaInteracciones = "";
        for (Interaccion interaccion : interacciones) {
            bodyTablaInteracciones += "<tr>";
            bodyTablaInteracciones += "<td>" + interaccion.getHora() + "</td>";
            bodyTablaInteracciones += "<td>" + interaccion.getTipo() + "</td>";
            bodyTablaInteracciones += "<td>" + interaccion.getRespuesta() + "</td>";
            bodyTablaInteracciones += "<td>" + interaccion.getObservaciones() + "</td>";
            bodyTablaInteracciones += "</tr>";
        }
        return bodyTablaInteracciones;
    }

    private String getDate() {
        Calendar rightNow = Calendar.getInstance();
        int dia = rightNow.get(Calendar.DAY_OF_MONTH);
        int mes = rightNow.get(Calendar.MONTH) + 1;
        int anio = rightNow.get(Calendar.YEAR);
        return "" + dia + "/" + mes + "/" + anio;
    }

    public Context getContext() {
        return context;
    }
}