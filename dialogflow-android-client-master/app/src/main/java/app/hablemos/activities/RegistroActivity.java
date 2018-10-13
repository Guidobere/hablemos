package app.hablemos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
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
import app.hablemos.model.Recordatorio;
import app.hablemos.model.User;


public class RegistroActivity extends AppCompatActivity {

    //DATOS PERSONALES
    private EditText nombreAbuelo;
    private EditText mailTutor;
    private EditText equipoFavorito;
    private EditText contra;

    //MEDICAMENTOS
    private EditText medicamentosM;
    private EditText medicamentosT;
    private EditText medicamentosN;

    //private static HashMap<String, User> users = new HashMap<>();
    //ACCEDO A LOS USUARIOS DE FIREBASE y uso esta instancia como global
    DatabaseReference myUsersFb =FirebaseDatabase.getInstance().getReference().child("users");
    DatabaseReference myRecordatoriosGlucosaFb =FirebaseDatabase.getInstance().getReference().child("recordatorioglucosa");
    DatabaseReference myRecordatoriosPresionFb =FirebaseDatabase.getInstance().getReference().child("recordatoriosPresion");

    //ESto es para la autenticacion
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cinicial);

        nombreAbuelo = findViewById(R.id.txtAbuelo);
        mailTutor = findViewById(R.id.txtEmail);
        equipoFavorito = findViewById(R.id.txtEquipo);
        contra = findViewById(R.id.txtContra);
        medicamentosM = findViewById(R.id.txtmañana);
        medicamentosT = findViewById(R.id.txttarde);
        medicamentosN = findViewById(R.id.txtnoche);
        Button botonRegistro = findViewById(R.id.btnRegister);

        //LOGICA PARA QUE AL HACER CLICK EN EL BOTON ENVIE LOS DATOS A LA FUNCION writeNewUser
        botonRegistro.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //Aca lo guarda para que lo podamos autenticar
                crearUsuario();
            }
        });
    }

    //FUNCION PARA CREAR USUARIO EN FIREBASE
    private void writeNewUser(String username, String email,String equipo,String remediosM,String remediosT,String remediosN) {
        //Inserto un nuevo valor con su respectivo ID y le Asigno el valor del nuevo
        myUsersFb.push().setValue(new User(username,email,equipo,remediosM,remediosT,remediosN));
    }

    private void CrearNuevoRecordatoriosGlucosa() {
        String diasRecordatorioGlucManiana = RecordatoriosGlucosaManiana();
        String diasRecordatorioGlucTarde = RecordatoriosGlucosaTarde();
        String diasRecordatorioGlucNoche = RecordatoriosGlucosaNoche();

        String email = mailTutor.getText().toString().toLowerCase();
        if (diasRecordatorioGlucManiana != null && !diasRecordatorioGlucManiana.equals(""))
            myRecordatoriosGlucosaFb.push().setValue(new Recordatorio(email, diasRecordatorioGlucManiana, "mañana"));
        if (diasRecordatorioGlucTarde != null && !diasRecordatorioGlucTarde.equals(""))
            myRecordatoriosGlucosaFb.push().setValue(new Recordatorio(email, diasRecordatorioGlucTarde, "tarde"));
        if (diasRecordatorioGlucNoche != null && !diasRecordatorioGlucNoche.equals(""))
            myRecordatoriosGlucosaFb.push().setValue(new Recordatorio(email, diasRecordatorioGlucNoche, "noche"));
    }

    private String activoCheckbox(int checkBoxId, String diasRecordatorio, String dia) {
        String agregado = "";
        CheckBox checkBox = findViewById(checkBoxId);
        if(checkBox.isChecked()) {
            if (!diasRecordatorio.equals(""))
                agregado += ",";
            agregado += dia;
        }
        return agregado;
    }

    private String RecordatoriosGlucosaManiana(){
        String diasRecordatorioGlucManiana = "";
        diasRecordatorioGlucManiana += activoCheckbox(R.id.chBxGlucManianaL, diasRecordatorioGlucManiana, "lunes");
        diasRecordatorioGlucManiana += activoCheckbox(R.id.chBxGlucManianaMa, diasRecordatorioGlucManiana, "martes");
        diasRecordatorioGlucManiana += activoCheckbox(R.id.chBxGlucManianaMi, diasRecordatorioGlucManiana, "miercoles");
        diasRecordatorioGlucManiana += activoCheckbox(R.id.chBxGlucManianaJ, diasRecordatorioGlucManiana, "jueves");
        diasRecordatorioGlucManiana += activoCheckbox(R.id.chBxGlucManianaV, diasRecordatorioGlucManiana, "viernes");
        diasRecordatorioGlucManiana += activoCheckbox(R.id.chBxGlucManianaS, diasRecordatorioGlucManiana, "sabado");
        diasRecordatorioGlucManiana += activoCheckbox(R.id.chBxGlucManianaD, diasRecordatorioGlucManiana, "domingo");
        return diasRecordatorioGlucManiana;
    }

    private String RecordatoriosGlucosaTarde(){
        String diasRecordatorioGlucTarde = "";
        diasRecordatorioGlucTarde += activoCheckbox(R.id.chBxGlucTardeLu, diasRecordatorioGlucTarde, "lunes");
        diasRecordatorioGlucTarde += activoCheckbox(R.id.chBxGlucTardeMa, diasRecordatorioGlucTarde, "martes");
        diasRecordatorioGlucTarde += activoCheckbox(R.id.chBxGlucTardeMi, diasRecordatorioGlucTarde, "miercoles");
        diasRecordatorioGlucTarde += activoCheckbox(R.id.chBxGlucTardeJ, diasRecordatorioGlucTarde, "jueves");
        diasRecordatorioGlucTarde += activoCheckbox(R.id.chBxGlucTardeV, diasRecordatorioGlucTarde, "viernes");
        diasRecordatorioGlucTarde += activoCheckbox(R.id.chBxGlucTardeS, diasRecordatorioGlucTarde, "sabado");
        diasRecordatorioGlucTarde += activoCheckbox(R.id.chBxGlucTardeD, diasRecordatorioGlucTarde, "domingo");
        return diasRecordatorioGlucTarde;
    }

    private String RecordatoriosGlucosaNoche(){
        String diasRecordatorioGlucNoche = "";
        diasRecordatorioGlucNoche += activoCheckbox(R.id.chBxGlucNocheLu, diasRecordatorioGlucNoche, "lunes");
        diasRecordatorioGlucNoche += activoCheckbox(R.id.chBxGlucNocheMa, diasRecordatorioGlucNoche, "martes");
        diasRecordatorioGlucNoche += activoCheckbox(R.id.chBxGlucNocheMi, diasRecordatorioGlucNoche, "miercoles");
        diasRecordatorioGlucNoche += activoCheckbox(R.id.chBxGlucNocheJ, diasRecordatorioGlucNoche, "jueves");
        diasRecordatorioGlucNoche += activoCheckbox(R.id.chBxGlucNocheV, diasRecordatorioGlucNoche, "viernes");
        diasRecordatorioGlucNoche += activoCheckbox(R.id.chBxGlucNocheS, diasRecordatorioGlucNoche, "sabado");
        diasRecordatorioGlucNoche += activoCheckbox(R.id.chBxGlucNocheD, diasRecordatorioGlucNoche, "domingo");
        return diasRecordatorioGlucNoche;
    }

    private void CrearNuevoRecordatoriosPresion() {
        String diasRecordatorioPresionManiana = RecordatoriosPresionManiana();
        String diasRecordatorioPresionTarde = RecordatoriosPresionTarde();
        String diasRecordatorioPresionNoche = RecordatoriosPresionNoche();

        String email = mailTutor.getText().toString().toLowerCase();
        if (diasRecordatorioPresionManiana != null && !diasRecordatorioPresionManiana.equals(""))
            myRecordatoriosPresionFb.push().setValue(new Recordatorio(email, diasRecordatorioPresionManiana, "mañana"));
        if (diasRecordatorioPresionTarde != null && !diasRecordatorioPresionTarde.equals(""))
            myRecordatoriosPresionFb.push().setValue(new Recordatorio(email, diasRecordatorioPresionTarde, "tarde"));
        if (diasRecordatorioPresionNoche != null && !diasRecordatorioPresionNoche.equals(""))
            myRecordatoriosPresionFb.push().setValue(new Recordatorio(email, diasRecordatorioPresionNoche, "noche"));
    }

    private String RecordatoriosPresionManiana(){
        String diasRecordatorioPresionManiana = "";
        diasRecordatorioPresionManiana += activoCheckbox(R.id.chBxPresionManianaL, diasRecordatorioPresionManiana, "lunes");
        diasRecordatorioPresionManiana += activoCheckbox(R.id.chBxPresionManianaMa, diasRecordatorioPresionManiana, "martes");
        diasRecordatorioPresionManiana += activoCheckbox(R.id.chBxPresionManianaMi, diasRecordatorioPresionManiana, "miercoles");
        diasRecordatorioPresionManiana += activoCheckbox(R.id.chBxPresionManianaJ, diasRecordatorioPresionManiana, "jueves");
        diasRecordatorioPresionManiana += activoCheckbox(R.id.chBxPresionManianaV, diasRecordatorioPresionManiana, "viernes");
        diasRecordatorioPresionManiana += activoCheckbox(R.id.chBxPresionManianaS, diasRecordatorioPresionManiana, "sabado");
        diasRecordatorioPresionManiana += activoCheckbox(R.id.chBxPresionManianaD, diasRecordatorioPresionManiana, "domingo");
        return diasRecordatorioPresionManiana;
    }

    private String RecordatoriosPresionTarde(){
        String diasRecordatorioPresionTarde = "";
        diasRecordatorioPresionTarde += activoCheckbox(R.id.chBxPresionTardeLu, diasRecordatorioPresionTarde, "lunes");
        diasRecordatorioPresionTarde += activoCheckbox(R.id.chBxPresionTardeMa, diasRecordatorioPresionTarde, "martes");
        diasRecordatorioPresionTarde += activoCheckbox(R.id.chBxPresionTardeMi, diasRecordatorioPresionTarde, "miercoles");
        diasRecordatorioPresionTarde += activoCheckbox(R.id.chBxPresionTardeJ, diasRecordatorioPresionTarde, "jueves");
        diasRecordatorioPresionTarde += activoCheckbox(R.id.chBxPresionTardeV, diasRecordatorioPresionTarde, "viernes");
        diasRecordatorioPresionTarde += activoCheckbox(R.id.chBxPresionTardeS, diasRecordatorioPresionTarde, "sabado");
        diasRecordatorioPresionTarde += activoCheckbox(R.id.chBxPresionTardeD, diasRecordatorioPresionTarde, "domingo");
        return diasRecordatorioPresionTarde;
    }

    private String RecordatoriosPresionNoche(){
        String diasRecordatorioPresionNoche = "";
        diasRecordatorioPresionNoche += activoCheckbox(R.id.chBxPresionNocheL, diasRecordatorioPresionNoche, "lunes");
        diasRecordatorioPresionNoche += activoCheckbox(R.id.chBxPresionNocheMa, diasRecordatorioPresionNoche, "martes");
        diasRecordatorioPresionNoche += activoCheckbox(R.id.chBxPresionNocheMi, diasRecordatorioPresionNoche, "miercoles");
        diasRecordatorioPresionNoche += activoCheckbox(R.id.chBxPresionNocheJ, diasRecordatorioPresionNoche, "jueves");
        diasRecordatorioPresionNoche += activoCheckbox(R.id.chBxPresionNocheV, diasRecordatorioPresionNoche, "viernes");
        diasRecordatorioPresionNoche += activoCheckbox(R.id.chBxPresionNocheS, diasRecordatorioPresionNoche, "sabado");
        diasRecordatorioPresionNoche += activoCheckbox(R.id.chBxPresionNocheD, diasRecordatorioPresionNoche, "domingo");
        return diasRecordatorioPresionNoche;
    }

    private void crearUsuario() {
        String email = mailTutor.getText().toString().toLowerCase();
        String password = contra.getText().toString();

        if (TextUtils.isEmpty(nombreAbuelo.getText().toString())) {
            Toast.makeText(getApplicationContext(), getString(R.string.nombreAbueloVacio), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), getString(R.string.emailVacio), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.matches(getString(R.string.regexEmail)))  {
            Toast.makeText(getApplicationContext(), getString(R.string.emailMalFormado), Toast.LENGTH_SHORT).show();
            return;
        }

       if (TextUtils.isEmpty(password)) {
           Toast.makeText(getApplicationContext(), getString(R.string.contraseniaVacia), Toast.LENGTH_SHORT).show();
           return;
       }

       if(!password.matches(getString(R.string.regexPassword))) {
            Toast.makeText(getApplicationContext(), getString(R.string.contraseniaMalFormada), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(equipoFavorito.getText().toString())) {
            Toast.makeText(getApplicationContext(), getString(R.string.equipoVacio), Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();

                        //Si no se repite o algo, lo guarda en la base
                        writeNewUser( nombreAbuelo.getText().toString().toLowerCase(),mailTutor.getText().toString().toLowerCase(),equipoFavorito.getText().toString().toLowerCase(),medicamentosM.getText().toString().toLowerCase(), medicamentosT.getText().toString().toLowerCase(),medicamentosN.getText().toString().toLowerCase());
                        CrearNuevoRecordatoriosGlucosa();
                        CrearNuevoRecordatoriosPresion();
                        startActivity(Login.class);
                    } else {
                        Toast.makeText(RegistroActivity.this, getString(R.string.fallo_autenticacion), Toast.LENGTH_SHORT).show();
                    }
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