/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.api.sample;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

/**
 * Created by alexey on 07/12/16.
 */
public class AITextSampleActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, AIListener {

    //ESTO USAMOS!

    public static final String TAG = AITextSampleActivity.class.getName();

    private AIService aiService;
    private String clientAccessToken = "3d0b0b12561c4040963f4e4f529527c7";

    private Gson gson = GsonFactory.getGson();

    private TextView resultTextView;
    private EditText queryEditText;
    private Button micButton;

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

        setContentView(R.layout.activity_aitext_sample);

        resultTextView = (TextView) findViewById(R.id.resultTextView);
        queryEditText = (EditText) findViewById(R.id.textQuery);
        queryEditText.setVisibility(View.VISIBLE);

        micButton = (Button) findViewById(R.id.micButton);

        //PARA EL MENSAJE ANTERIOR - PARA MOSTRARLO
        resultTextViewAnterior = (TextView) findViewById(R.id.resultTextViewAnterior);

        findViewById(R.id.buttonSend).setOnClickListener(this);
        findViewById(R.id.buttonClear).setOnClickListener(this);
        findViewById(R.id.buttonClearHistorial).setOnClickListener(this);

        final AIConfiguration config = new AIConfiguration(clientAccessToken, AIConfiguration.SupportedLanguages.Spanish, AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        aiDataService = new AIDataService(this, config);
    }

    public void micButtonOnClick(final View view) {
        Toast.makeText(this, "Escuchando...", Toast.LENGTH_SHORT).show();
        micButton.setText("Detener");
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
        micButton.setText("Hablar");
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
    private void sendRequest() {

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
                String query = params[0];
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
                } else {
                    onError(aiError);
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
        if (currentHourIn24Format < 13) {
            return "Buenos días";
        } else if (currentHourIn24Format > 12 && currentHourIn24Format < 19) {
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
                TTS.speak(speech);
                break;
            case "tarde":
                resultTextView.setText("2 paracetamol");
                TTS.speak("2 paracetamol");
                break;
            case "mañana":
                resultTextView.setText("1 paracetamol");
                TTS.speak("1 paracetamol");
                break;
            case "noche":
                resultTextView.setText("3 paracetamol");
                TTS.speak("3 paracetamol");
                break;
            default:
                resultTextView.setText(speech);
                TTS.speak(speech);
                break;
        }
    }

    public void onResult(final AIResponse response) {
        mostrarMensajeAnterior();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onResult");

                //resultTextView.setText(gson.toJson(response));

                Log.i(TAG, "Received success response");

                // this is example how to get different parts of result object
                final Status status = response.getStatus();
                Log.i(TAG, "Status code: " + status.getCode());
                Log.i(TAG, "Status type: " + status.getErrorType());

                final Result result = response.getResult();
                Log.i(TAG, "Resolved query: " + result.getResolvedQuery());
                queryEditText.setText(result.getResolvedQuery());

                Log.i(TAG, "Action: " + result.getAction());

                //ACA ES DONDE SE USA SPEECH PARA PEDIRLE QUE LO DIGA EN VOZ ALTA Y LO ESCRIBA
                speech = result.getFulfillment().getSpeech();
                Log.i(TAG, "Speech: " + speech);
                personalizarMensaje();

                final Metadata metadata = result.getMetadata();
                if (metadata != null) {
                    Log.i(TAG, "Intent id: " + metadata.getIntentId());
                    Log.i(TAG, "Intent name: " + metadata.getIntentName());
                }

                final HashMap<String, JsonElement> params = result.getParameters();
                if (params != null && !params.isEmpty()) {
                    Log.i(TAG, "Parameters: ");
                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                        Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    }
                }
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_aibutton_sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(AISettingsActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startActivity(Class<?> cls) {
        final Intent intent = new Intent(this, cls);
        startActivity(intent);
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
}