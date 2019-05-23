package aplicacion.contactos.com.miscontactos;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.URIResolver;


public class Anadir extends AppCompatActivity {

    private Button bt_aceptar,bt_imagen,masdomicilio, mastelefono;
    private TextView tv_nombre,tv_apellido,tv_domicilio,tv_telefono,tv_email;
    private ImageView fotoperfil;

    private BDInterna bdInterna;

    private ArrayList<String> StringDomicilio = new ArrayList<String>(),StringTelefono = new ArrayList<String>();
    private ArrayList<Contacto> contactos;
    /*
    Declarar instancias globales
    */
    private RecyclerView recyclerdomicilio, recyclertelefono;
    private static RecyclerView.Adapter adapterdomi, adaptertelf;
    private RecyclerView.LayoutManager lManagerDom, lManagerTelf;

    // key to store image path in savedInstance state
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";
    public static final int MEDIA_TYPE_IMAGE = 1;

    // Bitmap sampling size
    public static final int BITMAP_SAMPLE_SIZE = 8;

    // Gallery directory name to store the imagenes or videos
    public static final String GALLERY_DIRECTORY_NAME = "Hello_Camera", IMAGE_EXTENSION = "jpg";

    private static String imageStoragePath;

    private Bitmap bitmap;
    private Contacto editContacto;

    private String editImagePath;
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
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

      /*  //Colores
        bt_aceptar.setBackgroundColor(TextoApp.colorBoton);
        bt_imagen.setBackgroundColor(TextoApp.colorBoton);
*/
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


        // Recuperar los datos si se dispone, para editar
        try {
            editContacto = (Contacto) getIntent().getSerializableExtra("EDIT");
            if (editContacto != null) {

                tv_nombre.setText(editContacto.getNombre());
                tv_apellido.setText(editContacto.getApellidos());
                tv_email.setText(editContacto.getCorreo());

                if (editContacto.getFoto().equals("NO")){
                    fotoperfil.setImageResource(R.drawable.perfil);
                } else {
                    fotoperfil.setImageBitmap(BitmapFactory.decodeFile(editContacto.getFoto()));
                }
                StringDomicilio.clear();
                for (int i = 0; i < editContacto.getDomicilios().size() ; i++) {
                    StringDomicilio.add(editContacto.getDomicilios().get(i).getDireccion());
                }
                StringTelefono.clear();
                for (int i = 0; i < editContacto.getTelefonos().size() ; i++) {
                    StringTelefono.add(editContacto.getTelefonos().get(i).getNumero());
                }
            }
        } catch (NullPointerException e) {
            //No hay nada que editar aqui
        }

        // Chequea si tu dispositivo tiene incorporada una cámara
        if (!CameraUtils.isDeviceSupportCamera(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),
                    R.string.error_camara,
                    Toast.LENGTH_LONG).show();
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
     * Método que al hacer click en el Botón Añadir, verifica que mínimo tenga un nombre,
     * busca los últimos ID de las tablas Galeria, Domicilio, Telefono y añade el contacto
     * en cada una de las tablas.
     * @param v
     */
    public void clickAnadir(View v) {

        boolean error = false;
        if (tv_nombre.getText().length() == 0) {
            Toast.makeText(this, R.string.minimo_nombre, Toast.LENGTH_SHORT).show();
            error = true;
        }
        //guardar
        if (error == false) {
            if (imageStoragePath == null) {
                imageStoragePath = "NO";
            }
            //System.out.println("DEBUG GRABANDO" + imageStoragePath);
            editImagePath = imageStoragePath;

            bdInterna = new BDInterna(this);
            //buscamos los ultimos id
            int last_galeria_id = bdInterna.ultimo_id("USUARIOS");
            int last_domicilio_id = bdInterna.ultimo_id("USUARIOS");
            int last_telefono_id = bdInterna.ultimo_id("USUARIOS");

            //EDITO un contacto con las ultimas id
            if (editContacto != null) {

                imageStoragePath=editContacto.getFoto();
                if (editImagePath !=null) {imageStoragePath = editImagePath;}
                //System.out.println("DEBUG IMAGEN " + imageStoragePath);

                //Si lo estaba editando, borro ese contacto
                bdInterna.borraContacto(editContacto.getId());

                bdInterna.insertarContacto(
                        editContacto.getId(),
                        imageStoragePath,
                        tv_nombre.getText().toString(),
                        tv_apellido.getText().toString(),
                        editContacto.getGaleria_id(),
                        editContacto.getDireccion_id(),
                        editContacto.getTelefono_id(),
                        tv_email.getText().toString(),
                        bdInterna.getUniqueID()
                );
                for (int i = 0; i < adapterdomi.getItemCount(); i++) {
                    bdInterna.insertarDomicilio(editContacto.getDireccion_id(), DMAdapter.mDatasetDOM.get(i));
                }
                for (int i = 0; i < adaptertelf.getItemCount(); i++) {
                    bdInterna.insertarTelefono(editContacto.getTelefono_id(), TFAdapter.mDatasetTEL.get(i));
                }

            } else {
                //Se genera un nuevo usuario
                bdInterna.insertarContacto(
                        imageStoragePath,
                        tv_nombre.getText().toString(),
                        tv_apellido.getText().toString(),
                        last_galeria_id,
                        last_domicilio_id,
                        last_telefono_id,
                        tv_email.getText().toString(),
                        bdInterna.getUniqueID()
                );
                for (int i = 0; i < adapterdomi.getItemCount(); i++) {
                    bdInterna.insertarDomicilio(last_domicilio_id, DMAdapter.mDatasetDOM.get(i));
                }
                for (int i = 0; i < adaptertelf.getItemCount(); i++) {
                    bdInterna.insertarTelefono(last_telefono_id, TFAdapter.mDatasetTEL.get(i));
                }
            }


            //uploadImage(imageStoragePath);

            imageStoragePath = null;

            if (editContacto !=null ){
                finish();
            } else {
                startActivity(getIntent());
                finish();
            }
        }
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


