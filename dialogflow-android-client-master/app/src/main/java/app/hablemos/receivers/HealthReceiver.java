package app.hablemos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import app.hablemos.model.User;
import app.hablemos.services.HealthService;
import app.hablemos.services.NotificationService;

public class HealthReceiver extends BroadcastReceiver {

    HealthService healthService;

    public HealthReceiver() {
        healthService = new HealthService();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String nombreAbuelo = intent.getExtras().getString("nombreAbuelo");
        String mailQueInicioSesion = intent.getExtras().getString("mailQueInicioSesion");
        String turno = intent.getExtras().getString("turno");
        healthService.sendHealthNotifications(context, nombreAbuelo, mailQueInicioSesion, turno);
    }
}
