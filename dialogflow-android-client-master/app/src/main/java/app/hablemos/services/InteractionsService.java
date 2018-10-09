package app.hablemos.services;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.hablemos.R;
import app.hablemos.mailsender.GmailSender;
import app.hablemos.model.Interaccion;

public class InteractionsService {

    private final Context context;
    private final AssetManager assetManager;
    private DatabaseReference fbRefInteracciones;

    public InteractionsService(Context context, AssetManager assetManager, DatabaseReference fbRefInteracciones) {
        this.context = context;
        this.assetManager = assetManager;
        this.fbRefInteracciones = fbRefInteracciones;
    }

    public void enviarReporteInteracciones(final String nombreAbuelo, final String mailRegistro){
        try {
            //Obtener interacciones de Firebase
            fbRefInteracciones.orderByChild("emailDia").equalTo(getKey(mailRegistro)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot interaccionesFb) {
                    List<Interaccion> interacciones = new ArrayList<>();
                    Iterable<DataSnapshot> usersChildren = interaccionesFb.getChildren();
                    for (DataSnapshot inte : usersChildren) {
                        interacciones.add(inte.getValue(Interaccion.class));
                    }

                    if (interacciones != null) {
                        Log.d(this.getClass().getName(), "Interacciones obtenidas" + interacciones.size());
                        generarYEnviarMail(nombreAbuelo, mailRegistro, interacciones);
                    } else {
                        Log.w(this.getClass().getName(), "Ocurrio un error al obtener las interacciones (resultado nulo).");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al obtener interacciones.", e);
        }
    }

    private void generarYEnviarMail(String nombreAbuelo, final String emailsDestino, List<Interaccion> interacciones) {
        final String bodyFinal = generarBodyHTML(nombreAbuelo, interacciones, assetManager);
        new Thread(new Runnable() {
            public void run() {
                try {
                    GmailSender sender = new GmailSender(getContext().getString(R.string.mail), getContext().getString(R.string.pass));
                    sender.sendMail("Reporte del " + getDate(), bodyFinal, getContext().getString(R.string.remitente), emailsDestino);
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), "Error al enviar reporte.", e);
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

    private String getKey(String email){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fecha = sdf.format(new Date());
        return email+"-"+fecha;
    }

    public void guardarInteraccion(String email, String tipo, String respuesta, String observaciones){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String hora = sdf.format(new Date());
            fbRefInteracciones.push().setValue(
                new Interaccion(getKey(email),hora, tipo, respuesta, observaciones));
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al guardar interacción.", e);
        }
    }
}