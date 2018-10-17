package app.hablemos.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;


import app.hablemos.R;
import app.hablemos.activities.MainActivity;
import app.hablemos.model.Function;



public class ClimaService {

     private MainActivity elmain = new MainActivity();

     public void sendClimaNotifications(Context context, final String nombreAbuelo, String mailQueInicioSesion) {
        final Context localContext = context;
        final NotificationService notificationService = new NotificationService(mailQueInicioSesion);
        final int horaActual = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        final int horarioClima = Integer.parseInt(context.getString(R.string.horarioClima));

        if(horaActual == horarioClima) {

         //  if(elmain.estaLindo()) {
                notificationService.enviarNotificacionClima(localContext, "mañana");
        //    }

        }

        }

    }

