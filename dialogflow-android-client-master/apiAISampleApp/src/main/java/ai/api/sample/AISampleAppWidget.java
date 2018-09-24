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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;


/**
 * Implementation of App Widget functionality.
 */
public class AISampleAppWidget extends AppWidgetProvider {
    private static final String ACTION_KEY = "tap_on_icon";
    @Override
    public void onReceive(final Context context, final Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(ACTION_KEY)) {

            final Intent popUpIntent = new Intent(context, AIWidgetActivity.class);

            popUpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(popUpIntent);
            Toast.makeText(context.getApplicationContext(), "API.AI Dialog shown",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(final Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(final Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ai_app_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        final Intent intent = new Intent(context, AISampleAppWidget.class);
        intent.setAction(ACTION_KEY);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_new_app_widget, pendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

