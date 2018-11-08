package app.hablemos.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import app.hablemos.R;


public class ClimaService {

    private String city = "Buenos Aires"; //Asi lo muestra la app cuando pedis en la web

    private JSONObject data = null;
    private JSONObject main = null;
    private JSONObject details;
    private String mensaje;
    private String id;

    double temperatura;

    private void sendNotifications(final Context localContext, final NotificationService notificationService, final Double latitud, final Double longitud, final String city) {

        AsyncTask<Void, Void, Void> execute = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url;
                    if (latitud != null && longitud != null)
                        url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + latitud + "&lon=" + longitud + "&units=metric&APPID=" + localContext.getString(R.string.climaApiKey));
                    else
                        url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&APPID=" + localContext.getString(R.string.climaApiKey));

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuffer json = new StringBuffer(1024);

                    String tmp = "";

                    while ((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();

                    data = new JSONObject(json.toString());
                    main = data.getJSONObject("main");
                    mensaje = main.getString("temp");
                    details = data.getJSONArray("weather").getJSONObject(0);

                    id = details.getString("id");
                    int numeroID = Integer.parseInt(id);
                    Double numero = Double.parseDouble(mensaje);

                    if (numero > 32 && numeroID > 799) { //
                        notificationService.enviarNotificacionMuchoCalor(localContext, "mañana");
                        return null;
                    }

                    if (numero > 21 && numeroID > 799) { //
                        notificationService.enviarNotificacionClimaLindo(localContext, "mañana");
                        return null;
                    }

                    if (numeroID > 299) {
                        notificationService.enviarNotificacionClimaParaguas(localContext, "mañana");
                        return null;
                    }

                    if (numeroID > 199) {
                        notificationService.enviarNotificacionClimaHorrible(localContext, "mañana");
                        return null;
                    }

                    if (data.getInt("cod") != 200) {
                        System.out.println("Cancelled");
                        return null;
                    }


                } catch (Exception e) {

                    System.out.println("Exception " + e.getMessage());
                    return null;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void Void) {
                if (data != null) {
                    Log.d("my weather received", mensaje);
                    Log.d("holi", id);
                }

            }
        };
        execute.execute();

    }

    public void sendClimaNotifications(final Context context, final String nombreAbuelo, String mailQueInicioSesion) {
        final NotificationService notificationService = new NotificationService(mailQueInicioSesion);

        //Si hay permiso, obtener ubicación y luego el clima para enviar las notificaciones (sino usa Buenos Aires)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

            mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        sendNotifications(context, notificationService,
                            location!=null?location.getLatitude():null, location!=null?location.getLongitude():null, city);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sendNotifications(context, notificationService, null, null, city);
                    }
                });
        } else {
            sendNotifications(context, notificationService, null, null, city);
        }



    }

}