package app.hablemos.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import app.hablemos.model.HorariosRecordatorios;
import app.hablemos.model.Recordatorio;
import app.hablemos.model.User;
import app.hablemos.services.FootballService;


public class RegistroActivity extends AppCompatActivity {
    //Lonuevo
    private TextInputLayout nombreAbuelo , mailTutor, contra,  repetirPwd, medicamentosM, medicamentosT,medicamentosN;
    private TextInputEditText mN,mM,mT,n,emailTutor,erepetirPwd, econtra;

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
    private Spinner spinnerEquipo;

    //Remedios
    private Spinner spinnerHoraMedManiana;
    private Spinner spinnerHoraMedTarde;
    private Spinner spinnerHoraMedNoche;
    private Spinner spinnerMinutosMedManiana;
    private Spinner spinnerMinutosMedTarde;
    private Spinner spinnerMinutosMedNoche;

    //Presion
    private Spinner spinnerHoraPresionManiana;
    private Spinner spinnerHoraPresionTarde;
    private Spinner spinnerHoraPresionNoche;
    private Spinner spinnerMinutosPresionManiana;
    private Spinner spinnerMinutosPresionTarde;
    private Spinner spinnerMinutosPresionNoche;

    //Glucosa
    private Spinner spinnerHoraGlucosaManiana;
    private Spinner spinnerHoraGlucosaTarde;
    private Spinner spinnerHoraGlucosaNoche;
    private Spinner spinnerMinutosGlucosaManiana;
    private Spinner spinnerMinutosGlucosaTarde;
    private Spinner spinnerMinutosGlucosaNoche;

    private FootballService footballService;
    private int a=0;

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
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        myUsersFb = FirebaseDatabase.getInstance().getReference().child(getString(R.string.fbUsuarios));
        myRecordatoriosGlucosaFb = FirebaseDatabase.getInstance().getReference().child(getString(R.string.fbRecordatoriosGlucosa));
        myRecordatoriosPresionFb = FirebaseDatabase.getInstance().getReference().child(getString(R.string.fbRecordatoriosPresion));

        setContentView(R.layout.nuevaconfig);

        //Lonuevo
        nombreAbuelo = findViewById(R.id.txtAbuelo_id);
        mailTutor= findViewById(R.id.txtEmail_id);
        contra= findViewById(R.id.txtContra_id);
        repetirPwd= findViewById(R.id.txtContra2_id);
        medicamentosM= findViewById(R.id.txtmaniana_id);
        medicamentosT= findViewById(R.id.txttarde_id);
        medicamentosN= findViewById(R.id.txtnoche_id);

        mN= findViewById(R.id.txtnoche);
        mT= findViewById(R.id.txttarde);
        mM= findViewById(R.id.txtmaniana);
        n=findViewById(R.id.txtAbuelo);
        emailTutor=findViewById(R.id.txtEmail);
        erepetirPwd=findViewById(R.id.txtContra2);
        econtra=findViewById(R.id.txtContra);

        Button botonRegistro = findViewById(R.id.btnRegister);
        Button botonGuardar = findViewById(R.id.btnGuardar);
        Button botonCancel = findViewById(R.id.btnCancel);

        footballService = new FootballService(this);

        spinnerEquipo = (Spinner) findViewById(R.id.spinnerEquipo);

        spinnerHoraMedManiana = (Spinner) findViewById(R.id.spinnerHoraMedManiana);
        spinnerHoraMedTarde = (Spinner) findViewById(R.id.spinnerHoraMedTarde);
        spinnerHoraMedNoche = (Spinner) findViewById(R.id.spinnerHoraMedNoche);
        spinnerMinutosMedManiana = (Spinner) findViewById(R.id.spinnerMinutosMedManiana);
        spinnerMinutosMedTarde = (Spinner) findViewById(R.id.spinnerMinutosMedTarde);
        spinnerMinutosMedNoche = (Spinner) findViewById(R.id.spinnerMinutosMedNoche);

        spinnerHoraPresionManiana = (Spinner) findViewById(R.id.spinnerHoraPresionManiana);
        spinnerHoraPresionTarde = (Spinner) findViewById(R.id.spinnerHoraPresionTarde);
        spinnerHoraPresionNoche = (Spinner) findViewById(R.id.spinnerHoraPresionNoche);
        spinnerMinutosPresionManiana = (Spinner) findViewById(R.id.spinnerMinutosPresionManiana);
        spinnerMinutosPresionTarde = (Spinner) findViewById(R.id.spinnerMinutosPresionTarde);
        spinnerMinutosPresionNoche = (Spinner) findViewById(R.id.spinnerMinutosPresionNoche);

        spinnerHoraGlucosaManiana = (Spinner) findViewById(R.id.spinnerHoraGlucosaManiana);
        spinnerHoraGlucosaTarde = (Spinner) findViewById(R.id.spinnerHoraGlucosaTarde);
        spinnerHoraGlucosaNoche = (Spinner) findViewById(R.id.spinnerHoraGlucosaNoche);
        spinnerMinutosGlucosaManiana = (Spinner) findViewById(R.id.spinnerMinutosGlucosaManiana);
        spinnerMinutosGlucosaTarde = (Spinner) findViewById(R.id.spinnerMinutosGlucosaTarde);
        spinnerMinutosGlucosaNoche = (Spinner) findViewById(R.id.spinnerMinutosGlucosaNoche);

        cargarEquiposEnSpinner();

        cargarHorasEnSpinners();

        //Es modificación
        if(mAuth.getCurrentUser() != null) {
            botonRegistro.setVisibility(View.GONE);
            botonGuardar.setVisibility(View.VISIBLE);
            a=1;
        }
        //Es alta
        else{
            botonRegistro.setVisibility(View.VISIBLE);
            botonGuardar.setVisibility(View.GONE);
            a=0;

            cargarSpinnersSaludDefault(spinnerHoraMedManiana, spinnerMinutosMedManiana,
                    spinnerHoraMedTarde, spinnerMinutosMedTarde, spinnerHoraMedNoche, spinnerMinutosMedNoche, "00");
            cargarSpinnersSaludDefault(spinnerHoraGlucosaManiana, spinnerMinutosGlucosaManiana,
                    spinnerHoraGlucosaTarde, spinnerMinutosGlucosaTarde, spinnerHoraGlucosaNoche, spinnerMinutosGlucosaNoche, "15");
            cargarSpinnersSaludDefault(spinnerHoraPresionManiana, spinnerMinutosPresionManiana,
                    spinnerHoraPresionTarde, spinnerMinutosPresionTarde, spinnerHoraPresionNoche, spinnerMinutosPresionNoche, "30");
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
                User user = ActualizarUsuario();
                if(a==1) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("user", user);
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

    private void cargarHorasEnSpinners() {
        List<String> spinnerArrayHorasManiana = new ArrayList<>();
        spinnerArrayHorasManiana.add(0,"");
        spinnerArrayHorasManiana.add(1,"6");
        spinnerArrayHorasManiana.add(2,"7");
        spinnerArrayHorasManiana.add(3,"8");
        spinnerArrayHorasManiana.add(4,"9");
        spinnerArrayHorasManiana.add(5,"10");
        spinnerArrayHorasManiana.add(6,"11");
        spinnerArrayHorasManiana.add(7,"12");

        List<String> spinnerArrayHorasTarde = new ArrayList<>();
        spinnerArrayHorasTarde.add(0,"");
        spinnerArrayHorasTarde.add(1,"13");
        spinnerArrayHorasTarde.add(2,"14");
        spinnerArrayHorasTarde.add(3,"15");
        spinnerArrayHorasTarde.add(4,"16");
        spinnerArrayHorasTarde.add(5,"17");
        spinnerArrayHorasTarde.add(6,"18");

        List<String> spinnerArrayHorasNoche = new ArrayList<>();
        spinnerArrayHorasNoche.add(0,"");
        spinnerArrayHorasNoche.add(1,"19");
        spinnerArrayHorasNoche.add(2,"20");
        spinnerArrayHorasNoche.add(3,"21");
        spinnerArrayHorasNoche.add(4,"22");
        spinnerArrayHorasNoche.add(5,"23");

        List<String> spinnerArrayMinutos = new ArrayList<>();
        spinnerArrayMinutos.add(0,"");
        spinnerArrayMinutos.add(1,"00");
        spinnerArrayMinutos.add(2,"15");
        spinnerArrayMinutos.add(3,"30");
        spinnerArrayMinutos.add(4,"45");

        ArrayAdapter<String> adapterHorasManiana = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArrayHorasManiana);
        ArrayAdapter<String> adapterHorasTarde = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArrayHorasTarde);
        ArrayAdapter<String> adapterHorasNoche = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArrayHorasNoche);
        ArrayAdapter<String> adapterMinutos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArrayMinutos);
        adapterHorasManiana.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterHorasTarde.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterHorasNoche.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterMinutos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        buildSpinnerHoraMinutos(spinnerHoraMedManiana, spinnerMinutosMedManiana, adapterHorasManiana, adapterMinutos);
        buildSpinnerHoraMinutos(spinnerHoraMedTarde, spinnerMinutosMedTarde, adapterHorasTarde, adapterMinutos);
        buildSpinnerHoraMinutos(spinnerHoraMedNoche, spinnerMinutosMedNoche, adapterHorasNoche, adapterMinutos);

        buildSpinnerHoraMinutos(spinnerHoraGlucosaManiana, spinnerMinutosGlucosaManiana, adapterHorasManiana, adapterMinutos);
        buildSpinnerHoraMinutos(spinnerHoraGlucosaTarde, spinnerMinutosGlucosaTarde, adapterHorasTarde, adapterMinutos);
        buildSpinnerHoraMinutos(spinnerHoraGlucosaNoche, spinnerMinutosGlucosaNoche, adapterHorasNoche, adapterMinutos);

        buildSpinnerHoraMinutos(spinnerHoraPresionManiana, spinnerMinutosPresionManiana, adapterHorasManiana, adapterMinutos);
        buildSpinnerHoraMinutos(spinnerHoraPresionTarde, spinnerMinutosPresionTarde, adapterHorasTarde, adapterMinutos);
        buildSpinnerHoraMinutos(spinnerHoraPresionNoche, spinnerMinutosPresionNoche, adapterHorasNoche, adapterMinutos);
    }

    private void buildSpinnerHoraMinutos(Spinner spinnerHora, Spinner spinnerMinutos, ArrayAdapter<String> adapterHora, ArrayAdapter<String> adapterMinutos) {
        spinnerHora.setAdapter(adapterHora);
        spinnerHora.setOnItemSelectedListener(new HorasItemSelectedListener(spinnerMinutos));
        spinnerMinutos.setAdapter(adapterMinutos);
        spinnerMinutos.setOnItemSelectedListener(new MinutosOnItemSelectedListener(spinnerMinutos, spinnerHora));
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
     private String writeNewUser(String username, String email, String equipo, String remediosM, String remediosT, String remediosN) {
        //Obtengo el ID del usuario que se me va a guardar en la BD
        String userID=myUsersFb.push().getKey();
        //Guardo la informacion en el usuario.child(ID) al respectivo usuario.
        if(userID != null && userID != "")
            myUsersFb.child(userID).setValue(new User(userID, username, email, equipo, remediosM, remediosT, remediosN,
                getHorariosRemedios(), getHorariosGlucosa(), getHorariosPresion()));
        else {
            Toast.makeText(RegistroActivity.this, "fallo al crear al usuario", Toast.LENGTH_SHORT).show();
        }
        return userID;
    }

    private void CrearNuevoRecordatoriosGlucosa() {
        String diasRecordatorioGlucManiana = RecordatoriosGlucosaManiana();
        String diasRecordatorioGlucTarde = RecordatoriosGlucosaTarde();
        String diasRecordatorioGlucNoche = RecordatoriosGlucosaNoche();

        String email = mailTutor.getEditText().getText().toString().toLowerCase();
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

        String email = mailTutor.getEditText().getText().toString().toLowerCase();
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
        String email = mailTutor.getEditText().getText().toString().toLowerCase();
        String password = contra.getEditText().getText().toString();

        if(!CamposCompletadosCorrectamente())
            return;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Si no se repite o algo, lo guarda en la base
                            String userID = writeNewUser(nombreAbuelo.getEditText().getText().toString().toLowerCase(),
                                mailTutor.getEditText().getText().toString().toLowerCase(),
                                footballService.getNombreReferencia(spinnerEquipo.getSelectedItem().toString()),
                                medicamentosM.getEditText().getText().toString().toLowerCase(),
                                medicamentosT.getEditText().getText().toString().toLowerCase(),
                                medicamentosN.getEditText().getText().toString().toLowerCase());
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

    private User ActualizarUsuario() {

        if(!CamposCompletadosCorrectamente())
            return null;

        User user = UpdateUser();
        if(ActualizarRecordatorioPresion)
            UpdatePresion();
        else
            Toast.makeText(RegistroActivity.this, "No se pudo actualizar al al recordatorio de glucosa", Toast.LENGTH_SHORT).show();

        if(ActualizarRecordatorioGlucosa)
            UpdateGlucosa();
        else
            Toast.makeText(RegistroActivity.this, "No se pudo actualizar al al recordatorio de presion", Toast.LENGTH_SHORT).show();

        return user;
    }

    private boolean CamposCompletadosCorrectamente() {
        boolean camposEstanOk = true;
        String email = mailTutor.getEditText().getText().toString().toLowerCase();
        String password = contra.getEditText().getText().toString();

        if (TextUtils.isEmpty(nombreAbuelo.getEditText().getText().toString())) {
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
        if (!password.equals(repetirPwd.getEditText().getText().toString())) {
            Toast.makeText(getApplicationContext(), getString(R.string.contraseniasNoCoinciden), Toast.LENGTH_SHORT).show();
            camposEstanOk = false;
            return camposEstanOk;
        }

        if(!password.matches(getString(R.string.regexPassword))) {
            Toast.makeText(getApplicationContext(), getString(R.string.contraseniaMalFormada), Toast.LENGTH_SHORT).show();
            camposEstanOk = false;
        }

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
        myUsersFb.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot users) {

                User u = new User();

                Iterable<DataSnapshot> usersChildren = users.getChildren();
                for (DataSnapshot user : usersChildren) {
                    u = user.getValue(User.class);
                }

                if (u != null && u.userID != null) {
                    llenarCampos(u);
                } else {
                    System.out.println("Ocurrio un error al obtener el usuario");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }

        });
    }

    private void llenarCampos(User u) {
        UserID=u.userID;

        nombreAbuelo.setHint("Nombre usuario");
        mailTutor.setEnabled(false);
        mailTutor.setHint("Email tutor");
        mailTutor.setFocusable(false);
        contra.setHint("Contraseña");
        contra.setFocusable(false);
        contra.setEnabled(false);
        repetirPwd.setHint("Repetir contraseña");
        repetirPwd.setFocusable(false);
        repetirPwd.setEnabled(false);

        if (a==1){
            mM.setText(u.remediosManiana);
            mT.setText(u.remediosTarde);
            mN.setText(u.remediosNoche);
            emailTutor.setText(u.email);
            econtra.setText("123456");
            erepetirPwd.setText("123456");
            n.setText(u.username);

        }else{
            mM.setText(u.remediosManiana);
            mT.setText(u.remediosTarde);
            mN.setText(u.remediosNoche);}
        String nombreEquipo;
        if (u.equipo.equalsIgnoreCase("ninguno")){
            nombreEquipo = "Ninguno";
        } else {
            nombreEquipo = footballService.obtenerEquipoVisual(u.equipo).getNombre();
        }
        spinnerEquipo.setSelection(getIndex(spinnerEquipo, nombreEquipo));

        seterValoresSpinner(u.horariosRecordatoriosRemedios, "00", spinnerHoraMedManiana, spinnerMinutosMedManiana,
            spinnerHoraMedTarde, spinnerMinutosMedTarde, spinnerHoraMedNoche, spinnerMinutosMedNoche);

        seterValoresSpinner(u.horariosRecordatoriosGlucosa, "15", spinnerHoraGlucosaManiana, spinnerMinutosGlucosaManiana,
            spinnerHoraGlucosaTarde, spinnerMinutosGlucosaTarde, spinnerHoraGlucosaNoche, spinnerMinutosGlucosaNoche);

        seterValoresSpinner(u.horariosRecordatoriosPresion, "30", spinnerHoraPresionManiana, spinnerMinutosPresionManiana,
            spinnerHoraPresionTarde, spinnerMinutosPresionTarde, spinnerHoraPresionNoche, spinnerMinutosPresionNoche);
    }

    private void seterValoresSpinner(HorariosRecordatorios horariosRecordatorios, String minutosDefault,
                                     Spinner spinnerHoraManiana, Spinner spinnerMinutosManiana,
                                     Spinner spinnerHoraTarde, Spinner spinnerMinutosTarde,
                                     Spinner spinnerHoraNoche, Spinner spinnerMinutosNoche) {
        if(horariosRecordatorios!=null) {
            //Si tiene cargados los horarios, se setean en el spinner
            spinnerHoraManiana.setSelection(getIndex(spinnerHoraManiana, horariosRecordatorios.manianaHora));
            spinnerMinutosManiana.setSelection(getIndex(spinnerMinutosManiana, horariosRecordatorios.manianaMinutos));
            spinnerHoraTarde.setSelection(getIndex(spinnerHoraTarde, horariosRecordatorios.tardeHora));
            spinnerMinutosTarde.setSelection(getIndex(spinnerMinutosTarde, horariosRecordatorios.tardeMinutos));
            spinnerHoraNoche.setSelection(getIndex(spinnerHoraNoche, horariosRecordatorios.nocheHora));
            spinnerMinutosNoche.setSelection(getIndex(spinnerMinutosNoche, horariosRecordatorios.nocheMinutos));
        } else{
            //Si no tiene horarios cargados se toman los default para setear en los spinner
            cargarSpinnersSaludDefault(spinnerHoraManiana, spinnerMinutosManiana, spinnerHoraTarde, spinnerMinutosTarde,
                spinnerHoraNoche, spinnerMinutosNoche, minutosDefault);
        }
    }

    private void cargarSpinnersSaludDefault(Spinner spinnerHoraManiana, Spinner spinnerMinutosManiana,
                                       Spinner spinnerHoraTarde, Spinner spinnerMinutosTarde,
                                       Spinner spinnerHoraNoche, Spinner spinnerMinutosNoche, String minutosDefault){
        spinnerHoraManiana.setSelection(getIndex(spinnerHoraManiana, getString(R.string.horarioSaludManiana)));
        spinnerMinutosManiana.setSelection(getIndex(spinnerMinutosManiana, minutosDefault));
        spinnerHoraTarde.setSelection(getIndex(spinnerHoraTarde, getString(R.string.horarioSaludTarde)));
        spinnerMinutosTarde.setSelection(getIndex(spinnerMinutosTarde, minutosDefault));
        spinnerHoraNoche.setSelection(getIndex(spinnerHoraNoche, getString(R.string.horarioSaludNoche)));
        spinnerMinutosNoche.setSelection(getIndex(spinnerMinutosNoche, minutosDefault));
    }

    private int getIndex(Spinner spinner, String value){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value))
                return i;
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
                                checkAndSetCheckBoxById(losdias,"domingo",R.id.chBxPresionManianaD);
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

    private HorariosRecordatorios getHorariosGlucosa(){
        return new HorariosRecordatorios(
            spinnerHoraGlucosaManiana.getSelectedItem().toString(), spinnerMinutosGlucosaManiana.getSelectedItem().toString(),
            spinnerHoraGlucosaTarde.getSelectedItem().toString(), spinnerMinutosGlucosaTarde.getSelectedItem().toString(),
            spinnerHoraGlucosaNoche.getSelectedItem().toString(), spinnerMinutosGlucosaNoche.getSelectedItem().toString());
    }

    private HorariosRecordatorios getHorariosRemedios(){
        return new HorariosRecordatorios(
            spinnerHoraMedManiana.getSelectedItem().toString(), spinnerMinutosMedManiana.getSelectedItem().toString(),
            spinnerHoraMedTarde.getSelectedItem().toString(), spinnerMinutosMedTarde.getSelectedItem().toString(),
            spinnerHoraMedNoche.getSelectedItem().toString(), spinnerMinutosMedNoche.getSelectedItem().toString());
    }

    private HorariosRecordatorios getHorariosPresion(){
        return new HorariosRecordatorios(
            spinnerHoraPresionManiana.getSelectedItem().toString(), spinnerMinutosPresionManiana.getSelectedItem().toString(),
            spinnerHoraPresionTarde.getSelectedItem().toString(), spinnerMinutosPresionTarde.getSelectedItem().toString(),
            spinnerHoraPresionNoche.getSelectedItem().toString(), spinnerMinutosPresionNoche.getSelectedItem().toString());
    }

    private User UpdateUser() {
        User user = null;

        //uso la variable global userID obtenida previamente en funcion "pedirAlaBaseUsuarioByEmail(email)"
        // luego Asigno en la tabla users en el child "userID" (con el valor de ese userID) los nuevos valores del usuario
        String userID=UserID !=null? UserID : "" ;
        if(!userID.equals("")) {
            user = new User(userID, nombreAbuelo.getEditText().getText().toString().toLowerCase(),
                mailTutor.getEditText().getText().toString().toLowerCase(),
                footballService.getNombreReferencia(spinnerEquipo.getSelectedItem().toString()),
                medicamentosM.getEditText().getText().toString().toLowerCase(),
                medicamentosT.getEditText().getText().toString().toLowerCase(),
                medicamentosN.getEditText().getText().toString().toLowerCase(), getHorariosRemedios(), getHorariosGlucosa(), getHorariosPresion());
            myUsersFb.child(userID).setValue(user);
        }
        else{
            Toast.makeText(RegistroActivity.this, "no se pudo actualizar al usuario", Toast.LENGTH_SHORT).show();
        }
        return user;
    }

    private void UpdatePresion() {
        // uso la variable global presionTurnoID obtenida previamente en funcion "pedirAlaBaseSobrePresion(email)"
        // luego Asigno en la tabla recordatoriosPresion en el child "presionTurnoID" (con el valor de ese presionTurnoID) los nuevos valores del recordatoriosPresion
        if(PresionManianaID != "" && PresionManianaID != null){
            myRecordatoriosPresionFb.child(PresionManianaID).setValue(new Recordatorio(
                PresionManianaID,mailTutor.getEditText().getText().toString(),RecordatoriosPresionManiana(),"mañana"));
        }
        if(PresionTardeID != "" && PresionTardeID != null){
            myRecordatoriosPresionFb.child(PresionTardeID).setValue(
                new Recordatorio(PresionTardeID,mailTutor.getEditText().getText().toString(),RecordatoriosPresionTarde(),"tarde"));
        }
        if(PresionNocheID != "" && PresionNocheID != null){
            myRecordatoriosPresionFb.child(PresionNocheID).setValue(
                new Recordatorio(PresionNocheID,mailTutor.getEditText().getText().toString(),RecordatoriosPresionNoche(),"noche"));
        }
    }


    private void UpdateGlucosa() {
        // Uso la variable global glucosaTurnoID obtenida previamente en funcion "pedirAlaBaseSobreGlucosa(email)"
        // Asigno en la tabla recordatoriosGlucosa en el child "glucosaTurnoID" (con el valor de ese glucosaTurnoID) los nuevos valores del recordatoriosGlucosa
        if(GlucosaManianaID != "" && GlucosaManianaID != null){
            myRecordatoriosGlucosaFb.child(GlucosaManianaID).setValue(
                new Recordatorio(GlucosaManianaID,mailTutor.getEditText().getText().toString(),RecordatoriosGlucosaManiana(),"mañana"));
        }
        if(GlucosaTardeID != "" && GlucosaTardeID != null){
            myRecordatoriosGlucosaFb.child(GlucosaTardeID).setValue(
                new Recordatorio(GlucosaTardeID,mailTutor.getEditText().getText().toString(),RecordatoriosGlucosaTarde(),"tarde"));
        }
        if(GlucosaNocheID != "" && GlucosaNocheID != null){
            myRecordatoriosGlucosaFb.child(GlucosaNocheID).setValue(
                new Recordatorio(GlucosaNocheID,mailTutor.getEditText().getText().toString(),RecordatoriosGlucosaNoche(),"noche"));
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


    private class HorasItemSelectedListener implements AdapterView.OnItemSelectedListener {
        Spinner spinnerMinutos;
        public HorasItemSelectedListener(Spinner spinnerMinutos) {
            this.spinnerMinutos = spinnerMinutos;
        }

        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            if(position==0)
                spinnerMinutos.setSelection(0);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    }

    private class MinutosOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        Spinner spinnerMinutos;
        Spinner spinnerHora;

        public MinutosOnItemSelectedListener(Spinner spinnerMinutos, Spinner spinnerHora) {
            this.spinnerMinutos = spinnerMinutos;
            this.spinnerHora = spinnerHora;
        }

        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            if(TextUtils.isEmpty(spinnerHora.getSelectedItem().toString()))
                spinnerMinutos.setSelection(0);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    }
}
