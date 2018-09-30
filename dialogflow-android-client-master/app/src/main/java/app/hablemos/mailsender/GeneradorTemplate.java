package app.hablemos.mailsender;

import android.content.res.AssetManager;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import app.hablemos.model.Interaccion;

public class GeneradorTemplate {

    public void generarYEnviarMail(String nombreAbuelo, List<Interaccion> interacciones, AssetManager assetManager) {
        final String bodyFinal = generarBodyHTML(nombreAbuelo, interacciones, assetManager);
        new Thread(new Runnable() {
            public void run() {
                try {
                    GmailSender sender = new GmailSender("hablemosproyectofinal@gmail.com", "aprobamoscomosea");
                    //TODO: poner el mail del tutor y el contenido del mail
                    sender.sendMail("Reporte del " + getDate(), bodyFinal, "Hablemos!", "mail tutor");
                } catch (Exception e) {
                    //TODO: logguear correspondientemente
                }
            }
        }).start();
    }

    private String generarBodyHTML(String nombreAbuelo, List<Interaccion> interacciones, AssetManager assetManager) {
        String body = "";
        try {
            InputStream is = assetManager.open("templateReportesTablaUnica.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            body = new String(buffer);
        } catch (Exception e){
            e.printStackTrace();
        }
        String bodyFinal = body;
        //reemplazo nombre abuelo
        bodyFinal = bodyFinal.replace("@Abuelo@", nombreAbuelo);
        bodyFinal = bodyFinal.replace("@ListaInteracciones@", generarListaInteraccionesHTML(interacciones));
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
}
