package aplicacion.contactos.com.miscontactos;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BDExterna bdExterna = new BDExterna();

        TextView tv_nombreUsuario = (TextView) findViewById(R.id.tv_nombreUsuario);
        TextView tv_emailUsuario = (TextView) findViewById(R.id.tv_emailUsuario);
        ImageView fotoUsuario = (ImageView) findViewById(R.id.fotoUsuario);
        Button bt_fotoUsuario = (Button) findViewById(R.id.bt_fotoUsuario);
        Button bt_aceptaConfig= (Button) findViewById(R.id.bt_aceptaConfig);

        bt_aceptaConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bdExterna.insertarUsuario(
                        tv_nombreUsuario.getText(),
                        tv_emailUsuario.getText(),
                        "mipath",
                        fotoUsuario,
                        "uueiruere"
                        );
                Toast.makeText(LoginActivity.this, "hecho", Toast.LENGTH_SHORT).show();
            }
        });


        bt_fotoUsuario.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CameraUtils.checkPermissions(getApplicationContext())) {
                    //captureImage();
                } else {
                    //requestCameraPermission(MEDIA_TYPE_IMAGE);
                }
            }
        });


    }

}
