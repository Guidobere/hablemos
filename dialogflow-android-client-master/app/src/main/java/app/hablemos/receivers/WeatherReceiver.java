package app.hablemos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;

import app.hablemos.services.WeatherService;

public class WeatherReceiver extends BroadcastReceiver {

    public WeatherReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //inicializo en false las variables por si falla, para que no le diga nada al abuelo
        Boolean isHot = false, isSuitableForOutdoor = false;
        try {
            isHot = WeatherService.isItHotToday();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            isSuitableForOutdoor = WeatherService.isSuitableForOutsideActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent i = new Intent("respuestasClima");
        i.putExtra("isHot", isHot);
        i.putExtra("isSuitableForOutdoor", isSuitableForOutdoor);
        context.sendBroadcast(i);
    }
}
