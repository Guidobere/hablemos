package app.hablemos.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import app.hablemos.R;
import app.hablemos.model.Recordatorio;
import app.hablemos.model.User;
import app.hablemos.receivers.EmailReceiver;
import app.hablemos.receivers.WeatherReceiver;
import app.hablemos.services.InteractionsService;

public class MainActivity extends AppCompatActivity implements AIListener , View.OnClickListener , AdapterView.OnItemSelectedListener, TextToSpeech.OnInitListener {
    private String TAG;

    //status check code
    private int MY_DATA_CHECK_CODE = 0;
    private static final int REQUEST_AUDIO_PERMISSIONS_ID = 33;
    private static final int REQUEST_AUDIO_PERMISSIONS_ON_BUTTON_CLICK_ID = 133;
    private boolean recordPermissionGranted = false;

    //TTS object
    private TextToSpeech myTTS;

    private TextView resultTextView;
    private EditText queryEditText;
    private Button micButton;
    private Button send;

    private int HORARIO_MANANA;
    private int HORARIO_TARDE;
    private int HORARIO_NOCHE;

    private int CANTIDAD_HORAS_CLIMA;

    //PARA EL MENSAJE ANTERIOR
    //DONDE SE GUARDA EL MSJ ANTERIOR
    private String dialogoAnterior = "";

    //Donde se muestra el msj anterior
    private TextView resultTextViewAnterior;

    //Lo que dice y muestra en la aplicacion
    private String speech;

    private String nombreAbuelo = "";
    
    private int a;

    private AIDataService aiDataService;
    private AIService aiService;
    private String mailQueInicioSesion;

    private String diaSemana;

    boolean yaSaludo = false;
    boolean vieneDeNotificacion;

    //FIREBASE
    DatabaseReference myUsersFb = FirebaseDatabase.getInstance().getReference().child("users");
    DatabaseReference myUsersFb2 = FirebaseDatabase.getInstance().getReference().child("recordatorioglucosa");
    DatabaseReference myUsersFb3 = FirebaseDatabase.getInstance().getReference().child("recordatoriosPresion");
    DatabaseReference fbRefInteracciones = FirebaseDatabase.getInstance().getReference().child("interacciones");

    private InteractionsService interactionsService;
    private PendingIntent pendingEmailIntent;
    private PendingIntent pendingWeatherIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interactionsService = new InteractionsService(getBaseContext(), getAssets(), fbRefInteracciones);

        setContentView(R.layout.nuevomain);

        mailQueInicioSesion = getIntent().getExtras().getString("1");
        vieneDeNotificacion = getIntent().getExtras().getBoolean("vieneDeNotificacion");

        TAG = getString(R.string.tagMain);

        diaSemana = getDiaSemana(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        
        HORARIO_MANANA = Integer.parseInt(getString(R.string.horarioManiana));
        HORARIO_TARDE = Integer.parseInt(getString(R.string.horarioTarde));
        HORARIO_NOCHE = Integer.parseInt(getString(R.string.horarioNoche));

        CANTIDAD_HORAS_CLIMA = Integer.parseInt(getString(R.string.horasClima));

        resultTextView = (TextView) findViewById(R.id.resultTextView);
        queryEditText = (EditText) findViewById(R.id.textQuery);
        queryEditText.setVisibility(View.VISIBLE);

        micButton = (Button) findViewById(R.id.micButton);
        send = (Button) findViewById(R.id.buttonSend);

        //PARA EL MENSAJE ANTERIOR - PARA MOSTRARLO
        //resultTextViewAnterior = (TextView) findViewById(R.id.resultTextViewAnterior);

      //  findViewById(R.id.buttonSend).setOnClickListener(this);
        send.setOnClickListener(this);
        //findViewById(R.id.buttonClear).setOnClickListener(this);
        //findViewById(R.id.buttonClearHistorial).setOnClickListener(this);

        final AIConfiguration config = new AIConfiguration(getString(R.string.accessToken), AIConfiguration.SupportedLanguages.Spanish, AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        aiDataService = new AIDataService(this, config);

        //check for TTS data
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        registerReceiver(respuestasClimaReceiver, new IntentFilter("respuestasClima"));
        setearCronClima();
    }

    // Add this inside your class
    BroadcastReceiver respuestasClimaReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String isHot = intent.getExtras().getString("isHot");
            String isSuitableForOutdoor = intent.getExtras().getString("isSuitableForOutdoor");
            if (Boolean.parseBoolean(isHot)) {
                //TODO: mandar a tomar agua
                System.out.println("isHot:" + isHot);
            }
            if (Boolean.parseBoolean(isSuitableForOutdoor)) {
                //TODO: mandar pregunta para caminar
                System.out.println("isSuitableForOutdoor: " + isSuitableForOutdoor);
            }
        }
    };

    private String getDiaSemana(int i) {
        String dia = "";
        switch (i) {
            case 1:
                dia = "domingo";
                break;
            case 2:
                dia = "lunes";
                break;
            case 3:
                dia = "martes";
                break;
            case 4:
                dia = "miercoles";
                break;
            case 5:
                dia = "jueves";
                break;
            case 6:
                dia = "viernes";
                break;
            case 7:
                dia = "sabado";
                break;
        }
        return dia;
    }

    private String parsearNombre(String nombre) {
        String s1 = nombre.substring(0, 1).toUpperCase();
        return s1 + nombre.substring(1).toLowerCase();
    }

    //act on result of TTS data check
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSIONS_ID);
        } else
            recordPermissionGranted = true;
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            } else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    //setup TTS
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            myTTS.setLanguage(new Locale("es", "AR"));
            if(!yaSaludo) {
                pedirAlaBase("saludo");
                yaSaludo = true;
            }
        }
    }

    /**
     * Contiene la lógica del botón Hablar. Si ya dió permiso de grabación de audio anteriormente,
     * puede hablar. Si no dió permiso se le solicita nuevamente. Luego en onRequestPermissionsResult
     * se verifica la respuesta y se le permite hablar o no.
     * @param view
     */
    public void micButtonOnClick(final View view) {
        //Se verifica primero con la variable local para mejor tiempo de respuesta del botón
        if (recordPermissionGranted) {
            escucharAbuelo();
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSIONS_ON_BUTTON_CLICK_ID);
        } else {
            //Esto es para cuando da falso porque se salió de la app y se volvió a entrar
            recordPermissionGranted = true;
        }
    }

    private void escucharAbuelo() {
        Toast.makeText(this, getString(R.string.escuchando), Toast.LENGTH_SHORT).show();
        micButton.setEnabled(false);
        aiService.startListening();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSIONS_ID : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recordPermissionGranted = true;
                } else {
                    Toast.makeText(this, getString(R.string.permisoGrabarDenegado), Toast.LENGTH_LONG).show();
                }
                break;
            }
            case REQUEST_AUDIO_PERMISSIONS_ON_BUTTON_CLICK_ID : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recordPermissionGranted = true;
                    escucharAbuelo();
                } else {
                    Toast.makeText(this, getString(R.string.permisoGrabarDenegado), Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    public void onListeningStarted() {
        // show recording indicator
    }

    @Override
    public void onListeningCanceled() {
        micButton.setEnabled(true);
    }

    @Override
    public void onListeningFinished() {
        micButton.setEnabled(true);
    }

    @Override
    public void onAudioLevel(final float level) {
        // show sound level
    }

  /*  private void clearEditText() {
        queryEditText.setText("");
    }

    //Aca te borra el ultimo mensaje, su anterior y el texto.
    private void clearEditTextHistorial() {
        resultTextViewAnterior.setText("");
        resultTextView.setText("");
        queryEditText.setText("");
        dialogoAnterior="";
        speech="";
    }*/

    /*
     * AIRequest should have query OR event
     */
        private void sendRequest()  {

            final String queryString = String.valueOf(queryEditText.getText());

            send.onEditorAction(EditorInfo.IME_ACTION_DONE);

        if (TextUtils.isEmpty(queryString)) {
            onError(new AIError(getString(R.string.non_empty_query)));
            return;
        }

        final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {

            private AIError aiError;

                    @Nullable
                    @Override
                    protected AIResponse doInBackground(final String... params) {
                        final AIRequest request = new AIRequest();
                        String query  = params[0];
                        RequestExtras requestExtras = null;
                if (!TextUtils.isEmpty(query))
                    request.setQuery(query);

                        try {
                            return aiDataService.request(request, requestExtras);
                        } catch (final AIServiceException e) {
                    aiError = new AIError(e);
                            return null;
                        }
                    }

                    @Override
            protected void onPostExecute(final AIResponse response) {
                        if (response != null) {
                            onResult(response);
                        }
                    }

                };
                task.execute(queryString);
            }
/*
    public void mostrarMensajeAnterior(){
      dialogoAnterior=speech;
      resultTextViewAnterior.setText(speech);
    }*/

    private String getSaludo() {
        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
        if (currentHourIn24Format >= HORARIO_MANANA && currentHourIn24Format < HORARIO_TARDE) {
            return getString(R.string.saludoManiana);
        } else if (currentHourIn24Format >= HORARIO_TARDE && currentHourIn24Format < HORARIO_NOCHE) {
            return getString(R.string.saludoTarde);
        } else {
            return getString(R.string.saludoNoche);
        }
    }

    public void personalizarMensaje(){
        switch (speech) {
            case "tarde":
                pedirAlaBase("tarde");
                break;
            case "mañana":
                pedirAlaBase("mañana");
                break;
            case "noche":
                pedirAlaBase("noche");
                break;
            case "glucosa-mañana":
                pedirAlaBaseSobreGlucosa("mañana");
                break;
            case "glucosa-noche":
                pedirAlaBaseSobreGlucosa("noche");
                break;
            case "glucosa-tarde":
                pedirAlaBaseSobreGlucosa("tarde");
                break;
            case "presión-mañana":
                pedirAlaBaseSobrePresion("mañana");
                break;
            case "presión-noche":
                pedirAlaBaseSobrePresion("noche");
                break;
            case "presión-tarde":
                pedirAlaBaseSobrePresion("tarde");
                break;
            default: //Aca no lo modifique por que lo que dice es el mismo speech, los otros lo modificaba
                resultTextView.setText(speech);
                myTTS.speak(speech,0,null, "default");
                break;
        }
    }

    public void pedirAlaBaseSobrePresion(final String turnoPresion){
        a = 0;
        myUsersFb3.orderByChild("email").equalTo(mailQueInicioSesion).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot users) {

                Recordatorio u;
                Iterable<DataSnapshot> usersChildren = users.getChildren();

                for (DataSnapshot user : usersChildren) {
                    u = user.getValue(Recordatorio.class);
                    String elturno = "", losdias = "";
                    if (u != null) {
                        elturno = u.turno;
                        losdias = u.dias;
                    }

                    if(elturno.equalsIgnoreCase(turnoPresion) && losdias.contains(diaSemana)){
                        a=1;
                       // speech="Hoy si";
                        loQueDiceYescribe("Hoy si");
                    }
                }

                if(a == 0) {
                    loQueDiceYescribe("Hoy no");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

   public void pedirAlaBaseSobreGlucosa(final String turnoGlucosa){
       a = 0;
       myUsersFb2.orderByChild("email").equalTo(mailQueInicioSesion).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot users) {

                Recordatorio u;
                Iterable<DataSnapshot> usersChildren = users.getChildren();

                for (DataSnapshot user : usersChildren) {
                    u = user.getValue(Recordatorio.class);
                    String elturno = "", losdias = "";
                    if (u != null) {
                        elturno = u.turno;
                        losdias = u.dias;
                    }

                    if(elturno.equalsIgnoreCase(turnoGlucosa) && losdias.contains(diaSemana)){
                        a=1;
                       loQueDiceYescribe("Hoy si");
                    }
                }

                if(a == 0) {
                     loQueDiceYescribe("Hoy no");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void pedirAlaBase(final String turno){
        myUsersFb.orderByChild("email").equalTo(mailQueInicioSesion).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot users) {

                User u = new User();
                Iterable<DataSnapshot> usersChildren = users.getChildren();
                for (DataSnapshot user : usersChildren) {
                    u = user.getValue(User.class);
                }

                if (u != null && u.username != null) {
                    switch (turno) {
                        case "saludo":
                            nombreAbuelo = parsearNombre(u.username);
                            loQueDiceYescribe(getSaludo() + ", " + nombreAbuelo + "! ");
                            interactionsService.guardarInteraccion(
                                mailQueInicioSesion, getString(R.string.interaccionAbrirApp), "-", "-");
                            if(!vieneDeNotificacion)
                                setearCronReporte();
                            break;
                        case "tarde":
                            if(TextUtils.isEmpty(u.remediosTarde)){
                                loQueDiceYescribe("Nada que tomar");
                            }
                            else{
                                loQueDiceYescribe(u.remediosTarde);}
                             break;
                        case "mañana":
                           if(TextUtils.isEmpty(u.remediosManiana)){
                                loQueDiceYescribe("Nada que tomar");
                            }
                            else{
                                loQueDiceYescribe(u.remediosManiana);}
                          break;
                        case "noche":
                            if(TextUtils.isEmpty(u.remediosTarde)){
                                loQueDiceYescribe("Nada que tomar");
                            }
                            else{
                            loQueDiceYescribe(u.remediosTarde);}
                            break;
                        default:
                            break;
                    }
                } else {
                    System.out.println("Ocurrio un error al obtener el usuario");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void onResult(final AIResponse response) {
        //mostrarMensajeAnterior();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Result result = response.getResult();
                queryEditText.setText(result.getResolvedQuery());

                //ACA ES DONDE SE USA SPEECH PARA PEDIRLE QUE LO DIGA EN VOZ ALTA Y LO ESCRIBA
                speech = result.getFulfillment().getSpeech();
                personalizarMensaje();
            }
        });
    }

    public void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Se loguea el error.
                Log.w(TAG, error.toString());

                //Si error es del reconocimiento de voz se muestra un mensaje preestablecido.
                if(error.toString().contains(getString(R.string.errorSpeechRecog)))
                    resultTextView.setText(getString(R.string.errorReconVoz));
                else
                    resultTextView.setText(error.toString());

                //Cuando el error es "No speech input" se llama directamente al onError
                //sin llamar a onListeningFinished. Por eso se habilita el micrófono acá.
                micButton.setEnabled(true);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonClear:
             //   clearEditText();
                break;
        case R.id.buttonClearHistorial:
           // clearEditTextHistorial();
            break;
            case R.id.buttonSend:
                sendRequest();
                break;
        }
    }

    private void startActivity(Class<?> cls) {
        final Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    public void loQueDiceYescribe(String unTexto){
        try {
            speech = unTexto;
            resultTextView.setText(speech);
            myTTS.speak(speech, 0, null, "default");
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al emitir respuesta.", e);
        }
    }

    @Override
    public void onBackPressed() {
        if (true) {
        } else {
            super.onBackPressed();
        }
    }

    public void setearCronReporte(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent emailIntent = new Intent(this, EmailReceiver.class);
        emailIntent.putExtra("nombreAbuelo", nombreAbuelo);
        emailIntent.putExtra("mailQueInicioSesion", mailQueInicioSesion);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, emailIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);

        // Clear previous everyday pending intent if exists.
        if (null != pendingEmailIntent) {
            alarmManager.cancel(pendingEmailIntent);
        }
        pendingEmailIntent = pendingIntent;
        /* INTERVAL_DAY: TODOS LOS DIAS A ESTA HORA */
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingEmailIntent);
    }

    public void setearCronClima(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent weatherIntent = new Intent(this, WeatherReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, weatherIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, CANTIDAD_HORAS_CLIMA);

        // Clear previous everyday pending intent if exists.
        if (null != pendingWeatherIntent) {
            alarmManager.cancel(pendingWeatherIntent);
        }
        pendingWeatherIntent = pendingIntent;
        /* INTERVAL_HOUR: CADA HORA */
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR * CANTIDAD_HORAS_CLIMA, pendingWeatherIntent);
    }
}