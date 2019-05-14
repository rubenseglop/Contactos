package aplicacion.contactos.com.miscontactos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;
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
    Button masdomicilio;
    Button mastelefono;

    ArrayList<String> StringDomicilio = new ArrayList<String>();
    ArrayList<String> StringTelefono = new ArrayList<String>();


    /*
    Declarar instancias globales
    */
    private RecyclerView recyclerdomicilio;
    private RecyclerView recyclertelefono;
    private static RecyclerView.Adapter adapterdomi;
    private static RecyclerView.Adapter adaptertelf;
    private RecyclerView.LayoutManager lManagerDom;
    private RecyclerView.LayoutManager lManagerTelf;

    // key to store image path in savedInstance state
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";
    public static final int MEDIA_TYPE_IMAGE = 1;

    // Bitmap sampling size
    public static final int BITMAP_SAMPLE_SIZE = 8;

    // Gallery directory name to store the images or videos
    public static final String GALLERY_DIRECTORY_NAME = "Hello_Camera";

    // Image and Video file extensions
    public static final String IMAGE_EXTENSION = "jpg";

    private static String imageStoragePath;

    private String UPLOAD_PHP = BDExternaLinks.upload_php;

    private Bitmap bitmap;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("ResourceAsColor")
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
        masdomicilio = findViewById(R.id.masdomicilio);
        mastelefono = findViewById(R.id.mastelefono);

        // Obtener el Recycler para el adatapdor DMAdapter
        recyclerdomicilio = (RecyclerView) findViewById(R.id.recicladordomicilio);
        recyclerdomicilio.setHasFixedSize(true);
        // Usar un administrador para LinearLayout
        lManagerDom = new LinearLayoutManager(this);
        recyclerdomicilio.setLayoutManager(lManagerDom);
        // Crear un nuevo adaptador
        adapterdomi = new DMAdapter(StringDomicilio);
        recyclerdomicilio.setAdapter(adapterdomi);

        // Obtener el Recycler para el adatapdor TFAdapter
        recyclertelefono = (RecyclerView) findViewById(R.id.recicladortelefono);
        recyclertelefono.setHasFixedSize(true);
        // Usar un administrador para LinearLayout
        lManagerTelf = new LinearLayoutManager(this);
        recyclertelefono.setLayoutManager(lManagerTelf);
        // Crear un nuevo adaptador
        adaptertelf = new TFAdapter(StringTelefono);
        recyclertelefono.setAdapter(adaptertelf);


        // Chequea si tu dispositivo tiene incorporada una cámara
        if (!CameraUtils.isDeviceSupportCamera(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),
                    R.string.error_camara,
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

        masdomicilio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(adapterdomi.getItemCount()<=2) {
                    StringDomicilio.add("");
                    actualizarAdaptador();
                }
                else
                    Toast.makeText(Anadir.this, R.string.limite3,
                            Toast.LENGTH_LONG).show();
            }
        });


        mastelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adaptertelf.getItemCount()<=2) {
                    StringTelefono.add("");
                    actualizarAdaptador();
                }
                else
                    Toast.makeText(Anadir.this, R.string.limite3,
                            Toast.LENGTH_LONG).show();
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
                else {imageStoragePath = BDExternaLinks.imageStoragePath;}

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

    public static void actualizarAdaptador(){
        adapterdomi.notifyDataSetChanged();
        adaptertelf.notifyDataSetChanged();

    }

    /**
     * Método que al hacer click en el Botón Añadir, verifica que minimo tenga un nombre,
     * busca los últimos ID de las tablas Galeria, Domicilio, Telefono y añade el contacto
     * en cada una de las tablas.
     * Finaliza subiendo la posible foto
     * @param v
     */
    public void clickAnadir(View v){

        boolean error = false;
        if (tv_nombre.getText().length()==0) {
            Toast.makeText(this, R.string.minimo_nombre, Toast.LENGTH_SHORT).show();
            error = true;
        }
        //guardar
        if (error==false) {
            if(imageStoragePath == null) { imageStoragePath = BDExternaLinks.imageStoragePath;}
            System.out.println("DEBUG GRABANDO" + imageStoragePath);
            bdInterna = new BDInterna(this);
            //buscamos los ultimos id
            int last_galeria_id = bdInterna.ultimo_id("GALERIA");
            int last_domicilio_id = bdInterna.ultimo_id("DOMICILIO");
            int last_telefono_id = bdInterna.ultimo_id("TELEFONO");


            //inserto contacto con las ultimas id

            bdInterna.insertarContacto(
                    imageStoragePath,  // todo CREAR UN TRIGGER EN MYSQL QUE CONVIERTA EL IMAGE PATH POR SU URL
                    tv_nombre.getText().toString(),
                    tv_apellido.getText().toString(),
                    last_galeria_id,
                    last_domicilio_id,
                    last_telefono_id,
                    tv_email.getText().toString(),
                    bdInterna.getUniqueID()
            );
            bdInterna.insertarGaleria(last_galeria_id,null); //todo implementar la galeria de fotos


            for (int i = 0; i < adapterdomi.getItemCount(); i++) {
                bdInterna.insertarDomicilio(last_domicilio_id,DMAdapter.mDatasetDOM.get(i));
            }
            for (int i = 0; i < adaptertelf.getItemCount(); i++) {
                bdInterna.insertarTelefono(last_telefono_id, TFAdapter.mDatasetTEL.get(i));
            }
            uploadImage(imageStoragePath);

            imageStoragePath=null;
            startActivity(getIntent());
            finish();
        }
    }

    /**
     * Método que sube por WebService un bitmap
     * Debe comprobar que existe nueva foto para subir o no (en el PHP).
     * @param nombre String con la Uri
     */
    private void uploadImage(String nombre) {
        //Mostrar el diálogo de progreso

        //TODO Revisar estos errores de dialogo
        final ProgressDialog loading = ProgressDialog.show(this, Integer.toString(R.string.subiendo), Integer.toString(R.string.mensaje_subida), false, false);

        System.out.println("DEBUG ejecutrando " + UPLOAD_PHP);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_PHP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Descartar el diálogo de progreso
                        loading.dismiss();
                        //Mostrando el mensaje de la respuesta
                        System.out.println("DEBUG HASTA AQUI BIEN " + nombre);
                        Toast.makeText(Anadir.this, s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Descartar el diálogo de progreso
                        System.out.println("DEBUG HASTA ALLA ERROR");
                        loading.dismiss();
                        //Showing toast
                        //Toast.makeText(Anadir.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Convertir bits a cadena
                String imagen=null;

                if (bitmap!=null){
                    imagen = getStringImagen(bitmap);
                } else {
                    imagen = getStringImagen(drawableToBitmap(fotoperfil.getDrawable()));
                }

                //Creación de parámetros
                Map<String, String> params = new Hashtable<String, String>();
                //Agregando de parámetros al PHP de Upload.php
                params.put("UUID", bdInterna.getUniqueID());
                params.put("FOTO", imagen);
                params.put("PATH", nombre);
                //Parámetros de retorno

                System.out.println("DEBUG FOTO " + bdInterna.getUniqueID() + " " + imagen.isEmpty() + " " + nombre);
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

    /**
     * Método que convierte un recurso Drawable en un Bitmap
     * @param drawable
     * @return Devuelve un Bitmap de la foto
     */
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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
     * Muestra la imagen de la galeria
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
     * Alert dialog to navigate to app settings
     * to enable necessary permissions
     */
    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permisos_camara)
                .setMessage(R.string.mensaje_error_camara)
                .setPositiveButton(R.string.ir_config, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(Anadir.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }
}


