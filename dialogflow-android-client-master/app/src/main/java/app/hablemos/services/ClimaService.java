package app.hablemos.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Calendar;

import app.hablemos.R;

import static android.support.v4.content.ContextCompat.getSystemService;


public class ClimaService {

    private String city = "Buenos Aires"; //Asi lo muestra la app cuando pedis en la web

    /* Please Put your API KEY here */
    private String OPEN_WEATHER_MAP_API = "ea574594b9d36ab688642d5fbeab847e";
    /* Please Put your API KEY here */

    JSONObject data = null;
    JSONObject main = null;
    JSONObject details;
    String mensaje;
    String id;
    int a=0;

    double temperatura;

    public void getJSON(final String city, final Context localContext , final NotificationService notificationService) {

        new AsyncTask<Void, Void, Void>() {


            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+city+"&units=metric&APPID=ea574594b9d36ab688642d5fbeab847e");

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuffer json = new StringBuffer(1024);

                    String tmp = "";

                    while((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();

                    data = new JSONObject(json.toString());
                    main = data.getJSONObject("main");
                    mensaje = main.getString("temp");
                    details = data.getJSONArray("weather").getJSONObject(0);

                    id = details.getString("id");
                    int numeroID = Integer.parseInt(id);
                    Double numero = Double.parseDouble(mensaje);

                    if(numero>1){ //4 && numeroID>799
                        notificationService.enviarNotificacionClima(localContext, "mañana");
                    }

                    if(data.getInt("cod") != 200) {
                        System.out.println("Cancelled");
                        return null;
                    }


                } catch (Exception e) {

                    System.out.println("Exception "+ e.getMessage());
                    return null;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void Void) {
                if(data!=null){
                    Log.d("my weather received", mensaje);
                    Log.d("holi", id);
                }

            }
        }.execute();

    }

    public void sendClimaNotifications(Context context, final String nombreAbuelo, String mailQueInicioSesion) {
        final Context localContext = context;
        final NotificationService notificationService = new NotificationService(mailQueInicioSesion);
        final int horaActual = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        final int horarioClima = Integer.parseInt(context.getString(R.string.horarioClima));

        if(horaActual == horarioClima) {

            getJSON(city,localContext,notificationService);

        }

    }

}