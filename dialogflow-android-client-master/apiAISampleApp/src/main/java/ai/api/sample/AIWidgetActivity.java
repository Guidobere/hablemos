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
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import ai.api.android.AIConfiguration;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.ui.AIDialog;


public class AIWidgetActivity extends ActionBarActivity {

    private AIDialog aiDialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_sample);
        final AIConfiguration config = new AIConfiguration(Config.ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiDialog = new AIDialog(this, config);
        aiDialog.setResultsListener(new AIDialog.AIDialogListener() {
            @Override
            public void onResult(final AIResponse aiResponse) {
                // TODO Process aiResponse
                aiDialog.close();
                Toast.makeText(getApplicationContext(), String.format("%s %s","Successful response: ",
                        aiResponse.getResult().getResolvedQuery()), Toast.LENGTH_SHORT).show();
                AIWidgetActivity.this.finish();
            }

            @Override
            public void onError(final AIError aiError) {
                // TODO show error message
                aiDialog.close();
                Toast.makeText(getApplicationContext(), aiError.getMessage(), Toast.LENGTH_SHORT).show();
                AIWidgetActivity.this.finish();
            }

            @Override
            public void onCancelled() {
                aiDialog.close();
                Toast.makeText(getApplicationContext(), "Process cancelled", Toast.LENGTH_SHORT).show();
                AIWidgetActivity.this.finish();
            }

        });

        aiDialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface dialog) {
                Toast.makeText(getApplicationContext(), "Dialog dismissed by user", Toast.LENGTH_SHORT).show();
                AIWidgetActivity.this.finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        aiDialog.showAndListen();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
