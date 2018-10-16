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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.hablemos.R;
import app.hablemos.activities.MainActivity;
import app.hablemos.receivers.NotificationDismissReceiver;

public class NotificationService {
    private static final String CANAL_NOTIFICACION = "1";
    private static final int TIPO_SONIDO_DEFAULT = RingtoneManager.TYPE_NOTIFICATION;

    public static final int ID_NOTIFICACION_AVISO_MAIL = 1;
    public static final int ID_NOTIFICACION_SALUD = 2;

    private String mailQueInicioSesion;

    private DatabaseReference fbRefInteracciones;

    public NotificationService(String mailQueInicioSesion){
        this.mailQueInicioSesion = mailQueInicioSesion;
        this.fbRefInteracciones = FirebaseDatabase.getInstance().getReference().child("interacciones");

    }

    public void enviarNotificacionAvisoMail(Context context, String nombreAbuelo) {
        String notificationTitle = "Hola " + nombreAbuelo + "!!";
        String notificationText = context.getString(R.string.notificacionAvisoMail);
        enviarNotificacion(context, notificationTitle, notificationText,
            null, ID_NOTIFICACION_AVISO_MAIL, "");
    }

    public void enviarNotificacionMedicamentos(Context context, String nombreAbuelo, String turno) {
        InteractionsService interactionsService = new InteractionsService(context, context.getAssets(), fbRefInteracciones);
        String notificationTitle = nombreAbuelo + ", hora de tus remedios de la " + turno;
        String notificationText = context.getString(R.string.notificacionTexto_Salud);

        enviarNotificacion(context, notificationTitle, notificationText,
            RingtoneManager.TYPE_ALARM, ID_NOTIFICACION_SALUD, turno);

        interactionsService.guardarInteraccion(
            mailQueInicioSesion, context.getString(R.string.interaccionTitulo_NotificacionSalud),
                "-", context.getString(R.string.interaccionTexto_EnviarNotificacionSalud));
    }

    private void enviarNotificacion(Context context, String notificationTitle, String notificationText,
                                    Integer tipoSonido, Integer tipoNotificacion, String extraInfo) {
        //Icono chico
        int smallIcon = R.mipmap.notification_icon;

        //Icono grande
        Resources resources = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.notification_logo);
        int height = (int) resources.getDimension(android.R.dimen.notification_large_icon_height);
        int width = (int) resources.getDimension(android.R.dimen.notification_large_icon_width);
        largeIcon = Bitmap.createScaledBitmap(largeIcon, width, height, false);

        //Sonido
        Uri alarmSound = RingtoneManager.getDefaultUri(tipoSonido!=null?tipoSonido: TIPO_SONIDO_DEFAULT);

        //Intent para ejecutar cuando toca la notificación
        Intent intentAbrir = new Intent(context, MainActivity.class);
        intentAbrir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intentAbrir.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); Con esto se resumia el estado anterior
        Bundle mBundle = new Bundle();
        mBundle.putString("1", mailQueInicioSesion);
        mBundle.putBoolean("vieneDeNotificacion", true);
        mBundle.putInt("tipoNotificacion", tipoNotificacion);
        mBundle.putString("extraInfo", extraInfo);

        intentAbrir.putExtras(mBundle);
        PendingIntent pendingIntentAbrir = PendingIntent.getActivity(
            context, 0, intentAbrir, PendingIntent.FLAG_ONE_SHOT);
            //context, 0, intentAbrir, PendingIntent.FLAG_UPDATE_CURRENT); Con esto se resumia el estado anterior

        //Intent para ejecutar cuando borra la notificación
        Intent intentCerrar = new Intent(context, NotificationDismissReceiver.class);
        intentCerrar.putExtras(mBundle);
        PendingIntent pendingIntentCerrar = PendingIntent.getBroadcast(context, 1, intentCerrar, 0);

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
            .setContentIntent(pendingIntentAbrir) //Se ejecuta cuando el usuario toca la notificación
            .setDeleteIntent(pendingIntentCerrar) //Se ejecuta cuando el usuario borra la notificación
            .setTimeoutAfter(Integer.valueOf(context.getString(R.string.minutosTimeoutNotificacion)))
            .setAutoCancel(true) //Para que se cierre cuando el usuario toca la notificación (y va a la app)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC); //Visibilidad con la pantalla bloqueada

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(tipoNotificacion, notificationBuilder.build());
    }

    public void registrarCerrarNotificacion(Context context, String titulo, String observacion, String mailQueInicioSesion){
        InteractionsService interactionsService = new InteractionsService(context, context.getAssets(), fbRefInteracciones);
        interactionsService.guardarInteraccion(mailQueInicioSesion, titulo, "No", observacion);
    }
}
