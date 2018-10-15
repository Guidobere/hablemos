package app.hablemos.backgroundServices;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;

import app.hablemos.R;
import app.hablemos.receivers.HealthReceiver;

public class SchedulerService extends Service{
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private PendingIntent intentSaludManiana;
    private PendingIntent intentSaludTarde;
    private PendingIntent intentSaludNoche;

    private boolean alarmasSaludSeteadas = false;

    private int horarioSaludManiana;
    private int horarioSaludTarde;
    private int horarioSaludNoche;

    @Override
    public void onCreate() {
        horarioSaludManiana = Integer.parseInt(getString(R.string.horarioSaludManiana));
        horarioSaludTarde = Integer.parseInt(getString(R.string.horarioSaludTarde));
        horarioSaludNoche = Integer.parseInt(getString(R.string.horarioSaludNoche));

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
                String mailQueInicioSesion = msg.getData().getString("1");
                String nombreAbuelo = msg.getData().getString("2");

                if(!alarmasSaludSeteadas)
                    setAlarmasSalud(mailQueInicioSesion, nombreAbuelo);

            } catch (Exception e) {
                Log.e(this.getClass().getName(), "Error en el servicio Scheduler", e);
                Thread.currentThread().interrupt();
            }
            //stopSelf(msg.arg1);
        }
    }

    private void setAlarmasSalud(String mailQueInicioSesion, String nombreAbuelo) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //Notificación de los remedios de la mañana
        intentSaludManiana = setAlarmaSalud(mailQueInicioSesion, nombreAbuelo,
            horarioSaludManiana, intentSaludManiana, alarmManager);

        //Notificación de los remedios de la tarde
        intentSaludTarde = setAlarmaSalud(mailQueInicioSesion, nombreAbuelo,
            horarioSaludTarde, intentSaludTarde, alarmManager);

        //Notificación de los remedios de la noche
        intentSaludNoche = setAlarmaSalud(mailQueInicioSesion, nombreAbuelo,
            horarioSaludNoche, intentSaludNoche, alarmManager);

        alarmasSaludSeteadas = true;

    }

    private PendingIntent setAlarmaSalud(String mailQueInicioSesion, String nombreAbuelo, int horaAlarma,
        PendingIntent pendingIntentAlarma, AlarmManager alarmManager) {

        // Intent asociado al receiver que va a mandar la notificación (si corresponde)
        // Es necesario un _id disinto para cada una de las alarmas de salud, sino quedaba seteada sólo la última
        Intent intent = new Intent(getBaseContext(), HealthReceiver.class);
        intent.putExtra("nombreAbuelo", nombreAbuelo);
        intent.putExtra("mailQueInicioSesion", mailQueInicioSesion);
        final int _id = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), _id, intent, 0);

        if (pendingIntentAlarma != null)
            alarmManager.cancel(pendingIntentAlarma);

        pendingIntentAlarma = pendingIntent;

        // Fecha y hora de la primera ejecución. Si ya pasó la hora se programa para el día siguiente.
        Calendar calendar = Calendar.getInstance();

        //if(calendar.get(Calendar.HOUR_OF_DAY)>=horaAlarma) // TODO prueba
        if(calendar.get(Calendar.HOUR_OF_DAY)>horaAlarma)
            calendar.add(Calendar.DAY_OF_MONTH, 1);


        calendar.set(Calendar.HOUR_OF_DAY, horaAlarma);

        //calendar.set(Calendar.MINUTE, 0); //TODO prueba
        calendar.set(Calendar.MINUTE, 50);

        calendar.set(Calendar.SECOND, 0);

        // Se configura la alarma para ejecutar todos los días a la misma hora.
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentAlarma);
        return pendingIntentAlarma;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.cancel(intentSaludManiana);
            alarmManager.cancel(intentSaludTarde);
            alarmManager.cancel(intentSaludNoche);
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al cancelar las alarmas.");
        }

    }
}
