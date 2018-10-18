package app.hablemos.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;

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
    public static final int ID_NOTIFICACION_CLIMA = 3;

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

    public void enviarNotificacionClima(Context context, String turno) {
        String notificationTitle = "Que lindo dia";
        String notificationText = context.getString(R.string.notificacionTexto_Clima);
        enviarNotificacion(context, notificationTitle, notificationText,
                RingtoneManager.TYPE_ALARM, ID_NOTIFICACION_CLIMA, turno);
    }

    private void enviarNotificacion(Context context, String notificationTitle, String notificationText,
                                    Integer tipoSonido, Integer tipoNotificacion, String extraInfo) {

        // Se verifica si la notificación anterior del mismo tipo sigue visible.
        // En ese caso se cancela y se registra en BD
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        verificarNotificacionAnterior(context, tipoNotificacion, notificationManager);

        //Icono chico
        int smallIcon = R.mipmap.notification_icon;

        //Icono grande
        Resources resources = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.notification_logo);
        int height = (int) resources.getDimension(android.R.dimen.notification_large_icon_height);
        int width = (int) resources.getDimension(android.R.dimen.notification_large_icon_width);
        largeIcon = Bitmap.createScaledBitmap(largeIcon, width, height, false);

        //Sonido
        //Uri alarmSound = RingtoneManager.getDefaultUri(tipoSonido!=null?tipoSonido: TIPO_SONIDO_DEFAULT);
        Uri alarmSound;
        if(tipoSonido!=null && tipoSonido==RingtoneManager.TYPE_ALARM) {
            try {
                alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + context.getPackageName() + "/" + R.raw.ticktac_ringtone_1min);
            } catch (Exception e){
                alarmSound = RingtoneManager.getDefaultUri(tipoSonido!=null?tipoSonido: TIPO_SONIDO_DEFAULT);
            }
        } else {
            alarmSound = RingtoneManager.getDefaultUri(tipoSonido != null ? tipoSonido : TIPO_SONIDO_DEFAULT);
        }

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
        int _id = (int) System.currentTimeMillis();
        PendingIntent pendingIntentAbrir = PendingIntent.getActivity(
            context, _id, intentAbrir, PendingIntent.FLAG_ONE_SHOT);
            //context, 0, intentAbrir, PendingIntent.FLAG_UPDATE_CURRENT); Con esto se resumia el estado anterior

        //Intent para ejecutar cuando borra la notificación
        Intent intentCerrar = new Intent(context, NotificationDismissReceiver.class);
        intentCerrar.putExtras(mBundle);
        _id = (int) System.currentTimeMillis();
        PendingIntent pendingIntentCerrar = PendingIntent.getBroadcast(context, _id, intentCerrar, 0);

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
            .setAutoCancel(true) //Para que se cierre cuando el usuario toca la notificación (y va a la app)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC); //Visibilidad con la pantalla bloqueada

        //Se envía la notificación
        notificationManager.notify(tipoNotificacion, notificationBuilder.build());
    }

    private void verificarNotificacionAnterior(Context context, Integer tipoNotificacion,
                                               NotificationManagerCompat notificationManager) {
        try{
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);

            StatusBarNotification[] activeNotifications = mNotificationManager.getActiveNotifications();

            for (StatusBarNotification notification : activeNotifications) {
                if (notification.getId() == tipoNotificacion) {
                    this.registrarNotificacionVencida(context, tipoNotificacion);
                    notificationManager.cancel(tipoNotificacion);
                }
            }
        } catch (Exception e){
            Log.w(this.getClass().getName(), "No se pudieron verificar las notificaciones anteriores.");
        }
    }

    public void registrarCerrarNotificacion(Context context, int tipoNotificacion){
        //Se obtiene título y observación para la interacción, si corresponde
        String titulo="";
        String observacion="";
        if(tipoNotificacion == NotificationService.ID_NOTIFICACION_SALUD) {
            titulo = context.getString(R.string.interaccionTitulo_NotificacionSalud);
            observacion = context.getString(R.string.interaccionTexto_CerrarNotificacionSalud);
        }

        InteractionsService interactionsService = new InteractionsService(context, context.getAssets(), fbRefInteracciones);
        interactionsService.guardarInteraccion(mailQueInicioSesion, titulo, "No", observacion);
    }

    public void registrarNotificacionVencida(Context context, int tipoNotificacion){
        //Se obtiene título y observación para la interacción, si corresponde
        String titulo="";
        String observacion="";
        if(tipoNotificacion == NotificationService.ID_NOTIFICACION_SALUD) {
            titulo = context.getString(R.string.interaccionTitulo_NotificacionSalud);
            observacion = context.getString(R.string.interaccionTexto_VencerNotificacionSalud);
        }
        if(!TextUtils.isEmpty(titulo)){
            InteractionsService interactionsService = new InteractionsService(context, context.getAssets(), fbRefInteracciones);
            interactionsService.guardarInteraccion(mailQueInicioSesion, titulo, "No", observacion);
        }
    }
}
