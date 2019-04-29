package aplicacion.contactos.com.miscontactos;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLOutput;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class Anadir extends AppCompatActivity {

    Button bt_aceptar;
    TextView tv_nombre;
    TextView tv_apellido;
    TextView tv_domicilio;
    TextView tv_telefono;
    TextView tv_email;
    BDInterna bdInterna;
    Button bt_imagen;
    ImageView fotoperfil;

    // key to store image path in savedInstance state
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";

    public static final int MEDIA_TYPE_IMAGE = 1;


    // Bitmap sampling size
    public static final int BITMAP_SAMPLE_SIZE = 8;

    // Gallery directory name to store the images or videos
    public static final String GALLERY_DIRECTORY_NAME = "Hello Camera";

    // Image and Video file extensions
    public static final String IMAGE_EXTENSION = "jpg";

    private static String imageStoragePath;

    private String UPLOAD_URL ="http://iesayala.ddns.net/BDSegura/misContactos/upload.php";

    private String KEY_IMAGEN = "foto";
    private String KEY_NOMBRE = "nombre";
    private Bitmap bitmap;



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
        fotoperfil = findViewById(R.id.fotoperfil);


        // Checking availability of the camera
        if (!CameraUtils.isDeviceSupportCamera(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device doesn't have camera
            finish();
        }
        /**
         * Capture image on button click
         */
        bt_imagen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CameraUtils.checkPermissions(getApplicationContext())) {
                    captureImage();
                } else {
                    requestCameraPermission(MEDIA_TYPE_IMAGE);
                }
            }
        });

        restoreFromBundle(savedInstanceState);

    }


    /**
     * Restoring store image path from saved instance state
     */
    private void restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_IMAGE_STORAGE_PATH)) {

                if (savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH).length()!=0) {imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);}
                else {imageStoragePath = "/storage/emulated/0/Pictures/Hello Camera/perfil.png";}

                if (!TextUtils.isEmpty(imageStoragePath)) {
                    if (imageStoragePath.substring(imageStoragePath.lastIndexOf(".")).equals("." + IMAGE_EXTENSION)) {
                        previewCapturedImage();
                    }
                }
            }
        }
    }

    /**
     * Requesting permissions using Dexter library
     */
    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    private List<PermissionRequest> permissions;
                    private PermissionToken token;

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            // capture picture
                            if (type == MEDIA_TYPE_IMAGE) captureImage();

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        this.permissions = permissions;
                        this.token = token;
                    }
                }).check();
    }

    /**
     * Capturing Camera Image will launch camera app requested image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);

        //TODO imprimir la foto en fotoperfil; (es con la URI)


        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);


        // start the image capture Intent
        startActivityForResult(intent, 100);
    }

    /**
     * Saving stored image path to saved instance state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putString(KEY_IMAGE_STORAGE_PATH, imageStoragePath);
    }
    /**
     * Restoring image path from saved instance state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
         imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
    }

    // no tocar
    public void clickPulsar(View v){
        boolean error = false;
        if (tv_nombre.getText().length()==0) {
            Toast.makeText(this, "Debes rellenar mínimo el nombre", Toast.LENGTH_SHORT).show();
            error = true;
        }
        //guardar
        if (error==false) {


            if(imageStoragePath == null) { imageStoragePath = "/storage/emulated/0/Pictures/Hello Camera/perfil.png";}
            System.out.println("DEBUG GRABANDO" + imageStoragePath);



            bdInterna = new BDInterna(this);
            bdInterna.insertarContacto(
                    imageStoragePath,
                    tv_nombre.getText().toString(),
                    tv_apellido.getText().toString(),
                    tv_domicilio.getText().toString(),
                    tv_telefono.getText().toString(),
                    tv_email.getText().toString()
            );
            //TODO GUARDAR LA IMAGEN ONLINE
            System.out.println("DEBUG VOY A SUBIR");

            uploadImage(imageStoragePath);

            imageStoragePath=null;
            startActivity(getIntent());
            finish();
        }
    }

    private void uploadImage(String nombre) {
        //Mostrar el diálogo de progreso


            final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor...", false, false);

            System.out.println("DEBUG ejecutrando " + UPLOAD_URL);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Descartar el diálogo de progreso
                            loading.dismiss();
                            //Mostrando el mensaje de la respuesta
                            System.out.println("DEBUG HASTA AQUI " + nombre);
                            Toast.makeText(Anadir.this, s, Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //Descartar el diálogo de progreso
                            System.out.println("DEBUG HASTA ALLA");
                            loading.dismiss();

                            //Showing toast
                            //Toast.makeText(Anadir.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //Convertir bits a cadena
                    String imagen = getStringImagen(bitmap);

                    //Creación de parámetros
                    Map<String, String> params = new Hashtable<String, String>();

                    //Agregando de parámetros
                    params.put(KEY_IMAGEN, imagen);
                    params.put(KEY_NOMBRE, nombre);

                    //Parámetros de retorno
                    return params;
                }
            };

            //Creación de una cola de solicitudes
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //Agregar solicitud a la cola
            requestQueue.add(stringRequest);

    }

    public String getStringImagen(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public String nextSessionId() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            // Refreshing the gallery
            CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

            // successfully captured the image
            // display it in image view
            previewCapturedImage();
        } else if (resultCode == RESULT_CANCELED) {
            // user cancelled Image capture
            Toast.makeText(getApplicationContext(),
                    "User cancelled image capture", Toast.LENGTH_SHORT)
                    .show();
        } else {
            // failed to capture image
            Toast.makeText(getApplicationContext(),
                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    /**
     * Display image from gallery
     */
    private void previewCapturedImage() {
        try {

            fotoperfil.setVisibility(View.VISIBLE);

            bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);

            fotoperfil.setImageBitmap(bitmap);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**

    /**
     * Alert dialog to navigate to app settings
     * to enable necessary permissions
     */
    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(Anadir.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }



    public void clickImagen(View v) {


    }



}


