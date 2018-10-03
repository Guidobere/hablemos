package app.hablemos.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import app.hablemos.R;
import app.hablemos.model.User;


public class RegistroActivity extends AppCompatActivity {

    private EditText nombreAbuelo;
    private EditText mailTutor;
    private EditText equipoFavorito;
    private EditText medicamentosM;
    private EditText medicamentosT;
    private EditText medicamentosN;

    private static HashMap<String, User> users = new HashMap<>();
    //ACCEDO A LOS USUARIOS DE FIREBASE y uso esta instancia como global
    DatabaseReference myUsersFb =FirebaseDatabase.getInstance().getReference().child("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracion_inicial);

        nombreAbuelo = (EditText) findViewById(R.id.txtAbuelo);
        mailTutor = (EditText) findViewById(R.id.txtEmail);
        equipoFavorito = (EditText) findViewById(R.id.txtEquipo);
        medicamentosM = (EditText) findViewById(R.id.txtmañana);
        medicamentosT = (EditText) findViewById(R.id.txttarde);
        medicamentosN = (EditText) findViewById(R.id.txtnoche);

        // Attach a listener to read the data at our posts reference
        myUsersFb.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //User user = new User();
                //String email = dataSnapshot.getValue("email").toString();
                User u = new User();
                DataSnapshot users = dataSnapshot;
                Iterable<DataSnapshot> usersChildren = users.getChildren();
                for (DataSnapshot user : usersChildren) {
                    u = user.getValue(User.class);
                    //Log.d("User:: ", u.username+ " " + u.email);
                }
                mailTutor.setText(u.email);
                nombreAbuelo.setText(u.username);
                equipoFavorito.setText(u.equipo);
                medicamentosM.setText(u.remediosManiana);
                medicamentosT.setText(u.remediosTarde);
                medicamentosN.setText(u.remediosNoche);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        //mailTutor.setText(usuario.email);

        //LOGICA PARA QUE AL HACER CLICK EN EL BOTON ENVIE LOS DATOS A LA FUNCION writeNewUser
        Button button = (Button) findViewById(R.id.btnLogin);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                writeNewUser(
                        nombreAbuelo.getText().toString(),
                        mailTutor.getText().toString(),
                        equipoFavorito.getText().toString(),
                        medicamentosM.getText().toString(),
                        medicamentosT.getText().toString(),
                        medicamentosN.getText().toString()
                );
            }
        });
    }

    //FUNCION PARA CREAR USUARIO EN FIREBASE
    private void writeNewUser(String username, String email,String equipo,String remediosM,String remediosT,String remediosN) {
        User user = new User(username, email,equipo,remediosM,remediosT,remediosN);
        myUsersFb.push().setValue(new User(username,email,equipo,remediosM,remediosT,remediosN));//Inserto un nuevo valor con su respectivo ID y le Asigno el valor del nuevo
    }
}