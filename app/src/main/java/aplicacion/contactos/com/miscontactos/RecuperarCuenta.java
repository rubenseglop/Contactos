package aplicacion.contactos.com.miscontactos;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class RecuperarCuenta extends AppCompatActivity {

    private Button bt_enviarEmail, bt_enviarRespuesta;
    private EditText et_email_respuesta, et_respuesta;

    BDInterna bdInterna;
    BDExterna bdExterna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_cuenta);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bt_enviarEmail = (Button) findViewById(R.id.bt_enviarEmail);
        bt_enviarRespuesta = (Button) findViewById(R.id.bt_enviarRespuesta);
        bt_enviarRespuesta.setEnabled(false);
        et_email_respuesta = (EditText)findViewById(R.id.et_email_respuesta);
        et_respuesta = (EditText)findViewById(R.id.et_respuesta);
        et_respuesta.setEnabled(false);

        bdInterna = new BDInterna(this);
        bdExterna = new BDExterna(this);

        try{BDInterna.clearUUID();}catch (NullPointerException e){ /*borrado de la uuid*/}


        bt_enviarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //todo confirmar correo

                boolean existe = false;
                ArrayList<UsuariosGaleria> usuarios = BDExterna.devuelveUsuarios(RecuperarCuenta.this);
                for (int i = 0; i < usuarios.size(); i++) {
                    System.out.println("DEBUG compara " + usuarios.get(i).getEmail().trim().equals(String.valueOf(et_email_respuesta.getText()).trim()));
                    if (usuarios.get(i).getEmail().trim().equals(String.valueOf(et_email_respuesta.getText()).trim())) {
                        existe = true;
                    }
                }
                if (existe==true){
                    //todo enviar email

                    bdExterna.insertarClave(String.valueOf(et_email_respuesta.getText()).trim(),RecuperarCuenta.this);

                    Toast.makeText(RecuperarCuenta.this, R.string.enviado_email, Toast.LENGTH_SHORT).show();
                    bt_enviarRespuesta.setEnabled(true);
                    et_respuesta.setEnabled(true);




                } else
                    Toast.makeText(RecuperarCuenta.this, R.string.noexisteusuario, Toast.LENGTH_SHORT).show();
            }
        });

        bt_enviarRespuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean recuperada = false;

                ArrayList<UsuariosGaleria> usuarios = BDExterna.devuelveUsuarios(RecuperarCuenta.this);
                for (int i = 0; i < usuarios.size(); i++) {
                    System.out.println("DEBUG compara " + usuarios.get(i).getClave().trim().equals(String.valueOf(et_respuesta.getText()).trim()));
                    if (usuarios.get(i).getClave().trim().equals(String.valueOf(et_respuesta.getText()).trim())) {
                        bdInterna.crearUUID(usuarios.get(i).getUUID());
                        recuperada = true;
                    }
                }
                if (recuperada == true) {
                    Toast.makeText(RecuperarCuenta.this, "RECUPERADA!!", Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("EDIT",true);
                    setResult(RESULT_OK,returnIntent);
                    finish();
                } else
                    Toast.makeText(RecuperarCuenta.this, "Contraseña erronea", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
