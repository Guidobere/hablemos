package app.hablemos.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.res.ResourcesCompat;

import app.hablemos.R;
import app.hablemos.activities.MainActivity;

public class NotificationService {
    private static final String CANAL_NOTIFICACION = "1";
    private static final int TIPO_SONIDO = RingtoneManager.TYPE_NOTIFICATION;
    private static final int ID_NOTIFICACION_AVISO_MAIL = 1;
    private String mailQueInicioSesion;

    public NotificationService(String mailQueInicioSesion){
        this.mailQueInicioSesion = mailQueInicioSesion;
    }

    public void enviarNotificacionAvisoMail(Context context, String nombreAbuelo) {
        String notificationTitle = "Hola " + nombreAbuelo + "!!";
        String notificationText = context.getString(R.string.notificacionAvisoMail);
        enviarNotificacion(context, notificationTitle, notificationText);
    }

    private void enviarNotificacion(Context context, String notificationTitle, String notificationText) {
        //Icono chico
        int smallIcon = R.mipmap.notification_icon;

        //Icono grande
        Resources resources = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.notification_logo);
        int height = (int) resources.getDimension(android.R.dimen.notification_large_icon_height);
        int width = (int) resources.getDimension(android.R.dimen.notification_large_icon_width);
        largeIcon = Bitmap.createScaledBitmap(largeIcon, width, height, false);

        //Sonido
        Uri alarmSound = RingtoneManager.getDefaultUri(TIPO_SONIDO);

        //Intent para ejecutar cuando toca la notificación
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle mBundle = new Bundle();
        mBundle.putString("1", mailQueInicioSesion);
        mBundle.putBoolean("vieneDeNotificacion", true);
        intent.putExtras(mBundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Color de logo y título
        int color = ResourcesCompat.getColor(context.getResources(), R.color.colorPrimary, null);

        //Notificación
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CANAL_NOTIFICACION)
            .setSmallIcon(smallIcon)
            .setLargeIcon(largeIcon)
            .setColor(Color.argb(255, Color.red(color), Color.green(color), Color.blue(color)))
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(notificationText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(alarmSound)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) //Para que se cierra al tocar
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC); //Visibilidad con la pantalla bloqueada

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(ID_NOTIFICACION_AVISO_MAIL, notificationBuilder.build());
    }
}
