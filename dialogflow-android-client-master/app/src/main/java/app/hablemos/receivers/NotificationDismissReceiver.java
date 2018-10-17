package app.hablemos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import app.hablemos.R;
import app.hablemos.services.NotificationService;

public class NotificationDismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Se obtienen datos de la notificación
        String mailQueInicioSesion = intent.getExtras().getString("1");
        int tipoNotificacion = intent.getExtras().getInt("tipoNotificacion");

        //Se registra que el usuario cerró la notificación
        NotificationService notificationService = new NotificationService(mailQueInicioSesion);
        notificationService.registrarCerrarNotificacion(context, tipoNotificacion);
    }
}
