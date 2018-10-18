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
import app.hablemos.model.User;

public class HealthService {

    public void sendHealthNotifications(Context context, final String nombreAbuelo, String mailQueInicioSesion) {
        final Context localContext = context;
        final NotificationService notificationService = new NotificationService(mailQueInicioSesion);
        final int horaActual = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        final int horarioSaludManiana = Integer.parseInt(context.getString(R.string.horarioSaludManiana));
        final int horarioSaludTarde = Integer.parseInt(context.getString(R.string.horarioSaludTarde));
        final int horarioSaludNoche = Integer.parseInt(context.getString(R.string.horarioSaludNoche));

        DatabaseReference usersFbReference = FirebaseDatabase.getInstance().getReference().child("users");
        usersFbReference.orderByChild("email").equalTo(mailQueInicioSesion).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot users) {

                User u = null;
                Iterable<DataSnapshot> usersChildren = users.getChildren();
                for (DataSnapshot user : usersChildren) {
                    u = user.getValue(User.class);
                }

                if (u != null && u.username != null) {
                    if (horaActual == horarioSaludManiana && !TextUtils.isEmpty(u.remediosManiana)) {
                        notificationService.enviarNotificacionMedicamentos(localContext, nombreAbuelo, "mañana");
                    } else if (horaActual == horarioSaludTarde && !TextUtils.isEmpty(u.remediosTarde)) {
                        notificationService.enviarNotificacionMedicamentos(localContext, nombreAbuelo, "tarde");
                    } else if (horaActual == horarioSaludNoche && !TextUtils.isEmpty(u.remediosNoche)){
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

}