package app.hablemos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import app.hablemos.services.ClimaService;

public class ClimaReceiver  extends BroadcastReceiver {

    ClimaService climaService;

    public ClimaReceiver() {
        climaService = new ClimaService();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String nombreAbuelo = intent.getExtras().getString("nombreAbuelo");
        String mailQueInicioSesion = intent.getExtras().getString("mailQueInicioSesion");
        climaService.sendClimaNotifications(context, nombreAbuelo, mailQueInicioSesion);
    }
}
