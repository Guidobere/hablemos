package app.hablemos.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
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
import app.hablemos.footballActions.FootballServiceActions;
import app.hablemos.model.Recordatorio;
import app.hablemos.model.SacadorDeAcentos;
import app.hablemos.model.User;
import app.hablemos.services.FootballService;
import app.hablemos.services.InteractionsService;
import app.hablemos.services.NotificationService;
import app.hablemos.util.DateUtils;

public class MainActivity extends AppCompatActivity implements AIListener , View.OnClickListener , AdapterView.OnItemSelectedListener, TextToSpeech.OnInitListener {
    private String TAG;

    private String queryString;

    //Subintents
    private final int TTS_CHECK_CODE = 0; // Para verificar servicio TextToSpeach
    private final int MODIF_CONFIG_CODE = 1; // Para modificar configuración

    //Permisos
    private static final int REQUEST_PERMISSIONS_ID = 33;
    private static final int REQUEST_AUDIO_PERMISSION_ON_BUTTON_CLICK_ID = 133;

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

    private boolean yaSaludo = false;
    private boolean vieneDeNotificacion;
    private boolean rtaNotificacionManejada;
    private boolean esAutomatico = false;
    private boolean recordPermissionGranted = false;
    private boolean esHablado = false;

    //Multi Tap en el boton "editar registro".
    public int contadorClicksEditarRegistro;

    //FIREBASE
    DatabaseReference usersFb;
    DatabaseReference glucosaFb;
    DatabaseReference presionFb;
    DatabaseReference interaccionesFb;

    private InteractionsService interactionsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        this.usersFb = FirebaseDatabase.getInstance().getReference().child(getString(R.string.fbUsuarios));
        this.glucosaFb = FirebaseDatabase.getInstance().getReference().child(getString(R.string.fbRecordatoriosGlucosa));
        this.presionFb = FirebaseDatabase.getInstance().getReference().child(getString(R.string.fbRecordatoriosPresion));
        this.interaccionesFb = FirebaseDatabase.getInstance().getReference().child(getString(R.string.rbInteracciones));

        interactionsService = new InteractionsService(getBaseContext(), getAssets(), interaccionesFb);

        setContentView(R.layout.nuevomain);

        mailQueInicioSesion = getIntent().getExtras().getString("1");
        vieneDeNotificacion = getIntent().getExtras().getBoolean("vieneDeNotificacion");
        rtaNotificacionManejada = false;
        contadorClicksEditarRegistro = 0;

        TAG = getString(R.string.tagMain);

        diaSemana = DateUtils.getDiaSemanaActual();

        HORARIO_MANANA = Integer.parseInt(getString(R.string.horarioManiana));
        HORARIO_TARDE = Integer.parseInt(getString(R.string.horarioTarde));
        HORARIO_NOCHE = Integer.parseInt(getString(R.string.horarioNoche));

        resultTextView = (TextView) findViewById(R.id.resultTextView);
        resultTextView2 = (TextView) findViewById(R.id.resultTextView2);
        queryEditText = (EditText) findViewById(R.id.textQuery);
        queryEditText.setVisibility(View.VISIBLE);

        micButton = (Button) findViewById(R.id.micButton);
        send = (Button) findViewById(R.id.buttonSend);

        send.setOnClickListener(this);

        final AIConfiguration config = new AIConfiguration(getString(R.string.accessToken), AIConfiguration.SupportedLanguages.Spanish, AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        aiDataService = new AIDataService(this, config);

        footballService = new FootballService(this);

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
        startActivityForResult(checkTTSIntent, TTS_CHECK_CODE);
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
                    contadorClicksEditarRegistro = 0;
                    //resultTextView.setText("Done");
                    //Log.w("contador tiempo", "done");
                }
            }.start();

            if(contadorClicksEditarRegistro > 5){
                interrumpirBotty();
                Intent intent = new Intent(this,RegistroActivity.class);
                startActivityForResult(intent, MODIF_CONFIG_CODE);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private String parsearNombre(String nombre) {
        String ret = "";
        try{
            String [] nombres = nombre.trim().split(" ");
            for (int i=0; i<nombres.length; i++) {
                String s1 = nombres[i].substring(0, 1).toUpperCase();
                ret += " " + s1 + nombres[i].substring(1).toLowerCase();
            }
        } catch (Exception e) {
            ret = nombre;
        }
        return ret.trim();
    }

    //Resultado de subintents
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {

            // Vuelve de verificar el servicio TextToSpeach
            case (TTS_CHECK_CODE) : {
                List<String> permisosList = new ArrayList();

                //Permiso de grabar audio para el botón Hablar
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    permisosList.add(Manifest.permission.RECORD_AUDIO);
                } else recordPermissionGranted = true;

                //Permiso de ubicación para el clima
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permisosList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                }

                if(permisosList.size()>0) {
                    String[] permisos = new String[permisosList.size()];
                    permisos = permisosList.toArray(permisos);
                    ActivityCompat.requestPermissions(this, permisos, REQUEST_PERMISSIONS_ID);
                }


                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    //the user has the necessary data - create the TTS
                    myTTS = new TextToSpeech(this, this);
                } else {
                    //no data - install it now
                    Intent installTTSIntent = new Intent();
                    installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installTTSIntent);
                }

                break;
            }
            // Vuelve de cambiar la configuración
            case (MODIF_CONFIG_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    User u = (User) data.getSerializableExtra("user");
                    equipoAbuelo = u.equipo;
                    iniciarServicioScheduler(u, true);
                }
                break;
            }
        }

    }

    //setup TTS
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            myTTS.setLanguage(new Locale("es", "AR"));
            if(vieneDeNotificacion) {
                send.onEditorAction(EditorInfo.IME_ACTION_DONE);
                if(!rtaNotificacionManejada) {
                    manejarRespuestaNotificacion(getIntent().getExtras());
                    rtaNotificacionManejada = true;
                    vieneDeNotificacion = false;
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
                new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_ON_BUTTON_CLICK_ID);
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
            case REQUEST_PERMISSIONS_ID: {
                int audioPermissionIndex = Arrays.asList(permissions).indexOf(Manifest.permission.RECORD_AUDIO);
                //int locationPermissionIndex = Arrays.asList(permissions).indexOf(Manifest.permission.ACCESS_COARSE_LOCATION);
                if (audioPermissionIndex >= 0 && grantResults[audioPermissionIndex] == PackageManager.PERMISSION_GRANTED) {
                    recordPermissionGranted = true;
                } else {
                    Toast.makeText(this, getString(R.string.permisoGrabarDenegado), Toast.LENGTH_LONG).show();
                    myTTS.speak(getString(R.string.permisoGrabarDenegado), 0, null, "default");
                }
                break;
            }
            case REQUEST_AUDIO_PERMISSION_ON_BUTTON_CLICK_ID: {
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
        esHablado = true;
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

        if (!esAutomatico) {
            resultTextView2.setText(String.valueOf(queryEditText.getText()));
            queryString = SacadorDeAcentos.stripAccents(String.valueOf(queryEditText.getText()));
            queryEditText.setText("");
        }

        if (TextUtils.isEmpty(queryString)) {
            resultTextView2.setText("");
            onError(new AIError(getString(R.string.non_empty_query)));
            myTTS.speak(getString(R.string.non_empty_query), 0, null, "default");
            return;
        }

        send.onEditorAction(EditorInfo.IME_ACTION_DONE);

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

    public void personalizarMensaje(String loQueDijo){
        if (speech.startsWith("futbol")) {
            String[] pedido = speech.split("_");
            String accion = pedido[1].trim().toUpperCase();
            try {
                FootballServiceActions action = FootballServiceActions.valueOf(accion);
                String result = action.getFootballActionExecutor().ejecutarAccion(pedido, equipoAbuelo, footballService);
                if (result.equalsIgnoreCase("irAlMenu")) {
                    irAlMenu("menu_futbol", 6);
                } else if (result.equalsIgnoreCase("irAlMenuYa")) {
                    irAlMenu("menu_futbol", 0);
                } else {
                    loQueDiceYescribe(result, action.getAccion(), true);
                }
            } catch (Exception e) {
                loQueDiceYescribe("Eso todavía no aprendí a contestarlo, preguntame otra cosa", "errorFutbol", false);
                irAlMenu("menu_futbol", 6);
            }
        } else if(speech.startsWith("medicamentos _") && speech.split(" _ ").length>1) {
            pedirAlaBase(speech.split(" _ ")[1]);
        } else if(speech.startsWith("chequeoSalud _") && speech.split(" _ ").length>1) {
            personalizarMensajeRevisionSalud(loQueDijo);
        } else if(speech.startsWith("glucosa-") && speech.split("-").length>1) {
            pedirAlaBaseSobreGlucosa(speech.split("-")[1]);
        } else if(speech.startsWith("presión-") && speech.split("-").length>1) {
            pedirAlaBaseSobrePresion(speech.split("-")[1]);
        } else {
            switch (speech) {
                case "caminarno":
                    interactionsService.guardarInteraccion(mailQueInicioSesion,
                            "Notificacion clima", "No",
                            "El usuario no accedio a salir a caminar");
                    loQueDiceYescribe("Entonces escribí o decí: Pastelería, Fútbol o Salud","default", false);
                    break;
                case "caminarsi":
                    interactionsService.guardarInteraccion(mailQueInicioSesion,
                            "Notificacion clima", "Si",
                            "El usuario accedio a salir a caminar");
                    loQueDiceYescribe("¡Buena suerte!","default", false);
                    break;
                case "despedida":
                    salir(2, this);
                    break;
                case "" :
                    speech = "¡No entendí!\nEscriba o diga:\nPastelería,\nFútbol\nSalud";
                    loQueDiceYescribe(speech,"default", false);
                    break;
                default: //Aca no lo modifique por que lo que dice es el mismo speech, los otros lo modificaba
                    loQueDiceYescribe(speech,"default", false);
                    break;
            }
        }
    }

    private void personalizarMensajeRevisionSalud(String loQueDijo) {
        String[] respuesta = speech.split(" _ ");
        String chequeo = respuesta[1]; // glucosa, presión
        String resultado = respuesta[2]; // si, no, ni (no se entendió)
        String observacion = "";
        if(resultado.equals("si")){
            observacion = getString(R.string.interaccionTexto_SaludRevisionSi, chequeo, loQueDijo); // Dijo que si y cuando le dió
        } else if(resultado.equals("no")){
            observacion = getString(R.string.interaccionTexto_SaludRevisionNo, chequeo, respuesta[3]); // Dijo que no y un motivo
        } else  if(resultado.equals("ni")){
            resultado = "-";
            observacion = getString(R.string.interaccionTexto_SaludRevisionNi, chequeo, loQueDijo); // No se entendió lo que dijo, se guarda textual
        }
        interactionsService.guardarInteraccion(mailQueInicioSesion,
            getString(R.string.interaccionTitulo_RevisionSalud, chequeo), parsearNombre(resultado), observacion);
        loQueDiceYescribe(getString(R.string.graciasAvisoRevision),"default", false);
        irAlMenu("menu_principal", 3);
    }

    private void irAlMenu(final String menu, int segundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                queryString = menu;
                esAutomatico = true;
                sendRequest();
            }
        }, 1000*segundos);
    }

    private void salir(int segundos, final Activity activity) {
        loQueDiceYescribe("¡Hasta pronto!", "default",false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                queryString = "";
                resultTextView.setText("");
                resultTextView2.setText("");
                yaSaludo = false;
                activity.moveTaskToBack(true);
            }
        }, 1000*segundos);
    }

    private void saludar(int segundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                loQueDiceYescribe("¡" + getSaludo() + ", " + nombreAbuelo + "! ","default", false);
                yaSaludo = true;
            }
        }, 1000*segundos);
    }

    public void pedirAlaBaseSobrePresion(final String turnoPresion){
        a = 0;
        presionFb.orderByChild("email").equalTo(mailQueInicioSesion).addValueEventListener(new ValueEventListener() {
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
                        loQueDiceYescribe("Hoy a la "+ turnoPresion +" debes medirte la presión","default", false);
                    }
                }

                if(a == 0) {
                    loQueDiceYescribe("Hoy a la "+ turnoPresion +" no debes medirte la presión","default", false);
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
       glucosaFb.orderByChild("email").equalTo(mailQueInicioSesion).addValueEventListener(new ValueEventListener() {
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
                       loQueDiceYescribe("Hoy a la "+ turnoGlucosa +" debes medirte la glucosa","default", false);
                    }
                }

                if(a == 0) {
                     loQueDiceYescribe("Hoy a la "+ turnoGlucosa +" no debes medirte la glucosa","default", false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void pedirAlaBase(final String pedido){
        usersFb.orderByChild("email").equalTo(mailQueInicioSesion).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot users) {

                User u = new User();
                Iterable<DataSnapshot> usersChildren = users.getChildren();
                for (DataSnapshot user : usersChildren) {
                    u = user.getValue(User.class);
                }

                if (u != null && u.username != null) {
                    switch (pedido) {
                        case "saludo":
                            nombreAbuelo = parsearNombre(u.username);
                            loQueDiceYescribe("¡" + getSaludo() + ", " + nombreAbuelo + "! ","default", false);
                            equipoAbuelo = u.equipo;
                            interactionsService.guardarInteraccion(
                                mailQueInicioSesion, getString(R.string.interaccionTitulo_AbrirApp), "-", "-");
                            if(!vieneDeNotificacion)
                                iniciarServicioScheduler(u, false);
                            break;
                        case "tarde":
                            if(TextUtils.isEmpty(u.remediosTarde)){
                                loQueDiceYescribe("Nada que tomar a la tarde.","default", false);
                            }
                            else{
                                loQueDiceYescribe("A la tarde tenés que tomar " + u.remediosTarde,"default", false);}
                             break;
                        case "mañana":
                           if(TextUtils.isEmpty(u.remediosManiana)){
                                loQueDiceYescribe("Nada que tomar a la mañana.","default", false);
                            }
                            else{
                                loQueDiceYescribe("A la mañana tenés que tomar " + u.remediosManiana,"default", false);}
                          break;
                        case "noche":
                            if(TextUtils.isEmpty(u.remediosNoche)){
                                loQueDiceYescribe("Nada que tomar a la noche.","default", false);
                            }
                            else{
                                loQueDiceYescribe("A la noche tenés que tomar " + u.remediosNoche,"default", false);}
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Result result = response.getResult();

                //Se muestra el texto cuando se usa el botón Hablar
                if(esHablado && !esAutomatico)
                    resultTextView2.setText(result.getResolvedQuery());

                try{
                    speech = ((ResponseMessage.ResponseSpeech) result.getFulfillment().getMessages().get(0)).getSpeech().get(0);
                } catch (Exception e){
                    speech = result.getFulfillment().getSpeech();
                }

                personalizarMensaje(result.getResolvedQuery());

                esAutomatico = false;
                esHablado = false;
                send.onEditorAction(EditorInfo.IME_ACTION_DONE);
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
                esAutomatico = false;
                esHablado = false;
                resultTextView2.setText("");
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
            case R.id.buttonSend: {
                sendRequest();
                break;
            }
        }
    }

    public void loQueDiceYescribe(String texto, String id, boolean revisarPronunciacion){
        try {
            send.onEditorAction(EditorInfo.IME_ACTION_DONE);
            resultTextView.setText(texto);
            if(revisarPronunciacion)
                texto = this.getTextoPronunciacionCorrecta(texto);
            myTTS.speak(texto, 0, null, id);
        } catch (Exception e){
            Log.e(this.getClass().getName(), "Error al emitir respuesta.", e);
        }
    }

    private String getTextoPronunciacionCorrecta(String texto) {
        return texto.replace("Racing Club", "Rácing Club")
            .replace("Velez Sarsfield", "Velez Sársfield")
            .replace("Heinze", "Géinze")
            .replace("River Plate", "Ríver Plate");
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

    public void iniciarServicioScheduler(User user, boolean reset){
        Intent intent = new Intent(this, SchedulerService.class);
        Bundle mBundle = new Bundle();
        mBundle.putBoolean("reset", reset);
        mBundle.putSerializable("user", user);
        intent.putExtras(mBundle);
        startService(intent);
    }

    public void onStop(){
        super.onStop();
        interrumpirBotty();
    }

    public void onResume(){
        contadorClicksEditarRegistro = 0;
        super.onResume();
        if(!yaSaludo && !TextUtils.isEmpty(nombreAbuelo)){
            saludar(1);
        }
    }

    @Override
    protected void onDestroy() {
        interrumpirBotty();
        super.onDestroy();
    }

    private void manejarRespuestaNotificacion(Bundle extras) {
        int tipoNotificacion = extras.getInt("tipoNotificacion");

        if(tipoNotificacion == NotificationService.ID_NOTIFICACION_SALUD){
            String turno = extras.getString("extraInfo");
            pedirAlaBase(turno);
            interactionsService.guardarInteraccion(mailQueInicioSesion,
                getString(R.string.interaccionTitulo_Notificacion, "medicamentos"), "Si",
                getString(R.string.interaccionTexto_Notificacion, "abrió", "medicamentos"));
        }

        else if (tipoNotificacion == NotificationService.ID_NOTIFICACION_PRESION){
            esAutomatico=true;
            queryString="presion_preguntar_si_se_la_tomo";
            sendRequest();
        }

        else if (tipoNotificacion == NotificationService.ID_NOTIFICACION_GLUCOSA){
            esAutomatico=true;
            queryString="glucosa_preguntar_si_se_la_tomo";
            sendRequest();
        }

        else if(tipoNotificacion == NotificationService.ID_NOTIFICACION_CLIMA_LINDO){
            esAutomatico=true;
            queryString="op_caminar";
            sendRequest();
        }

        else if(tipoNotificacion == NotificationService.ID_NOTIFICACION_CLIMA_FUERTE){
            loQueDiceYescribe("Tomar mucha agua durante todo el día\nEvitar las bebidas alcohólicas, muy dulces y las infusiones calientes\nUsar ropa suelta, de materiales livianos y de colores claros\nno exponerse al sol","default", false);
        }

    }

}
