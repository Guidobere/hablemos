package app.hablemos.hablemos3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;


public class RegistroActivity extends AppCompatActivity {

    private EditText nombreAbuelo;
    private EditText mailTutor;
    private EditText equipoFavorito;
    private EditText medicamentosM;
    private EditText medicamentosT;
    private EditText medicamentosN;

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

    }
}
