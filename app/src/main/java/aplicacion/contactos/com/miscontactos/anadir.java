package aplicacion.contactos.com.miscontactos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class anadir extends AppCompatActivity {

    Button bt_aceptar;
    TextView tv_nombre;
    TextView tv_apellido;
    TextView tv_domicilio;
    TextView tv_telefono;
    TextView tv_email;
    BDInterna bdInterna;
    Button bt_imagen;

    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


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
        bt_imagen = (Button) findViewById(R.id.imagen);

    }
    public void clickpulsar(View v){
        // comprobar todos los campos
        boolean error = false;
        if (tv_nombre.getText().length()==0) {
            Toast.makeText(this, "Debes rellenar mínimo el nombre", Toast.LENGTH_SHORT).show();
            error = true;
        }
        //guardar
        if (error==false) {
            System.out.println("GRABANDO");
            bdInterna = new BDInterna(this);
            bdInterna.insertarContacto(
                    tv_nombre.getText().toString(),
                    tv_apellido.getText().toString(),
                    tv_domicilio.getText().toString(),
                    tv_telefono.getText().toString(),
                    tv_email.getText().toString()
                    );

            startActivity(getIntent());
            finish();
        }
    }

    /*public void clickImagen(View v) {
        // comprobar todos los campos
        String ruta_fotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/misfotos/";


        String file = ruta_fotos + getCode() + ".jpg";
        File mi_foto = new File(file);
        try {
            mi_foto.createNewFile();
        } catch (IOException ex) {
            Log.e("ERROR ", "Error:" + ex);
        }
        //

        System.out.println("DEBUG " + mi_foto);
        Uri uri = Uri.fromFile(mi_foto);
        //Abre la camara para tomar la foto
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Guarda imagen
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //Retorna a la actividad
        startActivityForResult(cameraIntent, 0);
    }

    @SuppressLint("SimpleDateFormat")
    private String getCode() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoCode = "pic_" + date;
        return photoCode;
    }*/

    public void clickImagen(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /*Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "fname_" +
                String.valueOf(System.currentTimeMillis()) + ".jpg"));*/

        String ruta_fotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"";


        String file = ruta_fotos + String.valueOf(System.currentTimeMillis()) + ".jpg";
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, ruta_fotos);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

                //use imageUri here to access the image

                Bundle extras = data.getExtras();

                Double imageUri=null;
                Log.e("URI",imageUri.toString());

                Bitmap bmp = (Bitmap) extras.get("data");

                // here you will get the image as bitmap


            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
            }
        }


    }

}


