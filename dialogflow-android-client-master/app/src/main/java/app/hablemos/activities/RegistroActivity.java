package app.hablemos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

    //GLUCOSA MAÑANA
    private CheckBox chkbxGlucManianaLun;
    private CheckBox chkbxGlucManianaMa;
    private CheckBox chkbxGlucManianaMi;
    private CheckBox chkbxGlucManianaJ;
    private CheckBox chkbxGlucManianaV;
    private CheckBox chkbxGlucManianaS;
    private CheckBox chkbxGlucManianaD;

    //GLUCOSA TARDE
    private CheckBox chkbxGlucTardeLun;
    private CheckBox chkbxGlucTardeMa;
    private CheckBox chkbxGlucTardeMi;
    private CheckBox chkbxGlucTardeJ;
    private CheckBox chkbxGlucTardeV;
    private CheckBox chkbxGlucTardeS;
    private CheckBox chkbxGlucTardeD;

    //GLUCOSA NOCHE
    private CheckBox chkbxGlucNocheLun;
    private CheckBox chkbxGlucNocheMa;
    private CheckBox chkbxGlucNocheMi;
    private CheckBox chkbxGlucNocheJ;
    private CheckBox chkbxGlucNocheV;
    private CheckBox chkbxGlucNocheS;
    private CheckBox chkbxGlucNocheD;

    //PRESION MAÑANA
    private CheckBox chkbxPresionManianaL;
    private CheckBox chkbxPresionManianaMa;
    private CheckBox chkbxPresionManianaMi;
    private CheckBox chkbxPresionManianaJ;
    private CheckBox chkbxPresionManianaV;
    private CheckBox chkbxPresionManianaS;
    private CheckBox chkbxPresionManianaD;

    //PRESION TARDE
    private CheckBox chkbxPresionTardeL;
    private CheckBox chkbxPresionTardeMa;
    private CheckBox chkbxPresionTardeMi;
    private CheckBox chkbxPresionTardeJ;
    private CheckBox chkbxPresionTardeV;
    private CheckBox chkbxPresionTardeS;
    private CheckBox chkbxPresionTardeD;

    //PRESION NOCHE
    private CheckBox chkbxPresionNocheL;
    private CheckBox chkbxPresionNocheMa;
    private CheckBox chkbxPresionNocheMi;
    private CheckBox chkbxPresionNocheJ;
    private CheckBox chkbxPresionNocheV;
    private CheckBox chkbxPresionNocheS;
    private CheckBox chkbxPresionNocheD;

    private Button botonRegistro;

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

        nombreAbuelo = (EditText) findViewById(R.id.txtAbuelo);
        mailTutor = (EditText) findViewById(R.id.txtEmail);
        equipoFavorito = (EditText) findViewById(R.id.txtEquipo);
        contra = (EditText) findViewById(R.id.txtContra);
        medicamentosM = (EditText) findViewById(R.id.txtmañana);
        medicamentosT = (EditText) findViewById(R.id.txttarde);
        medicamentosN = (EditText) findViewById(R.id.txtnoche);
        botonRegistro = (Button) findViewById(R.id.btnRegister);

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
                //writeNewUser( nombreAbuelo.getText().toString(),mailTutor.getText().toString(),equipoFavorito.getText().toString(),medicamentosM.getText().toString(),medicamentosT.getText().toString(),medicamentosN.getText().toString());
                CrearNuevoRecordatoriosGlucosa();
                CrearNuevoRecordatoriosPresion();

            }
        });
    }

    //FUNCION PARA CREAR USUARIO EN FIREBASE
    private void writeNewUser(String username, String email,String equipo,String remediosM,String remediosT,String remediosN) {
        //Inserto un nuevo valor con su respectivo ID y le Asigno el valor del nuevo
        myUsersFb.push().setValue(new User(username,email,equipo,remediosM,remediosT,remediosN));

    }

    private void CrearNuevoRecordatoriosGlucosa() {
        Recordatorio recordatorio = new Recordatorio();

        String diasRecordatorioGlucMañana = RecordatoriosGlucosaMañana();
        String diasRecordatorioGlucTarde = RecordatoriosGlucosaTarde();
        String diasRecordatorioGlucNoche = RecordatoriosGlucosaNoche();

        String email = mailTutor.getText().toString();
        if (diasRecordatorioGlucMañana != "" && diasRecordatorioGlucMañana != null)
            myRecordatoriosGlucosaFb.push().setValue(new Recordatorio(email, diasRecordatorioGlucMañana, "mañana"));
        if (diasRecordatorioGlucTarde != "" && diasRecordatorioGlucMañana != null)
            myRecordatoriosGlucosaFb.push().setValue(new Recordatorio(email, diasRecordatorioGlucTarde, "tarde"));
        if (diasRecordatorioGlucNoche != "" && diasRecordatorioGlucMañana != null)
            myRecordatoriosGlucosaFb.push().setValue(new Recordatorio(email, diasRecordatorioGlucNoche, "noche"));


    }

    private String RecordatoriosGlucosaMañana(){

        String diasRecordatorioGlucManiana = "";

        chkbxGlucManianaLun =  (CheckBox) findViewById(R.id.chBxGlucManianaL);
        String diasRecordatorioGluc = "";
        if(chkbxGlucManianaLun.isChecked())
            diasRecordatorioGlucManiana += "lunes,";

        chkbxGlucManianaMa =  (CheckBox) findViewById(R.id.chBxGlucManianaMa);
        if(chkbxGlucManianaMa.isChecked())
            diasRecordatorioGlucManiana += "martes,";

        chkbxGlucManianaMi =  (CheckBox) findViewById(R.id.chBxGlucManianaMi);
        if(chkbxGlucManianaMi.isChecked())
            diasRecordatorioGlucManiana += "miercoles,";

        chkbxGlucManianaJ =  (CheckBox) findViewById(R.id.chBxGlucManianaJ);
        if(chkbxGlucManianaJ.isChecked())
            diasRecordatorioGlucManiana += "jueves,";

        chkbxGlucManianaV =  (CheckBox) findViewById(R.id.chBxGlucManianaV);
        if(chkbxGlucManianaV.isChecked())
            diasRecordatorioGlucManiana += "viernes,";

        chkbxGlucManianaS =  (CheckBox) findViewById(R.id.chBxGlucManianaS);
        if(chkbxGlucManianaS.isChecked())
            diasRecordatorioGlucManiana += "sabado,";

        chkbxGlucManianaD =  (CheckBox) findViewById(R.id.chBxGlucManianaD);
        if(chkbxGlucManianaD.isChecked())
            diasRecordatorioGlucManiana += "domingo";

        return diasRecordatorioGlucManiana;

    }

    private String RecordatoriosGlucosaTarde(){

        String diasRecordatorioGlucTarde = "";

        chkbxGlucTardeLun =  (CheckBox) findViewById(R.id.chBxGlucTardeLu);
        String diasRecordatorioGluc = "";
        if(chkbxGlucTardeLun.isChecked())
            diasRecordatorioGlucTarde += "lunes,";

        chkbxGlucTardeMa =  (CheckBox) findViewById(R.id.chBxGlucTardeMa);
        if(chkbxGlucTardeMa.isChecked())
            diasRecordatorioGlucTarde += "martes,";

        chkbxGlucTardeMi =  (CheckBox) findViewById(R.id.chBxGlucTardeMi);
        if(chkbxGlucTardeMi.isChecked())
            diasRecordatorioGlucTarde += "miercoles,";

        chkbxGlucTardeJ =  (CheckBox) findViewById(R.id.chBxGlucTardeJ);
        if(chkbxGlucTardeJ.isChecked())
            diasRecordatorioGlucTarde += "jueves,";

        chkbxGlucTardeV =  (CheckBox) findViewById(R.id.chBxGlucTardeV);
        if(chkbxGlucTardeV.isChecked())
            diasRecordatorioGlucTarde += "viernes,";

        chkbxGlucTardeS =  (CheckBox) findViewById(R.id.chBxGlucTardeS);
        if(chkbxGlucTardeS.isChecked())
            diasRecordatorioGlucTarde += "sabado,";

        chkbxGlucTardeD =  (CheckBox) findViewById(R.id.chBxGlucTardeD);
        if(chkbxGlucTardeD.isChecked())
            diasRecordatorioGlucTarde += "domingo";

        return diasRecordatorioGlucTarde;

    }

    private String RecordatoriosGlucosaNoche(){

        String diasRecordatorioGlucNoche = "";

        chkbxGlucNocheLun =  (CheckBox) findViewById(R.id.chBxGlucNocheLu);
        String diasRecordatorioGluc = "";
        if(chkbxGlucNocheLun.isChecked())
            diasRecordatorioGlucNoche += "lunes,";

        chkbxGlucNocheMa =  (CheckBox) findViewById(R.id.chBxGlucNocheMa);
        if(chkbxGlucNocheMa.isChecked())
            diasRecordatorioGlucNoche += "martes,";

        chkbxGlucNocheMi =  (CheckBox) findViewById(R.id.chBxGlucNocheMi);
        if(chkbxGlucNocheMi.isChecked())
            diasRecordatorioGlucNoche += "miercoles,";

        chkbxGlucNocheJ =  (CheckBox) findViewById(R.id.chBxGlucNocheJ);
        if(chkbxGlucNocheJ.isChecked())
            diasRecordatorioGlucNoche += "jueves,";

        chkbxGlucNocheV =  (CheckBox) findViewById(R.id.chBxGlucNocheV);
        if(chkbxGlucNocheV.isChecked())
            diasRecordatorioGlucNoche += "viernes,";

        chkbxGlucNocheS =  (CheckBox) findViewById(R.id.chBxGlucNocheS);
        if(chkbxGlucNocheS.isChecked())
            diasRecordatorioGlucNoche += "sabado,";

        chkbxGlucNocheD =  (CheckBox) findViewById(R.id.chBxGlucNocheD);
        if(chkbxGlucNocheD.isChecked())
            diasRecordatorioGlucNoche += "domingo";

        return diasRecordatorioGlucNoche;

    }

    private void CrearNuevoRecordatoriosPresion() {
        Recordatorio recordatorio = new Recordatorio();
        String diasRecordatorioPresionMañana = RecordatoriosPresionMañana();
        String diasRecordatorioPresionTarde = RecordatoriosPresionTarde();
        String diasRecordatorioPresionNoche = RecordatoriosPresionNoche();

        String email = mailTutor.getText().toString();
        if (diasRecordatorioPresionMañana != "" && diasRecordatorioPresionMañana != null)
            myRecordatoriosPresionFb.push().setValue(new Recordatorio(email, diasRecordatorioPresionMañana, "mañana"));
        if (diasRecordatorioPresionMañana != "" && diasRecordatorioPresionMañana != null)
            myRecordatoriosPresionFb.push().setValue(new Recordatorio(email, diasRecordatorioPresionTarde, "tarde"));
        if (diasRecordatorioPresionMañana != "" && diasRecordatorioPresionMañana != null)
            myRecordatoriosPresionFb.push().setValue(new Recordatorio(email, diasRecordatorioPresionNoche, "noche"));



    }

    private String RecordatoriosPresionMañana(){

        String diasRecordatorioPresionManiana = "";

        chkbxPresionManianaL =  (CheckBox) findViewById(R.id.chBxPresionManianaL);
        String diasRecordatorioPresion = "";
        if(chkbxPresionManianaL.isChecked())
            diasRecordatorioPresionManiana += "lunes,";

        chkbxPresionManianaMa =  (CheckBox) findViewById(R.id.chBxPresionManianaMa);
        if(chkbxPresionManianaMa.isChecked())
            diasRecordatorioPresionManiana += "martes,";

        chkbxPresionManianaMi =  (CheckBox) findViewById(R.id.chBxPresionManianaMi);
        if(chkbxPresionManianaMi.isChecked())
            diasRecordatorioPresionManiana += "miercoles,";

        chkbxPresionManianaJ =  (CheckBox) findViewById(R.id.chBxPresionManianaJ);
        if(chkbxPresionManianaJ.isChecked())
            diasRecordatorioPresionManiana += "jueves,";

        chkbxPresionManianaV =  (CheckBox) findViewById(R.id.chBxPresionManianaV);
        if(chkbxPresionManianaV.isChecked())
            diasRecordatorioPresionManiana += "viernes,";

        chkbxPresionManianaS =  (CheckBox) findViewById(R.id.chBxPresionManianaS);
        if(chkbxPresionManianaS.isChecked())
            diasRecordatorioPresionManiana += "sabado,";

        chkbxPresionManianaD =  (CheckBox) findViewById(R.id.chBxPresionManianaD);
        if(chkbxPresionManianaD.isChecked())
            diasRecordatorioPresionManiana += "domingo";

        return diasRecordatorioPresionManiana;

    }

    private String RecordatoriosPresionTarde(){

        String diasRecordatorioPresionTarde = "";

        chkbxPresionTardeL =  (CheckBox) findViewById(R.id.chBxPresionTardeLu);
        String diasRecordatorioPresion = "";
        if(chkbxPresionTardeL.isChecked())
            diasRecordatorioPresionTarde += "lunes,";

        chkbxPresionTardeMa =  (CheckBox) findViewById(R.id.chBxPresionTardeMa);
        if(chkbxPresionTardeMa.isChecked())
            diasRecordatorioPresionTarde += "martes,";

        chkbxPresionTardeMi =  (CheckBox) findViewById(R.id.chBxPresionTardeMi);
        if(chkbxPresionTardeMi.isChecked())
            diasRecordatorioPresionTarde += "miercoles,";

        chkbxPresionTardeJ =  (CheckBox) findViewById(R.id.chBxPresionTardeJ);
        if(chkbxPresionTardeJ.isChecked())
            diasRecordatorioPresionTarde += "jueves,";

        chkbxPresionTardeV =  (CheckBox) findViewById(R.id.chBxPresionTardeV);
        if(chkbxPresionTardeV.isChecked())
            diasRecordatorioPresionTarde += "viernes,";

        chkbxPresionTardeS =  (CheckBox) findViewById(R.id.chBxPresionTardeS);
        if(chkbxPresionTardeS.isChecked())
            diasRecordatorioPresionTarde += "sabado,";

        chkbxPresionTardeD =  (CheckBox) findViewById(R.id.chBxPresionTardeD);
        if(chkbxPresionTardeD.isChecked())
            diasRecordatorioPresionTarde += "domingo";

        return diasRecordatorioPresionTarde;

    }

    private String RecordatoriosPresionNoche(){

        String diasRecordatorioPresionNoche = "";

        chkbxPresionNocheL =  (CheckBox) findViewById(R.id.chBxPresionNocheL);
        String diasRecordatorioPresion = "";
        if(chkbxPresionNocheL.isChecked())
            diasRecordatorioPresionNoche += "lunes,";

        chkbxPresionNocheMa =  (CheckBox) findViewById(R.id.chBxPresionNocheMa);
        if(chkbxPresionNocheMa.isChecked())
            diasRecordatorioPresionNoche += "martes,";

        chkbxPresionNocheMi =  (CheckBox) findViewById(R.id.chBxPresionNocheMi);
        if(chkbxPresionNocheMi.isChecked())
            diasRecordatorioPresionNoche += "miercoles,";

        chkbxPresionNocheJ =  (CheckBox) findViewById(R.id.chBxPresionNocheJ);
        if(chkbxPresionNocheJ.isChecked())
            diasRecordatorioPresionNoche += "jueves,";

        chkbxPresionNocheV =  (CheckBox) findViewById(R.id.chBxPresionNocheV);
        if(chkbxPresionNocheV.isChecked())
            diasRecordatorioPresionNoche += "viernes,";

        chkbxPresionNocheS =  (CheckBox) findViewById(R.id.chBxPresionNocheS);
        if(chkbxPresionNocheS.isChecked())
            diasRecordatorioPresionNoche += "sabado,";

        chkbxPresionNocheD =  (CheckBox) findViewById(R.id.chBxPresionNocheD);
        if(chkbxPresionNocheD.isChecked())
            diasRecordatorioPresionNoche += "domingo";

        return diasRecordatorioPresionNoche;

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
                            //  Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Si no se repite o algo, lo guarda en la base
                            writeNewUser( nombreAbuelo.getText().toString(),mailTutor.getText().toString(),equipoFavorito.getText().toString(),medicamentosM.getText().toString(),medicamentosT.getText().toString(),medicamentosN.getText().toString());
                            startActivity(Login.class);
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //    Log.w(TAG, "createUserWithEmail:failure", task.getException());
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