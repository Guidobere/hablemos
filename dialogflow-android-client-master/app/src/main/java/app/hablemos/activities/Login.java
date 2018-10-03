package app.hablemos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.hablemos.R;


public class Login extends AppCompatActivity {

    private TextView txtRegistrar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String TAG = getString(R.string.tagLogin);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //LOGICA BOTON LOGIN
        Button button = findViewById(R.id.Ingresar);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                singIn();
                //loguearUsuario();
                //crearUsuario(user,pass);
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        //REGISTRARSE
        txtRegistrar = (TextView) findViewById(R.id.textRegistro);
        txtRegistrar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //startActivity(RegistroActivity.class);
                crearUsuario();
            }
        });
    }

/*    private void login(){
        EditText userTxtBox = (EditText) findViewById(R.id.txtUser);
        EditText passTxtBox = (EditText) findViewById(R.id.txtPass);

        final String email = userTxtBox.getText().toString();
        final String password = passTxtBox.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }*/

    //CREAR O LOGEAR USUARIO
    private void crearUsuario() {
        EditText userTxtBox = (EditText) findViewById(R.id.txtUser);
        EditText passTxtBox = (EditText) findViewById(R.id.txtPass);

        final String email = userTxtBox.getText().toString();
        final String password = passTxtBox.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(MainActivity.class);
                        //updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(Login.this, getString(R.string.fallo_autenticacion),
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

    //Create a new createAccount method which takes in an email address and password, validates them and then creates a new user with the createUserWithEmailAndPassword method.
    private void singIn(){
        EditText userTxtBox = (EditText) findViewById(R.id.txtUser);
        EditText passTxtBox = (EditText) findViewById(R.id.txtPass);
        String email = userTxtBox.getText().toString();
        String password = passTxtBox.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(MainActivity.class);
                        //updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(Login.this, getString(R.string.fallo_autenticacion),
                                Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }
                    // ...
                }
            });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}