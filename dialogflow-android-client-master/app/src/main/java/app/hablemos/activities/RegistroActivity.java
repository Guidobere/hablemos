package app.hablemos.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.hablemos.R;
import app.hablemos.model.Recordatorio;
import app.hablemos.model.User;
import app.hablemos.services.FootballService;


public class RegistroActivity extends AppCompatActivity {


    //ID's
    private String UserID;
    private String GlucosaManianaID;
    private String GlucosaTardeID;
    private String GlucosaNocheID;
    private String PresionManianaID;
    private String PresionTardeID;
    private String PresionNocheID;


    private  boolean ActualizarRecordatorioPresion = true;
    private  boolean ActualizarRecordatorioGlucosa = false;


    //DATOS PERSONALES
    private EditText nombreAbuelo;
    private EditText mailTutor;
    private String equipoFavorito;
    private EditText contra;
    private EditText repetirPwd;
    private Spinner spinnerEquipo;
    //MEDICAMENTOS
    private EditText medicamentosM;
    private EditText medicamentosT;
    private EditText medicamentosN;

    private FootballService footballService;
    private String mailQueInicioSesion;
    private int a=0;

    //private static HashMap<String, User> users = new HashMap<>();
    //ACCEDO A LOS USUARIOS DE FIREBASE y uso esta instancia como global
    private String TAG = "RegistroLog";
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference myUsersFb;
    DatabaseReference myRecordatoriosGlucosaFb;
    DatabaseReference myRecordatoriosPresionFb;

    //ESto es para la autenticacion
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myUsersFb = FirebaseDatabase.getInstance().getReference().child(getString(R.string.fbUsuarios));
        myRecordatoriosGlucosaFb = FirebaseDatabase.getInstance().getReference().child(getString(R.string.fbRecordatoriosGlucosa));
        myRecordatoriosPresionFb = FirebaseDatabase.getInstance().getReference().child(getString(R.string.fbRecordatoriosPresion));

        setContentView(R.layout.cinicial);

        nombreAbuelo = findViewById(R.id.txtAbuelo);
        mailTutor = findViewById(R.id.txtEmail);
        //equipoFavorito = findViewById(R.id.txtEquipo);
        contra = findViewById(R.id.txtContra);
        repetirPwd = findViewById(R.id.txtContra2);
        medicamentosM = findViewById(R.id.txtmañana);
        medicamentosT = findViewById(R.id.txttarde);
        medicamentosN = findViewById(R.id.txtnoche);
        Button botonRegistro = findViewById(R.id.btnRegister);
        Button botonGuardar = findViewById(R.id.btnGuardar);
        Button botonCancel = findViewById(R.id.btnCancel);

        footballService = new FootballService();

        spinnerEquipo = (Spinner) findViewById(R.id.spinnerEquipo);

        cargarEquiposEnSpinner();

        if(mAuth.getCurrentUser() != null) {
            botonRegistro.setVisibility(View.GONE);
            botonGuardar.setVisibility(View.VISIBLE);
            a=1;
        }
        else{
            botonRegistro.setVisibility(View.VISIBLE);
            botonGuardar.setVisibility(View.GONE);
            a=0;
        }

        //LOGICA PARA QUE AL HACER CLICK EN EL BOTON ENVIE LOS DATOS A LA FUNCION writeNewUser
        botonRegistro.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //Aca lo guarda para que lo podamos autenticar
                crearNuevoUsuario();
            }
        });

        botonCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });


        //LOGICA PARA QUE AL HACER CLICK EN EL BOTON ENVIE LOS DATOS A LA FUNCION guardarUsuario
        botonGuardar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //Aca actualiza al usuario existente
                ActualizarUsuario();
                if(a==1) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("equipoAbuelo", equipoFavorito);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }else {
                    startActivity(Login.class);
                }


            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    String email = user.getEmail().toString();
                    pedirAlaBaseUsuarioByEmail(email);
                    pedirAlaBaseSobrePresion(email);
                    pedirAlaBaseSobreGlucosa(email);

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + email);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void cargarEquiposEnSpinner() {
        List<String> equiposPrimera = footballService.getEquiposDePrimera();
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add(0,"Ninguno");
        Collections.sort(equiposPrimera, new Comparator<String>() {
            @Override
            public int compare(String equipo1, String equipo2) {
                return equipo1.compareToIgnoreCase(equipo2);
            }
        });
        spinnerArray.addAll(equiposPrimera);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEquipo.setAdapter(adapter);
    }

    //FUNCION PARA CREAR USUARIO EN FIREBASE
     private String writeNewUser(String username, String email,String equipo,String remediosM,String remediosT,String remediosN) {
        //Obtengo el ID del usuario que se me va a guardar en la BD
        String userID=myUsersFb.push().getKey();
        //Guardo la informacion en el usuario.child(ID) al respectivo usuario.
        if(userID != null && userID != "")
            myUsersFb.child(userID).setValue(new User(userID,username,email,equipo,remediosM,remediosT,remediosN));
        else {
            Toast.makeText(RegistroActivity.this, "fallo al crear al usuario", Toast.LENGTH_SHORT).show();
        }
        return userID;
    }

    private void CrearNuevoRecordatoriosGlucosa() {
        String diasRecordatorioGlucManiana = RecordatoriosGlucosaManiana();
        String diasRecordatorioGlucTarde = RecordatoriosGlucosaTarde();
        String diasRecordatorioGlucNoche = RecordatoriosGlucosaNoche();

        String email = mailTutor.getText().toString().toLowerCase();
        String recordatorioManianaID = myRecordatoriosGlucosaFb.push().getKey();
        myRecordatoriosGlucosaFb.child(recordatorioManianaID).setValue(new Recordatorio(recordatorioManianaID,email, diasRecordatorioGlucManiana, "mañana"));

        String recordatorioTardeID = myRecordatoriosGlucosaFb.push().getKey();
        myRecordatoriosGlucosaFb.child(recordatorioTardeID).setValue(new Recordatorio(recordatorioTardeID,email, diasRecordatorioGlucTarde, "tarde"));

        String recordatorioNocheID = myRecordatoriosGlucosaFb.push().getKey();
        myRecordatoriosGlucosaFb.child(recordatorioNocheID).setValue(new Recordatorio(recordatorioNocheID,email, diasRecordatorioGlucNoche, "noche"));
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
        String recordatorioManianaID = myRecordatoriosPresionFb.push().getKey();
        myRecordatoriosPresionFb.child(recordatorioManianaID).setValue(new Recordatorio(recordatorioManianaID,email, diasRecordatorioPresionManiana, "mañana"));
        String recordatorioTardeID = myRecordatoriosPresionFb.push().getKey();
        myRecordatoriosPresionFb.child(recordatorioTardeID).setValue(new Recordatorio(recordatorioTardeID,email, diasRecordatorioPresionTarde, "tarde"));
        String recordatorioNocheID = myRecordatoriosPresionFb.push().getKey();
        myRecordatoriosPresionFb.child(recordatorioNocheID).setValue(new Recordatorio(recordatorioNocheID,email, diasRecordatorioPresionNoche, "noche"));
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

    private void crearNuevoUsuario() {
        String email = mailTutor.getText().toString().toLowerCase();
        String password = contra.getText().toString();

        if(!CamposCompletadosCorrectamente())
            return;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Si no se repite o algo, lo guarda en la base
                            String userID=writeNewUser( nombreAbuelo.getText().toString().toLowerCase(),mailTutor.getText().toString().toLowerCase(),footballService.getNombreReferencia(spinnerEquipo.getSelectedItem().toString()),medicamentosM.getText().toString().toLowerCase(), medicamentosT.getText().toString().toLowerCase(),medicamentosN.getText().toString().toLowerCase());
                            if(userID != "" && userID != null) {
                                CrearNuevoRecordatoriosGlucosa();
                                CrearNuevoRecordatoriosPresion();
                            }
                            startActivity(Login.class);
                        } else {
                            Toast.makeText(RegistroActivity.this, getString(R.string.fallo_autenticacion), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void ActualizarUsuario() {

        if(!CamposCompletadosCorrectamente())
            return;

        UpdateUser();
        if(ActualizarRecordatorioPresion)
            UpdatePresion();
        else
            Toast.makeText(RegistroActivity.this, "No se pudo actualizar al al recordatorio de glucosa", Toast.LENGTH_SHORT).show();

        if(ActualizarRecordatorioGlucosa)
            UpdateGlucosa();
        else
            Toast.makeText(RegistroActivity.this, "No se pudo actualizar al al recordatorio de presion", Toast.LENGTH_SHORT).show();

    }

    private boolean CamposCompletadosCorrectamente() {
        boolean camposEstanOk = true;
        String email = mailTutor.getText().toString().toLowerCase();
        String password = contra.getText().toString();

        if (TextUtils.isEmpty(nombreAbuelo.getText().toString())) {
            Toast.makeText(getApplicationContext(), getString(R.string.nombreAbueloVacio), Toast.LENGTH_SHORT).show();
            camposEstanOk = false ;
            return camposEstanOk;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), getString(R.string.emailVacio), Toast.LENGTH_SHORT).show();
            camposEstanOk = false;
            return camposEstanOk;
        }

        if (!email.matches(getString(R.string.regexEmail)))  {
            Toast.makeText(getApplicationContext(), getString(R.string.emailMalFormado), Toast.LENGTH_SHORT).show();
            camposEstanOk = false;
            return camposEstanOk;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), getString(R.string.contraseniaVacia), Toast.LENGTH_SHORT).show();
            camposEstanOk = false;
        }
        if (!password.equals(repetirPwd.getText().toString())) {
            Toast.makeText(getApplicationContext(), getString(R.string.contraseñasNoCoinciden), Toast.LENGTH_SHORT).show();
            camposEstanOk = false;
            return camposEstanOk;
        }

        if(!password.matches(getString(R.string.regexPassword))) {
            Toast.makeText(getApplicationContext(), getString(R.string.contraseniaMalFormada), Toast.LENGTH_SHORT).show();
            camposEstanOk = false;
        }

/*        if (TextUtils.isEmpty(spinnerEquipo.getSelectedItem().toString())) {
            Toast.makeText(getApplicationContext(), getString(R.string.equipoVacio), Toast.LENGTH_SHORT).show();
            camposEstanOk = false;
            return camposEstanOk;
        }*/
        return  camposEstanOk;
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

    public void pedirAlaBaseUsuarioByEmail(final String email){
        //String mailQueInicioSesion = getIntent().getExtras().getString("1");
        myUsersFb.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot users) {

                User u = new User();

                Iterable<DataSnapshot> usersChildren = users.getChildren();
                for (DataSnapshot user : usersChildren) {
                    u = user.getValue(User.class);
                }

                if (u != null && u.userID != null) {

                    //LLENAR CAMPOS
                    UserID=u.userID;
                    nombreAbuelo.setText(u.username);
                    //nombreAbuelo.setEnabled(false);
                    //nombreAbuelo.setFocusable(false);
                    mailTutor.setEnabled(false);
                    mailTutor.setText(u.email);
                    mailTutor.setFocusable(false);
                    contra.setText("123456");
                    contra.setFocusable(false);
                    contra.setEnabled(false);
                    repetirPwd.setText("123456");
                    repetirPwd.setFocusable(false);
                    repetirPwd.setEnabled(false);
                    //equipoFavorito.setText(u.equipo);
                    medicamentosM.setText(u.remediosManiana);
                    medicamentosT.setText(u.remediosTarde);
                    medicamentosN.setText(u.remediosNoche);
                    String nombreEquipo = "";
                    if (u.equipo.equalsIgnoreCase("ninguno")){
                        nombreEquipo = "Ninguno";
                    } else {
                        nombreEquipo = footballService.obtenerEquipoVisual(u.equipo).getNombre();
                    }
                    spinnerEquipo.setSelection(getIndex(spinnerEquipo, nombreEquipo));



/*                    CrearNuevoRecordatoriosGlucosa();
                    CrearNuevoRecordatoriosPresion();*/

                } else {
                    System.out.println("Ocurrio un error al obtener el usuario");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private int getIndex(Spinner spinnerEquipo, String equipo){
        for (int i=0;i<spinnerEquipo.getCount();i++){
            if (spinnerEquipo.getItemAtPosition(i).toString().equalsIgnoreCase(equipo)){
                return i;
            }
        }

        return 0;
    }



    public void pedirAlaBaseSobreGlucosa(final String email){

        myRecordatoriosGlucosaFb.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot users) {

                Recordatorio r;
                Iterable<DataSnapshot> usersChildren = users.getChildren();

                for (DataSnapshot recordatorio : usersChildren) {
                    r = recordatorio.getValue(Recordatorio.class);
                    String elturno = "", losdias = "";
                    if (r != null) {
                        elturno = r.turno.toLowerCase();
                        losdias = r.dias.toLowerCase();

                        if(r.recordatorioID != null && r.recordatorioID != "") {
                            ActualizarRecordatorioGlucosa = true;
                            if (elturno.toLowerCase().equalsIgnoreCase("mañana")) {

                                GlucosaManianaID = r.recordatorioID;
                                checkAndSetCheckBoxById(losdias, "lunes", R.id.chBxGlucManianaL);
                                checkAndSetCheckBoxById(losdias, "martes", R.id.chBxGlucManianaMa);
                                checkAndSetCheckBoxById(losdias, "miercoles", R.id.chBxGlucManianaMi);
                                checkAndSetCheckBoxById(losdias, "jueves", R.id.chBxGlucManianaJ);
                                checkAndSetCheckBoxById(losdias, "viernes", R.id.chBxGlucManianaV);
                                checkAndSetCheckBoxById(losdias, "sabado", R.id.chBxGlucManianaS);
                                checkAndSetCheckBoxById(losdias, "domingo", R.id.chBxGlucManianaD);


                            } else if (elturno.toLowerCase().equalsIgnoreCase("tarde")) {
                                GlucosaTardeID = r.recordatorioID;
                                checkAndSetCheckBoxById(losdias, "lunes", R.id.chBxGlucTardeLu);
                                checkAndSetCheckBoxById(losdias, "martes", R.id.chBxGlucTardeMa);
                                checkAndSetCheckBoxById(losdias, "miercoles", R.id.chBxGlucTardeMi);
                                checkAndSetCheckBoxById(losdias, "jueves", R.id.chBxGlucTardeJ);
                                checkAndSetCheckBoxById(losdias, "viernes", R.id.chBxGlucTardeV);
                                checkAndSetCheckBoxById(losdias, "sabado", R.id.chBxGlucTardeS);
                                checkAndSetCheckBoxById(losdias, "domingo", R.id.chBxGlucTardeD);


                            } else if (elturno.toLowerCase().equalsIgnoreCase("noche")) {
                                GlucosaNocheID = r.recordatorioID;
                                checkAndSetCheckBoxById(losdias, "lunes", R.id.chBxGlucNocheLu);
                                checkAndSetCheckBoxById(losdias, "martes", R.id.chBxGlucNocheMa);
                                checkAndSetCheckBoxById(losdias, "miercoles", R.id.chBxGlucNocheMi);
                                checkAndSetCheckBoxById(losdias, "jueves", R.id.chBxGlucNocheJ);
                                checkAndSetCheckBoxById(losdias, "viernes", R.id.chBxGlucNocheV);
                                checkAndSetCheckBoxById(losdias, "sabado", R.id.chBxGlucNocheS);
                                checkAndSetCheckBoxById(losdias, "domingo", R.id.chBxGlucNocheD);
                            }
                        }
                        else{
                            ActualizarRecordatorioGlucosa = false;
                            Toast.makeText(RegistroActivity.this, "fallo al encontrar el recordatorio de glucosa", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void pedirAlaBaseSobrePresion(final String email){

        myRecordatoriosPresionFb.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot users) {

                Recordatorio r;
                Iterable<DataSnapshot> usersChildren = users.getChildren();

                for (DataSnapshot recordatorio : usersChildren) {
                    r = recordatorio.getValue(Recordatorio.class);
                    String elturno = "", losdias = "";
                    if (r != null) {
                        elturno = r.turno.toLowerCase();
                        losdias = r.dias.toLowerCase();

                        if(r.recordatorioID != null && r.recordatorioID != ""){
                            ActualizarRecordatorioPresion = true;
                            if(elturno.toLowerCase().equalsIgnoreCase("mañana")){
                                PresionManianaID= r.recordatorioID;
                                checkAndSetCheckBoxById(losdias,"lunes",R.id.chBxPresionManianaL);
                                checkAndSetCheckBoxById(losdias,"martes",R.id.chBxPresionManianaMa);
                                checkAndSetCheckBoxById(losdias,"miercoles",R.id.chBxPresionManianaMi);
                                checkAndSetCheckBoxById(losdias,"jueves",R.id.chBxPresionManianaJ);
                                checkAndSetCheckBoxById(losdias,"viernes",R.id.chBxPresionManianaV);
                                checkAndSetCheckBoxById(losdias,"sabado",R.id.chBxPresionManianaS);
                                checkAndSetCheckBoxById(losdias,"lunes",R.id.chBxPresionManianaD);
                            }
                            else if (elturno.toLowerCase().equalsIgnoreCase("tarde")){
                                PresionTardeID = r.recordatorioID;
                                checkAndSetCheckBoxById(losdias,"lunes",R.id.chBxPresionTardeLu);
                                checkAndSetCheckBoxById(losdias,"martes",R.id.chBxPresionTardeMa);
                                checkAndSetCheckBoxById(losdias,"miercoles",R.id.chBxPresionTardeMi);
                                checkAndSetCheckBoxById(losdias,"jueves",R.id.chBxPresionTardeJ);
                                checkAndSetCheckBoxById(losdias,"viernes",R.id.chBxPresionTardeV);
                                checkAndSetCheckBoxById(losdias,"sabado",R.id.chBxPresionTardeS);
                                checkAndSetCheckBoxById(losdias,"domingo",R.id.chBxPresionTardeD);


                            } else if (elturno.toLowerCase().equalsIgnoreCase("noche")){
                                PresionNocheID = r.recordatorioID;
                                checkAndSetCheckBoxById(losdias,"lunes",R.id.chBxPresionNocheL);
                                checkAndSetCheckBoxById(losdias,"martes",R.id.chBxPresionNocheMa);
                                checkAndSetCheckBoxById(losdias,"miercoles",R.id.chBxPresionNocheMi);
                                checkAndSetCheckBoxById(losdias,"jueves",R.id.chBxPresionNocheJ);
                                checkAndSetCheckBoxById(losdias,"viernes",R.id.chBxPresionNocheV);
                                checkAndSetCheckBoxById(losdias,"sabado",R.id.chBxPresionNocheS);
                                checkAndSetCheckBoxById(losdias,"domingo",R.id.chBxPresionNocheD);
                            }

                        }
                        else{
                            ActualizarRecordatorioPresion = false;
                            Toast.makeText(RegistroActivity.this, "fallo al encontrar el recordatorio de glucosa", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void checkAndSetCheckBoxById(String listaDias,String dia,int checkBoxById) {
        if(listaDias.contains(dia)) {
            CheckBox checkBox = findViewById(checkBoxById);
            checkBox.setChecked(true);
        }
    }


    private void UpdateUser() {
        //uso la variable global userID obtenida previamente en funcion "pedirAlaBaseUsuarioByEmail(email)"
        // luego Asigno en la tabla users en el child "userID" (con el valor de ese userID) los nuevos valores del usuario
        String userID=UserID !=null? UserID : "" ;
        if(!userID.equals("")) {
            equipoFavorito = footballService.getNombreReferencia(spinnerEquipo.getSelectedItem().toString());
            myUsersFb.child(userID).setValue(
                    new User(userID, nombreAbuelo.getText().toString().toLowerCase(), mailTutor.getText().toString().toLowerCase(),
                            equipoFavorito, medicamentosM.getText().toString().toLowerCase(),
                            medicamentosT.getText().toString().toLowerCase(), medicamentosN.getText().toString().toLowerCase()));
        }
        else{
            Toast.makeText(RegistroActivity.this, "no se pudo actualizar al usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private void UpdatePresion() {
        // uso la variable global presionTurnoID obtenida previamente en funcion "pedirAlaBaseSobrePresion(email)"
        // luego Asigno en la tabla recordatoriosPresion en el child "presionTurnoID" (con el valor de ese presionTurnoID) los nuevos valores del recordatoriosPresion
        if(PresionManianaID != "" && PresionManianaID != null){
            myRecordatoriosPresionFb.child(PresionManianaID).setValue(new Recordatorio(PresionManianaID,mailTutor.getText().toString(),RecordatoriosPresionManiana(),"mañana"));
        }
        if(PresionTardeID != "" && PresionTardeID != null){
            myRecordatoriosPresionFb.child(PresionTardeID).setValue(new Recordatorio(PresionTardeID,mailTutor.getText().toString(),RecordatoriosPresionTarde(),"tarde"));
        }
        if(PresionNocheID != "" && PresionNocheID != null){
            myRecordatoriosPresionFb.child(PresionNocheID).setValue(new Recordatorio(PresionNocheID,mailTutor.getText().toString(),RecordatoriosPresionNoche(),"noche"));
        }
    }


    private void UpdateGlucosa() {
        //// uso la variable global glucosaTurnoID obtenida previamente en funcion "pedirAlaBaseSobreGlucosa(email)"
        //Asigno en la tabla recordatoriosGlucosa en el child "glucosaTurnoID" (con el valor de ese glucosaTurnoID) los nuevos valores del recordatoriosGlucosa
        if(GlucosaManianaID != "" && GlucosaManianaID != null){
            myRecordatoriosGlucosaFb.child(GlucosaManianaID).setValue(new Recordatorio(GlucosaManianaID,mailTutor.getText().toString(),RecordatoriosGlucosaManiana(),"mañana"));
        }
        if(GlucosaTardeID != "" && GlucosaTardeID != null){
            myRecordatoriosGlucosaFb.child(GlucosaTardeID).setValue(new Recordatorio(GlucosaTardeID,mailTutor.getText().toString(),RecordatoriosGlucosaTarde(),"tarde"));
        }
        if(GlucosaNocheID != "" && GlucosaNocheID != null){
            myRecordatoriosGlucosaFb.child(GlucosaNocheID).setValue(new Recordatorio(GlucosaNocheID,mailTutor.getText().toString(),RecordatoriosGlucosaNoche(),"noche"));
        }
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

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public void onResume() {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);
    }


}