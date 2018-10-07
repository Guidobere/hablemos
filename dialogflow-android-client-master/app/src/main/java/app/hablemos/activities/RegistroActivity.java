package app.hablemos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.hablemos.R;
import app.hablemos.model.User;


public class RegistroActivity extends AppCompatActivity {

    private String TAG;

    private EditText nombreAbuelo;
    private EditText mailTutor;
    private EditText equipoFavorito;
    private EditText contra;
    private EditText medicamentosM;
    private EditText medicamentosT;
    private EditText medicamentosN;

    private Button botonRegistro;

    //private static HashMap<String, User> users = new HashMap<>();
    //ACCEDO A LOS USUARIOS DE FIREBASE y uso esta instancia como global
    DatabaseReference myUsersFb =FirebaseDatabase.getInstance().getReference().child("users");

    //ESto es para la autenticacion
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //  private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cinicial);

        TAG = getString(R.string.tagRegistro);

        nombreAbuelo = findViewById(R.id.txtAbuelo);
        mailTutor = findViewById(R.id.txtEmail);
        equipoFavorito = findViewById(R.id.txtEquipo);
        contra = findViewById(R.id.txtContra);
        medicamentosM = findViewById(R.id.txtmañana);
        medicamentosT = findViewById(R.id.txttarde);
        medicamentosN = findViewById(R.id.txtnoche);
        botonRegistro = findViewById(R.id.btnRegister);

        // Attach a listener to read the data at our posts reference
       /* myUsersFb.orderByKey().addValueEventListener(new ValueEventListener() {
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
        }); */

        //mailTutor.setText(usuario.email);

        //LOGICA PARA QUE AL HACER CLICK EN EL BOTON ENVIE LOS DATOS A LA FUNCION writeNewUser

        botonRegistro.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                //Al usar autenticacion no hay que guardar aca la contraseña!
                //Habria que poner alguna forma de manejar algo que ocurra malo!

                //Aca lo guarda para que lo podamos autenticar
                crearUsuario();

                //Aca lo mete a la database
                writeNewUser( nombreAbuelo.getText().toString(),mailTutor.getText().toString(),equipoFavorito.getText().toString(),medicamentosM.getText().toString(),medicamentosT.getText().toString(),medicamentosN.getText().toString());

            }
        });
    }

    //FUNCION PARA CREAR USUARIO EN FIREBASE
    private void writeNewUser(String username, String email,String equipo,String remediosM,String remediosT,String remediosN) {
        //Inserto un nuevo valor con su respectivo ID y le Asigno el valor del nuevo
        myUsersFb.push().setValue(new User(username,email,equipo,remediosM,remediosT,remediosN));

    }

    private void crearUsuario() {
        String email = mailTutor.getText().toString();
        String password = contra.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmailAndPassword:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(Login.class);
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmailAndPassword:failure", task.getException());
                            Toast.makeText(RegistroActivity.this, getString(R.string.fallo_autenticacion),
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        // ...
                    }
                });
    }

    private void startActivity(Class<?> cls) {
        final Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (true) {
        } else {
            super.onBackPressed();
        }
    }
}