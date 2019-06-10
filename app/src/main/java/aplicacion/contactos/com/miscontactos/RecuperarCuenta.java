package aplicacion.contactos.com.miscontactos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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


        // Botón de enviar email
        bt_enviarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Comprueba que existe ese email en la base de datos externa
                boolean existe = false;
                ArrayList<UsuariosGaleria> usuarios = BDExterna.devuelveUsuarios(RecuperarCuenta.this);
                for (int i = 0; i < usuarios.size(); i++) {
                    System.out.println("DEBUG compara " + usuarios.get(i).getEmail().trim().equals(String.valueOf(et_email_respuesta.getText()).trim()));
                    if (usuarios.get(i).getEmail().trim().equals(String.valueOf(et_email_respuesta.getText()).trim())) {
                        existe = true;
                    }
                }

                if (existe==true){

                    // Si existe, genero una clave aleatoria (mediante php)

                    bdExterna.insertarClave(String.valueOf(et_email_respuesta.getText()).trim(),RecuperarCuenta.this);

                    ToastCustomizado.tostada(RecuperarCuenta.this, R.string.enviado_email);
                    bt_enviarRespuesta.setEnabled(true);
                    et_respuesta.setEnabled(true);


                } else
                    ToastCustomizado.tostada(RecuperarCuenta.this, R.string.noexisteusuario);
            }
        });

        // Botón de enviar respuesta a la clave por email
        bt_enviarRespuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean recuperada = false;

                ArrayList<UsuariosGaleria> usuarios = BDExterna.devuelveUsuarios(RecuperarCuenta.this);
                for (int i = 0; i < usuarios.size(); i++) {
                    if (usuarios.get(i).getClave().trim().equals(String.valueOf(et_respuesta.getText()).trim())) {
                        bdInterna.crearUUID(usuarios.get(i).getUUID());
                        recuperada = true;
                    }
                }
                if (recuperada == true) {
                    ToastCustomizado.tostada(RecuperarCuenta.this, R.string.recuperada);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("EDIT", true);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else
                    ToastCustomizado.tostada(RecuperarCuenta.this, R.string.error_contrasena);
            }
        });
    }

}
