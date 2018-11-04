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

import org.w3c.dom.Text;

import java.util.Calendar;

import app.hablemos.R;
import app.hablemos.model.HorariosRecordatorios;
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
    private PendingIntent intentReporte;
    private PendingIntent intentClima;

    private boolean alarmasSeteadas = false;

    private int horarioRemediosManianaDefault;
    private int horarioRemediosTardeDefault;
    private int horarioRemediosNocheDefault;
    private int horarioClima;
    private int horarioReporte;

    @Override
    public void onCreate() {
        horarioRemediosManianaDefault = Integer.parseInt(getString(R.string.horarioSaludManiana));
        horarioRemediosTardeDefault = Integer.parseInt(getString(R.string.horarioSaludTarde));
        horarioRemediosNocheDefault = Integer.parseInt(getString(R.string.horarioSaludNoche));
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

        bundle.putString("tipo", "medicamentos");
        //Formato viejo de User sin horarios configurados, toma los default
        if(user.horariosRecordatoriosRemedios == null){
            // Notificación de los remedios de la mañana

            bundle.putString("turno", "mañana");
            intentRemediosManiana = setAlarma(user.email, user.username, bundle, horarioRemediosManianaDefault, 0,
                intentRemediosManiana, alarmManager, HealthReceiver.class, AlarmManager.INTERVAL_DAY);

            // Notificación de los remedios de la tarde
            bundle.putString("turno", "tarde");
            intentRemediosTarde = setAlarma(user.email, user.username, bundle, horarioRemediosTardeDefault, 0,
                intentRemediosTarde, alarmManager, HealthReceiver.class, AlarmManager.INTERVAL_DAY);

            bundle.putString("turno", "noche");
            intentRemediosNoche = setAlarma(user.email, user.username, bundle, horarioRemediosNocheDefault, 0,
                intentRemediosNoche, alarmManager, HealthReceiver.class, AlarmManager.INTERVAL_DAY);

        //Formato nuevo de User con horarios configurados. Setea la alma si el horario no es vacío
        } else {
            if(!TextUtils.isEmpty(user.horariosRecordatoriosRemedios.manianaHora) && !TextUtils.isEmpty(user.horariosRecordatoriosRemedios.manianaMinutos)){
                bundle.putString("turno", "mañana");
                intentRemediosManiana = setAlarma(user.email, user.username, bundle,
                    Integer.valueOf(user.horariosRecordatoriosRemedios.manianaHora),
                    Integer.valueOf(user.horariosRecordatoriosRemedios.manianaMinutos),
                    intentRemediosManiana, alarmManager, HealthReceiver.class, AlarmManager.INTERVAL_DAY);
            }
            if(!TextUtils.isEmpty(user.horariosRecordatoriosRemedios.tardeHora) && !TextUtils.isEmpty(user.horariosRecordatoriosRemedios.tardeMinutos)){
                bundle.putString("turno", "tarde");
                intentRemediosTarde = setAlarma(user.email, user.username, bundle,
                        Integer.valueOf(user.horariosRecordatoriosRemedios.tardeHora),
                        Integer.valueOf(user.horariosRecordatoriosRemedios.tardeMinutos),
                        intentRemediosTarde, alarmManager, HealthReceiver.class, AlarmManager.INTERVAL_DAY);
            }
            if(!TextUtils.isEmpty(user.horariosRecordatoriosRemedios.nocheHora) && !TextUtils.isEmpty(user.horariosRecordatoriosRemedios.nocheMinutos)){
                bundle.putString("turno", "noche");
                intentRemediosNoche = setAlarma(user.email, user.username, bundle,
                        Integer.valueOf(user.horariosRecordatoriosRemedios.nocheHora),
                        Integer.valueOf(user.horariosRecordatoriosRemedios.nocheMinutos),
                        intentRemediosNoche, alarmManager, HealthReceiver.class, AlarmManager.INTERVAL_DAY);
            }
        }

        // Mail de reporte al tutor
        intentReporte = setAlarma(user.email, user.username, null, horarioReporte, 0,
            intentReporte, alarmManager, EmailReceiver.class, AlarmManager.INTERVAL_DAY);

        intentClima = setAlarma(user.email, user.username, null, horarioClima, 0,
            intentClima, alarmManager, ClimaReceiver.class, AlarmManager.INTERVAL_DAY);

        alarmasSeteadas = true;

    }

    private PendingIntent setAlarma(String mailQueInicioSesion, String nombreAbuelo, Bundle bundle, int horaAlarma,
                                    int minutosAlarma, PendingIntent pendingIntentAlarma, AlarmManager alarmManager,
                                    Class<?> receiverClass, long periodicidad) {

        // Intent asociado al receiver que va a mandar la notificación (si corresponde)
        // Es necesario un _id distinto para cada una de las alarmas de salud, sino quedaba seteada sólo la última
        Intent intent = new Intent(getBaseContext(), receiverClass);
        intent.putExtra("nombreAbuelo", nombreAbuelo);
        intent.putExtra("mailQueInicioSesion", mailQueInicioSesion);
        if(bundle!=null)
            intent.putExtras(bundle);

        final int _id = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), _id, intent, 0);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        eliminarAlarmas();
    }

    private void eliminarAlarmas() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        try {
            if(intentRemediosManiana!=null) alarmManager.cancel(intentRemediosManiana);
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        try {
            if(intentRemediosTarde!=null) alarmManager.cancel(intentRemediosTarde);
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        try {
            if(intentRemediosNoche!=null) alarmManager.cancel(intentRemediosNoche);
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        try {
            if(intentReporte!=null) alarmManager.cancel(intentReporte);
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        try {
            if(intentClima!=null) alarmManager.cancel(intentClima);
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar alarma.");
        }
        alarmasSeteadas = false;
    }
}
