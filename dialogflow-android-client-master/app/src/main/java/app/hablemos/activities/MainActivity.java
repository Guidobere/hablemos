package app.hablemos.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
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
import ai.api.model.ResponseMessage;
import ai.api.model.Result;
import app.hablemos.R;
import app.hablemos.backgroundServices.SchedulerService;
import app.hablemos.model.Function;
import app.hablemos.model.Recordatorio;
import app.hablemos.model.User;
import app.hablemos.services.FootballService;
import app.hablemos.services.InteractionsService;
import app.hablemos.services.NotificationService;

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
    private TextView resultTextView2;
    private EditText queryEditText;
    private Button micButton;
    private Button send;

    private int HORARIO_MANANA;
    private int HORARIO_TARDE;
    private int HORARIO_NOCHE;

    //Lo que dice y muestra en la aplicacion
    private String speech;

    private String nombreAbuelo = "";
    
    private int a;

    private AIDataService aiDataService;
    private AIService aiService;
    private String mailQueInicioSesion;
    private FootballService footballService;

    private String diaSemana;

    boolean yaSaludo = false;
    boolean vieneDeNotificacion;
    boolean rtaNotificacionManejada;

    //FIREBASE
    DatabaseReference myUsersFb = FirebaseDatabase.getInstance().getReference().child("users");
    DatabaseReference myUsersFb2 = FirebaseDatabase.getInstance().getReference().child("recordatorioglucosa");
    DatabaseReference myUsersFb3 = FirebaseDatabase.getInstance().getReference().child("recordatoriosPresion");
    DatabaseReference fbRefInteracciones = FirebaseDatabase.getInstance().getReference().child("interacciones");

    private InteractionsService interactionsService;
    private PendingIntent pendingEmailIntent;

    //-------------------------------------------*CLIMA VERSION CARO
    private TextView selectCity, cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;
    private ProgressBar loader;
    private Typeface weatherFont;
    private String city = "Buenos Aires, AR"; //Asi lo muestra la app cuando pedis en la web

    /* Please Put your API KEY here */
    private String OPEN_WEATHER_MAP_API = "ea574594b9d36ab688642d5fbeab847e";
    /* Please Put your API KEY here */

    int temperatura;

    //-------------------------------------------*CLIMA VERSION CARO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interactionsService = new InteractionsService(getBaseContext(), getAssets(), fbRefInteracciones);

        setContentView(R.layout.nuevomain);

        mailQueInicioSesion = getIntent().getExtras().getString("1");
        vieneDeNotificacion = getIntent().getExtras().getBoolean("vieneDeNotificacion");
        rtaNotificacionManejada = false;

        TAG = getString(R.string.tagMain);

        diaSemana = getDiaSemana(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));

        HORARIO_MANANA = Integer.parseInt(getString(R.string.horarioManiana));
        HORARIO_TARDE = Integer.parseInt(getString(R.string.horarioTarde));
        HORARIO_NOCHE = Integer.parseInt(getString(R.string.horarioNoche));

        resultTextView = (TextView) findViewById(R.id.resultTextView);
        resultTextView2 = (TextView) findViewById(R.id.resultTextView2);
        queryEditText = (EditText) findViewById(R.id.textQuery);
        queryEditText.setVisibility(View.VISIBLE);

        micButton = (Button) findViewById(R.id.micButton);
        send = (Button) findViewById(R.id.buttonSend);

        //  findViewById(R.id.buttonSend).setOnClickListener(this);
        send.setOnClickListener(this);

        final AIConfiguration config = new AIConfiguration(getString(R.string.accessToken), AIConfiguration.SupportedLanguages.Spanish, AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        aiDataService = new AIDataService(this, config);

        footballService = new FootballService();

        //check for TTS data
        checkForTTSData();

        queryEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendRequest();
                    handled = true;
                }
                return handled;
            }
        });

             //-------------------------------------------*CLIMA VERSION CARO

        loader = (ProgressBar) findViewById(R.id.loader);
        selectCity = (TextView) findViewById(R.id.selectCity);
        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        humidity_field = (TextView) findViewById(R.id.humidity_field);
        pressure_field = (TextView) findViewById(R.id.pressure_field);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        weatherIcon.setTypeface(weatherFont);

        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Change City");
                final EditText input = new EditText(MainActivity.this);
                input.setText(city);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Change",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                city = input.getText().toString();
                                taskLoadUp(city);
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });


    }

    private void checkForTTSData() {
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }

    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getApplicationContext())) {
            DownloadWeather task = new DownloadWeather();
            task.execute(query);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class DownloadWeather extends AsyncTask < String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.setVisibility(View.VISIBLE);

        }
        protected String doInBackground(String...args) {
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    DateFormat df = DateFormat.getDateTimeInstance();

                    cityField.setText(json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country"));
                    detailsField.setText(details.getString("description").toUpperCase(Locale.US));
                    currentTemperatureField.setText(main.getString("temp"));
                    humidity_field.setText("Humidity: " + main.getString("humidity") + "%");
                    pressure_field.setText("Pressure: " + main.getString("pressure") + " hPa");
                    updatedField.setText(df.format(new Date(json.getLong("dt") * 1000)));
                    weatherIcon.setText(Html.fromHtml(Function.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000)));

                    loader.setVisibility(View.GONE);

                }
            } catch (JSONException e) {

                Toast.makeText(getApplicationContext(), "Error, Check City", Toast.LENGTH_SHORT).show();
            }
        }
}
    //-------------------------------------------*CLIMA VERSION CARO

    //mostrar menu de opciones
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menuacciones, menu);
        return true;
         }

    //que accione los botones de ahi
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.configurar) {
            startActivity(RegistroActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
            if(vieneDeNotificacion) {
                if(!rtaNotificacionManejada) {
                    manejarRespuestaNotificacion(getIntent().getExtras());
                    rtaNotificacionManejada = true;
                }
            } else if(!yaSaludo) {
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
                    myTTS.speak(getString(R.string.permisoGrabarDenegado), 0, null, "default");
                }
                break;
            }
            case REQUEST_AUDIO_PERMISSIONS_ON_BUTTON_CLICK_ID : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recordPermissionGranted = true;
                    escucharAbuelo();
                } else {
                    Toast.makeText(this, getString(R.string.permisoGrabarDenegado), Toast.LENGTH_LONG).show();
                    myTTS.speak(getString(R.string.permisoGrabarDenegado), 0, null, "default");
                }
                break;
            }
        }
    }

    @Override
    public void onListeningStarted() {
        Toast.makeText(this, getString(R.string.escuchando), Toast.LENGTH_SHORT).show();
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

    /*
     * AIRequest should have query OR event
     */
    private void sendRequest()  {

        final String queryString = String.valueOf(queryEditText.getText());


        queryEditText.setText("");

        send.onEditorAction(EditorInfo.IME_ACTION_DONE);

        if (TextUtils.isEmpty(queryString)) {
            onError(new AIError(getString(R.string.non_empty_query)));
            myTTS.speak(getString(R.string.non_empty_query), 0, null, "default");
            return;
        }
        resultTextView2.setText(queryString);

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
                                mailQueInicioSesion, getString(R.string.interaccionTitulo_AbrirApp), "-", "-");
                            if(!vieneDeNotificacion)
                                iniciarServicioScheduler();
                            break;
                        case "tarde":
                            if(TextUtils.isEmpty(u.remediosTarde)){
                                loQueDiceYescribe("Nada que tomar");
                            }
                            else{
                                loQueDiceYescribe("Tenés que tomar " + u.remediosTarde);}
                             break;
                        case "mañana":
                           if(TextUtils.isEmpty(u.remediosManiana)){
                                loQueDiceYescribe("Nada que tomar");
                            }
                            else{
                                loQueDiceYescribe("Tenés que tomar " + u.remediosManiana);}
                          break;
                        case "noche":
                            if(TextUtils.isEmpty(u.remediosNoche)){
                                loQueDiceYescribe("Nada que tomar");
                            }
                            else{
                                loQueDiceYescribe("Tenés que tomar " + u.remediosNoche);}
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
                resultTextView2.setText(result.getResolvedQuery());

                try{
                    speech = ((ResponseMessage.ResponseSpeech) result.getFulfillment().getMessages().get(0)).getSpeech().get(0);
                } catch (Exception e){
                    speech = result.getFulfillment().getSpeech();
                }

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
                if(error.toString().contains(getString(R.string.errorSpeechRecog))) {
                    resultTextView.setText(getString(R.string.errorReconVoz));
                    myTTS.speak(getString(R.string.errorReconVoz), 0, null, "default");
                }
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

    public void iniciarServicioScheduler(){
        Intent intent = new Intent(this, SchedulerService.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("1", mailQueInicioSesion);
        mBundle.putString("2", nombreAbuelo);
        intent.putExtras(mBundle);
        startService(intent);
    }

    public void onStop(){
        super.onStop();
        if(myTTS!=null && myTTS.isSpeaking())
            myTTS.stop();
    }

    public void onResume(){
        super.onResume();
    }

    //enviarNotificacionMedicamentos
    private void manejarRespuestaNotificacion(Bundle extras) {
        int tipoNotificacion = extras.getInt("tipoNotificacion");

        if(tipoNotificacion == NotificationService.ID_NOTIFICACION_SALUD){
            String turno = extras.getString("extraInfo");
            pedirAlaBase(turno);
            interactionsService.guardarInteraccion(mailQueInicioSesion,
                getString(R.string.interaccionTitulo_NotificacionSalud), "Si",
                getString(R.string.interaccionTexto_AbrirNotificacionSalud));
        }

        if(tipoNotificacion == NotificationService.ID_NOTIFICACION_CLIMA){

            loQueDiceYescribe("¿Queres ir a pasear?");
        }

    }

    public boolean estaLindo(){
        taskLoadUp(city);
        return (Integer.parseInt(currentTemperatureField.getText().toString())>15);
    }
}