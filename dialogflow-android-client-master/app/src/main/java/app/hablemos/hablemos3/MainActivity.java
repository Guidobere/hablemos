package app.hablemos.hablemos3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.android.GsonFactory;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;

public class MainActivity extends AppCompatActivity implements AIListener , View.OnClickListener , AdapterView.OnItemSelectedListener, TextToSpeech.OnInitListener {

    //TTS object
    private TextToSpeech myTTS;
    //status check code
    private int MY_DATA_CHECK_CODE = 0;
    private AIService aiService;
    private String clientAccessToken = "3d0b0b12561c4040963f4e4f529527c7";
    private static final int REQUEST_AUDIO_PERMISSIONS_ID = 33;

    private Gson gson = GsonFactory.getGson();

    private TextView resultTextView;
    private EditText queryEditText;
    private Button micButton;

    private Button bRegistro;

    private int HORARIO_MANANA = 6;
    private int HORARIO_TARDE = 12;
    private int HORARIO_NOCHE = 20;

    //PARA EL MENSAJE ANTERIOR

        //DONDE SE GUARDA EL MSJ ANTERIOR
    private String dialogoAnterior="Mensaje anterior";
       //Donde se muestra el msj anterior
    private TextView resultTextViewAnterior;

    //Lo que dice y muestra en la aplicacion
    private String speech;

    //nombre abuelo
    private String nombreAbuelo="Carolina";

    private AIDataService aiDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_main);

        resultTextView = (TextView) findViewById(R.id.resultTextView);
        queryEditText = (EditText) findViewById(R.id.textQuery);
        queryEditText.setVisibility(View.VISIBLE);

        micButton = (Button) findViewById(R.id.micButton);
        bRegistro = (Button) findViewById(R.id.buttonRegistro);

        //PARA EL MENSAJE ANTERIOR - PARA MOSTRARLO
        resultTextViewAnterior = (TextView) findViewById(R.id.resultTextViewAnterior);

        findViewById(R.id.buttonSend).setOnClickListener(this);
        findViewById(R.id.buttonClear).setOnClickListener(this);
        findViewById(R.id.buttonClearHistorial).setOnClickListener(this);

        final AIConfiguration config = new AIConfiguration(clientAccessToken, AIConfiguration.SupportedLanguages.Spanish, AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        aiDataService = new AIDataService(this, config);

        //check for TTS data
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }

    //act on result of TTS data check
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSIONS_ID);
        }
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
        }
    }

    public void micButtonOnClick(final View view) {
        Toast.makeText(this, "Escuchando...", Toast.LENGTH_SHORT).show();
        micButton.setEnabled(false);
        aiService.startListening();
    }

    @Override
    public void onListeningStarted() {
        // show recording indicator
    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {
        micButton.setEnabled(true);
    }

    @Override
    public void onAudioLevel(final float level) {
        // show sound level
    }

    private void clearEditText() {
        queryEditText.setText("");
    }

    //Aca te borra el ultimo mensaje, su anterior y el texto.
    private void clearEditTextHistorial() {
        resultTextViewAnterior.setText("");
        resultTextView.setText("");
        queryEditText.setText("");
        dialogoAnterior="";
        speech="";
    }

    /*
     * AIRequest should have query OR event
     */
        private void sendRequest()  {

            final String queryString = String.valueOf(queryEditText.getText());

        if (TextUtils.isEmpty(queryString)) {
            onError(new AIError(getString(R.string.non_empty_query)));
            return;
        }

        final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {

            private AIError aiError;

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

    public void mostrarMensajeAnterior(){
      dialogoAnterior=speech;
      resultTextViewAnterior.setText(speech);
    }

    private String getSaludo() {
        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
        if (currentHourIn24Format >= HORARIO_MANANA && currentHourIn24Format < HORARIO_TARDE) {
            return "Buenos días";
        } else if (currentHourIn24Format >= HORARIO_TARDE && currentHourIn24Format < HORARIO_NOCHE) {
            return "Buenas tardes";
        } else {
            return "Buenas noches";
        }
    }

    public void personalizarMensaje(){
        switch (speech) {
            case "Te puedo ofrecer hablar de: Pasteleria, Futbol y Salud":
                speech = getSaludo() + " " + nombreAbuelo + "! " + speech;
                resultTextView.setText(speech);
                myTTS.speak(speech,0,null, "saludo");
                break;
            case "tarde":
                speech = "2 paracetamol";
                resultTextView.setText(speech);
                myTTS.speak(speech,0,null, "tarde");
                break;
            case "mañana":
                speech = "1 paracetamol";
                resultTextView.setText(speech);
                myTTS.speak(speech,0,null, "maniana");
                break;
            case "noche":
                speech = "3 paracetamol";
                resultTextView.setText(speech);
                myTTS.speak(speech,0,null, "noche");
                break;
            default:
                resultTextView.setText(speech);
                myTTS.speak(speech,0,null, "default");
                break;
        }
    }

    public void onResult(final AIResponse response) {
        mostrarMensajeAnterior();
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
                resultTextView.setText(error.toString());
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
                    clearEditText();
                    break;
            case R.id.buttonClearHistorial:
                clearEditTextHistorial();
                break;
                case R.id.buttonSend:
                    sendRequest();
                    break;
            }
        }

    public void abrirVentanaRegistro(final View view) {
        startActivity(RegistroActivity.class);
    }

    private void startActivity(Class<?> cls) {
        final Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}