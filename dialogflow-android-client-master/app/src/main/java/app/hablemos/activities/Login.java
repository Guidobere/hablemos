package app.hablemos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
    //private FirebaseAuth auth;

    private String TAG;
    private  String password;
    private  String email;

    private EditText mailTxtBox;
    private EditText passTxtBox;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        TAG = getString(R.string.tagLogin);

        btnLogin = findViewById(R.id.Ingresar);
        mailTxtBox = findViewById(R.id.txtUser);
        passTxtBox = findViewById(R.id.txtPass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses btnLogin
                //loguearUsuario();
                //crearUsuario(user,pass);
                singIn();
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
            }
        };

        //REGISTRARSE
        txtRegistrar = findViewById(R.id.textRegistro);
        txtRegistrar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(RegistroActivity.class);
               // crearUsuario();
            }
        });
    }

/*    private void login(){
        EditText mailTxtBox = (EditText) findViewById(R.id.txtUser);
        EditText passTxtBox = (EditText) findViewById(R.id.txtPass);
        final String email = mailTxtBox.getText().toString();
        final String password = passTxtBox.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }
            }
        });
    }*/

    //CREAR O LOGEAR USUARIO
    private void crearUsuario() {
        email = mailTxtBox.getText().toString();
        password = passTxtBox.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmailAndPassword:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    startActivity(MainActivity.class);
                    //updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmailAndPassword:failure", task.getException());
                    Toast.makeText(Login.this, getString(R.string.fallo_autenticacion), Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }
            }
        });
    }

    private void startActivity(Class<?> cls) {
        final Intent intent = new Intent(this, cls);
        Bundle mBundle = new Bundle();
        mBundle.putString("1",email);
        intent.putExtras(mBundle);
        startActivity(intent);
    }

    private void singIn(){
        email = mailTxtBox.getText().toString();
        password = passTxtBox.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Coloque direccion de email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Coloque una contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    startActivity(MainActivity.class);
                    //updateUI(user);
                } else {
                    if (password.length() < 6) {
                        passTxtBox.setError(getString(R.string.minimum_password));
                    } else {
                        Toast.makeText(Login.this, getString(R.string.fallo_autenticacion), Toast.LENGTH_LONG).show();
                    }
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithEmail:failure", task.getException());
                    //Toast.makeText(Login.this, getString(R.string.fallo_autenticacion), Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public void onPause(){
        super.onPause();
        finish();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        finish();
    }

}