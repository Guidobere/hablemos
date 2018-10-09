package app.hablemos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;

import app.hablemos.services.InteractionsService;

public class EmailReceiver extends BroadcastReceiver {

    public EmailReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String nombreAbuelo = intent.getExtras().getString("nombreAbuelo");
        String mailQueInicioSesion = intent.getExtras().getString("mailQueInicioSesion");
        InteractionsService interactionsService = new InteractionsService(context, context.getAssets(), FirebaseDatabase.getInstance().getReference().child("interacciones"));
        interactionsService.enviarReporteInteracciones(nombreAbuelo, mailQueInicioSesion);
    }
}
