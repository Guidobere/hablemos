package app.hablemos.services;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import app.hablemos.R;
import app.hablemos.model.Recordatorio;
import app.hablemos.model.User;
import app.hablemos.util.DateUtils;

public class HealthService {

    public void sendHealthNotifications(Context context, final String nombreAbuelo, String mailQueInicioSesion, final String tipo, final String turno) {
        Context localContext = context;
        final NotificationService notificationService = new NotificationService(mailQueInicioSesion);

        if(tipo.equalsIgnoreCase("medicamentos"))
            enviarNotificacionMedicamentos(nombreAbuelo, mailQueInicioSesion, turno, localContext, notificationService);

        if(tipo.equalsIgnoreCase("presion")) {
            DatabaseReference presionFbReference = FirebaseDatabase.getInstance().getReference().child(
                localContext.getString(R.string.fbRecordatoriosPresion));
            enviarNotificacionChequeo(nombreAbuelo, mailQueInicioSesion, turno, "presión",
                presionFbReference, localContext, notificationService);
        }

        if(tipo.equalsIgnoreCase("glucosa")) {
            DatabaseReference glucosaFbReference = FirebaseDatabase.getInstance().getReference().child(
                    localContext.getString(R.string.fbRecordatoriosGlucosa));
            enviarNotificacionChequeo(nombreAbuelo, mailQueInicioSesion, turno, "glucosa",
                    glucosaFbReference, localContext, notificationService);
        }
    }

    private void enviarNotificacionMedicamentos(final String nombreAbuelo, String mailQueInicioSesion, final String turno,
                                                final Context localContext, final NotificationService notificationService) {
        DatabaseReference usersFbReference = FirebaseDatabase.getInstance().getReference().child(localContext.getString(R.string.fbUsuarios));
        usersFbReference.orderByChild("email").equalTo(mailQueInicioSesion).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot users) {

                User u = null;
                Iterable<DataSnapshot> usersChildren = users.getChildren();
                for (DataSnapshot user : usersChildren) {
                    u = user.getValue(User.class);
                }

                if (u != null && u.username != null) {
                    if (turno.equals("mañana") && !TextUtils.isEmpty(u.remediosManiana)) {
                        notificationService.enviarNotificacionMedicamentos(localContext, nombreAbuelo, "mañana");
                    } else if (turno.equals("tarde") && !TextUtils.isEmpty(u.remediosTarde)) {
                        notificationService.enviarNotificacionMedicamentos(localContext, nombreAbuelo, "tarde");
                    } else if (turno.equals("noche") && !TextUtils.isEmpty(u.remediosNoche)){
                        notificationService.enviarNotificacionMedicamentos(localContext, nombreAbuelo, "noche");
                    }

                } else {
                    Log.e(this.getClass().getName(), "Ocurrio un error al obtener el usuario");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(this.getClass().getName(), "Error al conectarse a Firebase: " + databaseError.getCode());
            }
        });
    }

    private void enviarNotificacionChequeo(final String nombreAbuelo, String mailQueInicioSesion, final String turno,
                                           final String chequeo, DatabaseReference chequeoFbReference,
                                           final Context localContext, final NotificationService notificationService) {
        final String diaDeHoy = DateUtils.getDiaSemanaActual();
        chequeoFbReference.orderByChild("email").equalTo(mailQueInicioSesion).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot recordatorios) {

                Recordatorio r;
                Iterable<DataSnapshot> recordatoriosChildren = recordatorios.getChildren();
                for (DataSnapshot recordatorio : recordatoriosChildren) {
                    r = recordatorio.getValue(Recordatorio.class);
                    if (r != null && r.email != null) {
                        if (turno.equals(r.turno) && !TextUtils.isEmpty(r.dias) && r.dias.contains(diaDeHoy)) {
                            notificationService.enviarNotificacionChequeo(localContext, nombreAbuelo, turno, chequeo);
                        }
                    } else {
                        Log.e(this.getClass().getName(), "Ocurrió un error al obtener los recordatorios del usuario");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(this.getClass().getName(), "Error al conectarse a Firebase: " + databaseError.getCode());
            }
        });
    }

}