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
import android.os.CountDownTimer;
import android.os.Handler;
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
import ai.api.util.StringUtils;
import app.hablemos.R;
import app.hablemos.backgroundServices.SchedulerService;
import app.hablemos.model.Function;
import app.hablemos.model.Recordatorio;
import app.hablemos.model.SacadorDeAcentos;
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

    //Sacador de acentos
    private SacadorDeAcentos chauAcentos;

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
    private String equipoAbuelo = "";
    
    private int a;

    private AIDataService aiDataService;
    private AIService aiService;
    private String mailQueInicioSesion;
    private FootballService footballService;

    private String diaSemana;

    boolean yaSaludo = false;
    boolean vieneDeNotificacion;
    boolean rtaNotificacionManejada;

    //Multi Tap en el boton "editar registro".
    public int contadorClicksEditarRegistro;

    //FIREBASE
    DatabaseReference myUsersFb = FirebaseDatabase.getInstance().getReference().child("users");
    DatabaseReference myUsersFb2 = FirebaseDatabase.getInstance().getReference().child("recordatorioglucosa");
    DatabaseReference myUsersFb3 = FirebaseDatabase.getInstance().getReference().child("recordatoriosPresion");
    DatabaseReference fbRefInteracciones = FirebaseDatabase.getInstance().getReference().child("interacciones");

    private InteractionsService interactionsService;
    private PendingIntent pendingEmailIntent;

    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interactionsService = new InteractionsService(getBaseContext(), getAssets(), fbRefInteracciones);

        setContentView(R.layout.nuevomain);

        mailQueInicioSesion = getIntent().getExtras().getString("1");
        vieneDeNotificacion = getIntent().getExtras().getBoolean("vieneDeNotificacion");
        rtaNotificacionManejada = false;
        contadorClicksEditarRegistro = 0;

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


/*        timer = new MiContador(3000,1000);
        timer.start();
        timer.onFinish(contadorClicksEditarRegistro);*/

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


    }

    private void checkForTTSData() {
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }

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
            contadorClicksEditarRegistro+=1;

            new CountDownTimer(5000, 1000) {

                public void onTick(long millisUntilFinished) {
                    //Log.w("contador tiempo", "seconds remaining: " + millisUntilFinished / 1000);
                    //resultTextView.setText("seconds remaining: " + millisUntilFinished / 1000);
                    //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    contadorClicksEditarRegistro =0;
                    //resultTextView.setText("Done");
                    //Log.w("contador tiempo", "done");
                }
            }.start();

            if(contadorClicksEditarRegistro > 5){
                interrumpirBotty();
                startActivity(RegistroActivity.class);
                return true;
            }
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
        interrumpirBotty();
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

        interrumpirBotty();
        final String queryString = chauAcentos.stripAccents( String.valueOf(queryEditText.getText()));

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
        if (speech.startsWith("futbol")) {
            String result = "";
            String[] pedido = speech.split("_");
            String accion = pedido[1].trim();
            String equipo = "";
            switch (accion) {
                case "posicion":
                    equipo = pedido[2].trim();
                    if(equipo.equalsIgnoreCase("miEquipo"))
                        result = footballService.getPosicionEquipo(equipoAbuelo);
                    else
                        result = footballService.getPosicionEquipo(equipo);
                    loQueDiceYescribe(result,"posicionEquipo");
                    break;
                case "topN":
                    int n = Integer.parseInt(pedido[2].trim());
                    result = footballService.getTopNEquipos(n);
                    loQueDiceYescribe(result,"topN");
                    break;
                case "bottomN":
                    int j = Integer.parseInt(pedido[2].trim());
                    result = footballService.getBottomNEquipos(j);
                    loQueDiceYescribe(result,"topN");
                    break;
                case "equipoEnPosicion":
                    int posicion = Integer.parseInt(pedido[2].trim());
                    result = footballService.getEquipoEnPosicion(posicion);
                    loQueDiceYescribe(result,"equipoEnPosicion");
                    break;
                case "proximoPartido":
                    equipo = pedido[2].trim();
                    if(equipo.equalsIgnoreCase("miEquipo"))
                        result = footballService.getProximoPartido(equipoAbuelo);
                    else
                        result = footballService.getProximoPartido(equipo);
                    loQueDiceYescribe(result,"proximoPartido");
                    break;
                case "ultimoPartido":
                    equipo = pedido[2].trim();
                    if(equipo.equalsIgnoreCase("miEquipo"))
                        result = footballService.getUltimoPartido(equipoAbuelo);
                    else
                        result = footballService.getUltimoPartido(equipo);
                    loQueDiceYescribe(result,"ultimoPartido");
                    break;
                case "datos":
                    equipo = pedido[2].trim();
                    if(equipo.equalsIgnoreCase("miEquipo"))
                        result = footballService.getDatosEquipo(equipoAbuelo);
                    else
                        result = footballService.getDatosEquipo(equipo);
                    loQueDiceYescribe(result,"datosEquipo");
                    break;
                case "estadisticas":
                    equipo = pedido[2].trim();
                    if(equipo.equalsIgnoreCase("miEquipo"))
                        result = footballService.getEstadisticasEquipo(equipoAbuelo);
                    else
                        result = footballService.getEstadisticasEquipo(equipo);
                    loQueDiceYescribe(result,"estadisticasEquipo");
                    break;
                case "comparacion":
                    String equipo1 = pedido[2].trim();
                    String equipo2 = pedido[3].trim();
                    if (!equipo1.equalsIgnoreCase(equipo2)) {
                        if (equipo1.equalsIgnoreCase("miEquipo"))
                            result = footballService.getComparacionEquipos(equipoAbuelo, equipo2);
                        else if (equipo2.equalsIgnoreCase("miEquipo"))
                            result = footballService.getComparacionEquipos(equipo1, equipoAbuelo);
                        else
                            result = footballService.getComparacionEquipos(equipo1, equipo2);
                        loQueDiceYescribe(result, "comparacionEquipos");
                    } else
                        loQueDiceYescribe("Ambos equipos son iguales, no se puede comparar", "comparacionEquipos");
                    break;
            }
        } else {
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
                    loQueDiceYescribe(speech,"default");
                    break;
            }
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
                        loQueDiceYescribe("Hoy si","default");
                    }
                }

                if(a == 0) {
                    loQueDiceYescribe("Hoy no","default");
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
                       loQueDiceYescribe("Hoy si","default");
                    }
                }

                if(a == 0) {
                     loQueDiceYescribe("Hoy no","default");
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
                            loQueDiceYescribe(getSaludo() + ", " + nombreAbuelo + "! ","default");
                            equipoAbuelo = u.equipo;
                            interactionsService.guardarInteraccion(
                                mailQueInicioSesion, getString(R.string.interaccionTitulo_AbrirApp), "-", "-");
                            if(!vieneDeNotificacion)
                                iniciarServicioScheduler();
                            break;
                        case "tarde":
                            if(TextUtils.isEmpty(u.remediosTarde)){
                                loQueDiceYescribe("Nada que tomar","default");
                            }
                            else{
                                loQueDiceYescribe("Tenés que tomar " + u.remediosTarde,"default");}
                             break;
                        case "mañana":
                           if(TextUtils.isEmpty(u.remediosManiana)){
                                loQueDiceYescribe("Nada que tomar","default");
                            }
                            else{
                                loQueDiceYescribe("Tenés que tomar " + u.remediosManiana,"default");}
                          break;
                        case "noche":
                            if(TextUtils.isEmpty(u.remediosNoche)){
                                loQueDiceYescribe("Nada que tomar","default");
                            }
                            else{
                                loQueDiceYescribe("Tenés que tomar " + u.remediosNoche,"default");}
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

    public void loQueDiceYescribe(String texto, String id){
        try {
            resultTextView.setText(texto);
            myTTS.speak(texto, 0, null, id);
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al emitir respuesta.", e);
        }
    }

    private void interrumpirBotty(){
        if(myTTS!=null && myTTS.isSpeaking())
            myTTS.stop();
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
        contadorClicksEditarRegistro =0;
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

            loQueDiceYescribe("¿Queres ir a pasear?", "default");
        }

    }

}