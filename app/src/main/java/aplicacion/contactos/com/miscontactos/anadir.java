package aplicacion.contactos.com.miscontactos;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class anadir extends AppCompatActivity {

    Button bt_aceptar;
    TextView tv_nombre;
    TextView tv_apellido;
    TextView tv_domicilio;
    TextView tv_telefono;
    TextView tv_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bt_aceptar = (Button)findViewById(R.id.aceptar);
        tv_nombre = (TextView)findViewById(R.id.id_nombre);
        tv_apellido = (TextView)findViewById(R.id.id_apellidos);
        tv_domicilio = (TextView)findViewById(R.id.id_domicilio);
        tv_telefono = (TextView)findViewById(R.id.id_telefono);
        tv_email = (TextView)findViewById(R.id.id_email);

    }
    public void clickpulsar(View v){
        // comprobar todos los campos
        boolean error = false;
        if (tv_nombre.getText().length()==0) {
            Toast.makeText(this, "Debes rellenar mínimo el nombre", Toast.LENGTH_SHORT).show();
            error = true;
        }
        //guardar
        if (error=false) {

        }

    }

}
