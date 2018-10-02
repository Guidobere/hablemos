package app.hablemos.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;

import app.hablemos.R;



public class Login extends AppCompatActivity {

    private TextView txtRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        txtRegistrar = (TextView) findViewById(R.id.textRegistro);

        txtRegistrar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(RegistroActivity.class);
            }
        });

    }

    private void startActivity(Class<?> cls) {
        final Intent intent = new Intent(this, cls);
        startActivity(intent);
    }


}
