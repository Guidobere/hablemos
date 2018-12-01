package app.hablemos.backgroundServices;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.Calendar;

import app.hablemos.R;
import app.hablemos.model.User;
import app.hablemos.receivers.ClimaReceiver;
import app.hablemos.receivers.EmailReceiver;
import app.hablemos.receivers.HealthReceiver;

public class SchedulerService extends Service{
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private PendingIntent intentRemediosManiana;
    private PendingIntent intentRemediosTarde;
    private PendingIntent intentRemediosNoche;

    private PendingIntent intentGlucosaManiana;
    private PendingIntent intentGlucosaTarde;
    private PendingIntent intentGlucosaNoche;

    private PendingIntent intentPresionManiana;
    private PendingIntent intentPresionTarde;
    private PendingIntent intentPresionNoche;

    private PendingIntent intentReporte;
    private PendingIntent intentClima;

    /** Ids de intents para resetear alarmas **/
    private int idIntentRemediosManiana = 1100;
    private int idIntentRemediosTarde = 1110;
    private int idIntentRemediosNoche = 1120;

    private int idIntentGlucosaManiana = 1200;
    private int idIntentGlucosaTarde = 1210;
    private int idIntentGlucosaNoche = 1220;

    private int idIntentPresionManiana = 1300;
    private int idIntentPresionTarde = 1310;
    private int idIntentPresionNoche = 1320;

    private int idIntentReporte = 1400;
    private int idIntentClima = 1500;
    /** Fin Ids de intents para resetear alarmas **/

    private boolean alarmasSeteadas = false;

    private int horarioSaludManianaDefault;
    private int horarioSaludTardeDefault;
    private int horarioSaludNocheDefault;
    private int horarioClima;
    private int horarioReporte;

    @Override
    public void onCreate() {
        horarioSaludManianaDefault = Integer.parseInt(getString(R.string.horarioSaludManiana));
        horarioSaludTardeDefault = Integer.parseInt(getString(R.string.horarioSaludTarde));
        horarioSaludNocheDefault = Integer.parseInt(getString(R.string.horarioSaludNoche));
        horarioClima = Integer.parseInt(getString(R.string.horarioClima));
        horarioReporte = Integer.parseInt(getString(R.string.horarioReporte));

        // Se crea un thread para el scheduler, sino usaría el thread principal
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
            Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Se setea el Handler que tiene la lógica del servicio
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Se inicia la ejecución enviando el id para poder detenerla y los datos necesarios en Extras
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        if(intent!=null){
            msg.setData(intent.getExtras());
        }
        mServiceHandler.sendMessage(msg);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            try {
                User user = (User) msg.getData().getSerializable("user");
                boolean reset = msg.getData().getBoolean("reset");

                if(reset)
                    eliminarAlarmas();

                if(!alarmasSeteadas)
                    setAlarmas(user);

            } catch (Exception e) {
                Log.e(this.getClass().getName(), "Error en el servicio Scheduler", e);
                Thread.currentThread().interrupt();
            }
            //stopSelf(msg.arg1);
        }
    }

    private void setAlarmas(User user) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Bundle bundle = new Bundle();
        if(user==null) return;

        //REMEDIOS
        //Formato viejo de User sin horarios configurados, toma los default
        if(user.horariosRecordatoriosRemedios == null){
            // Notificaciones de remedios
            intentRemediosManiana = setAlarmaSalud(intentRemediosManiana, user, alarmManager, bundle,
                String.valueOf(horarioSaludManianaDefault), "0","medicamentos", "mañana", idIntentRemediosManiana);
            intentRemediosTarde = setAlarmaSalud(intentRemediosTarde, user, alarmManager, bundle,
                String.valueOf(horarioSaludTardeDefault), "0","medicamentos", "tarde", idIntentRemediosTarde);
            intentRemediosNoche = setAlarmaSalud(intentRemediosNoche, user, alarmManager, bundle,
                String.valueOf(horarioSaludNocheDefault), "0","medicamentos", "noche", idIntentRemediosNoche);
        //Formato nuevo de User con horarios configurados. Setea la alma si el horario no es vacío
        } else {
            intentRemediosManiana = setAlarmaSalud(intentRemediosManiana, user, alarmManager, bundle,
                user.horariosRecordatoriosRemedios.manianaHora, user.horariosRecordatoriosRemedios.manianaMinutos, "medicamentos", "mañana", idIntentRemediosManiana);
            intentRemediosTarde = setAlarmaSalud(intentRemediosTarde, user, alarmManager, bundle,
                user.horariosRecordatoriosRemedios.tardeHora, user.horariosRecordatoriosRemedios.tardeMinutos, "medicamentos", "tarde", idIntentRemediosTarde);
            intentRemediosNoche = setAlarmaSalud(intentRemediosNoche, user, alarmManager, bundle,
                user.horariosRecordatoriosRemedios.nocheHora, user.horariosRecordatoriosRemedios.nocheMinutos, "medicamentos", "noche", idIntentRemediosNoche);
        }

        //GLUCOSA
        //Formato viejo de User sin horarios configurados, toma los default
        if(user.horariosRecordatoriosGlucosa == null){
            // Notificaciones de remedios
            intentGlucosaManiana = setAlarmaSalud(intentGlucosaManiana, user, alarmManager, bundle,
                String.valueOf(horarioSaludManianaDefault), "15","glucosa", "mañana", idIntentGlucosaManiana);
            intentGlucosaTarde = setAlarmaSalud(intentGlucosaTarde, user, alarmManager, bundle,
                String.valueOf(horarioSaludTardeDefault), "15","glucosa", "tarde", idIntentGlucosaTarde);
            intentGlucosaNoche = setAlarmaSalud(intentGlucosaNoche, user, alarmManager, bundle,
                String.valueOf(horarioSaludNocheDefault), "15","glucosa", "noche", idIntentGlucosaNoche);
            //Formato nuevo de User con horarios configurados. Setea la alma si el horario no es vacío
        } else {
            intentGlucosaManiana = setAlarmaSalud(intentGlucosaManiana, user, alarmManager, bundle,
                user.horariosRecordatoriosGlucosa.manianaHora, user.horariosRecordatoriosGlucosa.manianaMinutos,
                "glucosa", "mañana", idIntentGlucosaManiana);
            intentGlucosaTarde = setAlarmaSalud(intentGlucosaTarde, user, alarmManager, bundle,
                user.horariosRecordatoriosGlucosa.tardeHora, user.horariosRecordatoriosGlucosa.tardeMinutos,
                "glucosa", "tarde", idIntentGlucosaTarde);
            intentGlucosaNoche = setAlarmaSalud(intentGlucosaNoche, user, alarmManager, bundle,
                user.horariosRecordatoriosGlucosa.nocheHora, user.horariosRecordatoriosGlucosa.nocheMinutos,
                "glucosa", "noche", idIntentGlucosaNoche);
        }

        //PRESION
        //Formato viejo de User sin horarios configurados, toma los default
        if(user.horariosRecordatoriosPresion == null){
            // Notificaciones de remedios
            intentPresionManiana = setAlarmaSalud(intentPresionManiana, user, alarmManager, bundle,
                String.valueOf(horarioSaludManianaDefault), "30","presion", "mañana", idIntentPresionManiana);
            intentPresionTarde = setAlarmaSalud(intentPresionTarde, user, alarmManager, bundle,
                String.valueOf(horarioSaludTardeDefault), "30","presion", "tarde", idIntentPresionTarde);
            intentPresionNoche = setAlarmaSalud(intentPresionNoche, user, alarmManager, bundle,
                String.valueOf(horarioSaludNocheDefault), "30","presion", "noche", idIntentPresionNoche);
            //Formato nuevo de User con horarios configurados. Setea la alma si el horario no es vacío
        } else {
            intentPresionManiana = setAlarmaSalud(intentPresionManiana, user, alarmManager, bundle,
                user.horariosRecordatoriosPresion.manianaHora, user.horariosRecordatoriosPresion.manianaMinutos,
                "presion", "mañana", idIntentPresionManiana);
            intentPresionTarde = setAlarmaSalud(intentPresionTarde, user, alarmManager, bundle,
                user.horariosRecordatoriosPresion.tardeHora, user.horariosRecordatoriosPresion.tardeMinutos,
                "presion", "tarde", idIntentPresionTarde);
            intentPresionNoche = setAlarmaSalud(intentPresionNoche, user, alarmManager, bundle,
                user.horariosRecordatoriosPresion.nocheHora, user.horariosRecordatoriosPresion.nocheMinutos,
                "presion", "noche", idIntentPresionNoche);
        }

        // Mail de reporte al tutor
        intentReporte = setAlarma(user.email, user.username, null, horarioReporte, 0,
            intentReporte, alarmManager, EmailReceiver.class, AlarmManager.INTERVAL_DAY, idIntentReporte);

        intentClima = setAlarma(user.email, user.username, null, horarioClima, 0,
            intentClima, alarmManager, ClimaReceiver.class, AlarmManager.INTERVAL_DAY, idIntentClima);

        alarmasSeteadas = true;

    }

    private PendingIntent setAlarmaSalud(PendingIntent intent, User user, AlarmManager alarmManager,
                                         Bundle bundle, String hora, String minutos, String tipo,
                                         String turno, int idIntent) {
        PendingIntent pendingIntent = null;
        if(!TextUtils.isEmpty(hora) && !TextUtils.isEmpty(minutos)){
            bundle.putString("tipo", tipo);
            bundle.putString("turno", turno);
            pendingIntent = setAlarma(user.email, user.username, bundle,
                Integer.valueOf(hora),
                Integer.valueOf(minutos),
                intent, alarmManager, HealthReceiver.class, AlarmManager.INTERVAL_DAY, idIntent);
        }
        return pendingIntent;
    }


    private PendingIntent setAlarma(String mailQueInicioSesion, String nombreAbuelo, Bundle bundle, int horaAlarma,
                                    int minutosAlarma, PendingIntent pendingIntentAlarma, AlarmManager alarmManager,
                                    Class<?> receiverClass, long periodicidad, int intentId) {

        // Intent asociado al receiver que va a mandar la notificación (si corresponde)
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            getBaseContext(), intentId,
            getBaseIntent(mailQueInicioSesion, nombreAbuelo, bundle, receiverClass), 0);

        if (pendingIntentAlarma != null)
            alarmManager.cancel(pendingIntentAlarma);

        pendingIntentAlarma = pendingIntent;

        // Fecha y hora de la primera ejecución. Si ya pasó la hora se programa para el día siguiente.
        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.HOUR_OF_DAY)>horaAlarma || (calendar.get(Calendar.HOUR_OF_DAY)==horaAlarma && calendar.get(Calendar.MINUTE)>minutosAlarma))
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, horaAlarma);
        calendar.set(Calendar.MINUTE, minutosAlarma);
        calendar.set(Calendar.SECOND, 0);

        // Se configura la alarma para ejecutar todos los días a la misma hora.
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(), periodicidad, pendingIntentAlarma);
        return pendingIntentAlarma;
    }

    private Intent getBaseIntent(String mailQueInicioSesion, String nombreAbuelo, Bundle bundle, Class<?> receiverClass) {
        Intent intent = new Intent(getBaseContext(), receiverClass);
        intent.putExtra("nombreAbuelo", nombreAbuelo);
        intent.putExtra("mailQueInicioSesion", mailQueInicioSesion);
        if(bundle!=null)
            intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eliminarAlarmas();
    }

    private void eliminarAlarmas() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        try {
            alarmManager.cancel(getPendintIntent(idIntentRemediosManiana));
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        try {
            alarmManager.cancel(getPendintIntent(idIntentRemediosTarde));
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        try {
            alarmManager.cancel(getPendintIntent(idIntentRemediosNoche));
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        try {
            alarmManager.cancel(getPendintIntent(idIntentGlucosaManiana));
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        try {
            alarmManager.cancel(getPendintIntent(idIntentGlucosaTarde));
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        try {
            alarmManager.cancel(getPendintIntent(idIntentGlucosaNoche));
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        try {
            alarmManager.cancel(getPendintIntent(idIntentPresionManiana));
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        try {
            alarmManager.cancel(getPendintIntent(idIntentPresionTarde));
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        try {
            alarmManager.cancel(getPendintIntent(idIntentPresionNoche));
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        try {
            alarmManager.cancel(getPendintIntent(idIntentReporte));
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        try {
            alarmManager.cancel(getPendintIntent(idIntentClima));
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        alarmasSeteadas = false;
    }

    private PendingIntent getPendintIntent(int id) {
         return PendingIntent.getBroadcast(
                getBaseContext(), id, new Intent(), 0);
    }
}
