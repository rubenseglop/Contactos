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
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Anadir extends AppCompatActivity {

    private Button bt_aceptar,bt_imagen,masdomicilio, mastelefono;
    private TextView tv_nombre,tv_apellido,tv_domicilio,tv_telefono,tv_email;
    private ImageView fotoperfil;

    private BDInterna bdInterna;

    private ArrayList<String> StringDomicilio = new ArrayList<String>(),StringTelefono = new ArrayList<String>();

    private RecyclerView recyclerdomicilio, recyclertelefono;

     /*Hago métodos staticos para poder acceder a ellos en la clase Anadir (para
     añadir un notifyDataSetChanged )*/
    private static RecyclerView.Adapter adapterdomi, adaptertelf;

    // LayoutManager mide y posiciona las vistas de elementos dentro de un RecyclerView
    private RecyclerView.LayoutManager lManagerDom, lManagerTelf;

    // key to store image path in savedInstance state
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";
    public static final int MEDIA_TYPE_IMAGE = 1;
    // Bitmap sampling size
    public static final int BITMAP_SAMPLE_SIZE = 8;
    // Gallery directory name to store the imagenes or videos
    public static final String GALLERY_DIRECTORY_NAME = "MisContactos_fotos", IMAGE_EXTENSION = "jpg";
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

        bt_aceptar = findViewById(R.id.aceptar);
        tv_nombre = findViewById(R.id.id_nombre);
        tv_apellido = findViewById(R.id.id_apellidos);
        tv_domicilio = findViewById(R.id.id_domicilio);
        tv_telefono = findViewById(R.id.id_telefono);
        tv_email = findViewById(R.id.id_email);
        bt_imagen = findViewById(R.id.imagen);
        fotoperfil = findViewById(R.id.fotoperfil);
        masdomicilio = findViewById(R.id.masdomicilio);
        mastelefono = findViewById(R.id.mastelefono);

        // Obtener el Recycler para el adatapdor DMAdapter
        recyclerdomicilio = findViewById(R.id.recicladordomicilio);
        recyclerdomicilio.setHasFixedSize(true);
        // Usar un administrador para LinearLayout
        lManagerDom = new LinearLayoutManager(this);
        recyclerdomicilio.setLayoutManager(lManagerDom);
        // Crear un nuevo adaptador
        adapterdomi = new DMAdapter(StringDomicilio);
        recyclerdomicilio.setAdapter(adapterdomi);

        // Obtener el Recycler para el adatapdor TFAdapter
        recyclertelefono = findViewById(R.id.recicladortelefono);
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
                    try {
                        fotoperfil.setImageBitmap(BitmapFactory.decodeFile(editContacto.getFoto()));
                        imageStoragePath = editContacto.getFoto();
                    } catch (Exception e) {
                        System.out.println("DEBUG - Problema al leer la foto " + editContacto.getFoto());
                        // Muestro una foto en blanco
                        fotoperfil.setImageResource(R.drawable.perfil);
                    }
                }
                StringDomicilio.clear();
                for (int i = 0; i < editContacto.getDomicilios().size() ; i++) {
                    if (!editContacto.getDomicilios().get(i).getDireccion().trim().equals("") || editContacto.getDomicilios().get(i).getDireccion().length() != 0) {
                        StringDomicilio.add(editContacto.getDomicilios().get(i).getDireccion());
                    }
                }
                StringTelefono.clear();
                for (int i = 0; i < editContacto.getTelefonos().size(); i++) {
                    if (editContacto.getTelefonos().get(i).getNumero().trim().equals("") || editContacto.getTelefonos().get(i).getNumero().length() != 0) {
                        StringTelefono.add(editContacto.getTelefonos().get(i).getNumero());
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // Chequea si tu dispositivo tiene incorporada una cámara
        if (!CameraUtils.isDeviceSupportCamera(getApplicationContext())) {
            ToastCustomizado.tostada(Anadir.this, R.string.error_camara);
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

                int cantidadDomicilios = adapterdomi.getItemCount();

                if(cantidadDomicilios<=4) {
                    if (cantidadDomicilios > 0) {
                        if (DMAdapter.mDatasetDOM.get(cantidadDomicilios - 1).length() != 0) {
                            StringDomicilio.add("");
                            actualizarAdaptador();
                        } else {
                            ToastCustomizado.tostada(Anadir.this, R.string.rellenardomicilio);
                        }
                    } else {
                        StringDomicilio.add("");
                        actualizarAdaptador();
                    }
                }
                else ToastCustomizado.tostada(Anadir.this,R.string.limite5);
            }
        });

        mastelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int cantidadTelefonos = adaptertelf.getItemCount();

                if(cantidadTelefonos<=2) {
                    if (cantidadTelefonos > 0) {
                        if (TFAdapter.mDatasetTEL.get(cantidadTelefonos - 1).length() != 0) {
                            StringTelefono.add("");
                            actualizarAdaptador();
                        } else {
                            ToastCustomizado.tostada(Anadir.this, R.string.rellenartelefono);
                        }
                    } else {
                        StringTelefono.add("");
                        actualizarAdaptador();
                    }
                }
                else
                    ToastCustomizado.tostada(Anadir.this, R.string.limite3);
            }
        });
    }

    /**
     * Dexter es una libreria que simplifica el proceso de requerir permisos en tiempo de ejecución
     */
    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    private List<PermissionRequest> permissions;
                    private PermissionToken token;

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            // En caso de todos los permisos estén bien, capturar una foto
                            if (type == MEDIA_TYPE_IMAGE) captureImage();

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            dialogPermisos();
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
     * Capturar foto, y el link URI de la foto la envio mediante Bundle
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, 100);
    }

    /**
     * Guardo la ruta de la imagen tomada (URI) en caso de cerrar la Activity (o rotar la pantalla)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_IMAGE_STORAGE_PATH, imageStoragePath);
    }
    /**
     * Restaura la ruta de la imagen tomada (URI)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
         imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
    }

    /**
     * Método que avisa a los dos adaptadores
     */
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
            ToastCustomizado.tostada(Anadir.this, R.string.minimo_nombre);
            error = true;
        }
        //guardar
        if (error == false) {
            if (imageStoragePath == null) {
                imageStoragePath = "NO";
            }

            editImagePath = imageStoragePath;

            bdInterna = new BDInterna(this);

            //Última ID de la tabla Usuarios (para generar un nuevo contacto)
            int last_id = bdInterna.ultimo_id("CONTACTOS");


            //EDITO un contacto con sus ID's
            //editContacto es la copia que hago al mandarme un "EDIT" el Bundle
            if (editContacto != null) {
                imageStoragePath = editContacto.getFoto();
                if (editImagePath != null) {
                    imageStoragePath = editImagePath;
                }

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
                //Se genera un nuevo usuario (ID's nuevas)
                bdInterna.insertarContacto(
                        imageStoragePath,
                        tv_nombre.getText().toString(),
                        tv_apellido.getText().toString(),
                        last_id,
                        last_id,
                        last_id,
                        tv_email.getText().toString(),
                        bdInterna.getUniqueID()
                );
                for (int i = 0; i < adapterdomi.getItemCount(); i++) {
                    bdInterna.insertarDomicilio(last_id, DMAdapter.mDatasetDOM.get(i));
                }
                for (int i = 0; i < adaptertelf.getItemCount(); i++) {
                    bdInterna.insertarTelefono(last_id, TFAdapter.mDatasetTEL.get(i));
                }
            }

            imageStoragePath = null;

            // Si estaba editando un contacto, cierro.
            // Si estaba añadiendo uno, abro nueva instancia y cierro la actual
            if (editContacto != null) {
                finish();
            } else {
                startActivity(getIntent());
                finish();
            }

        }
    }

    /**
     * Método que se ejecuta al tomar una foto
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Recarga la galeria
            CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);
            // Visualiza la foto
            previewCapturedImage();
        } else if (resultCode == RESULT_CANCELED) {
            // Foto cancelada, muestro un Toast
            ToastCustomizado.tostada(Anadir.this, R.string.foto_cancelada);
        } else {
            // Fallo al tomar la foto, muestro un Toast
            ToastCustomizado.tostada(Anadir.this, R.string.errorfoto);
        }
    }

    /**
     * Muestra la foto capturada en mi Layout. El método CameraUtils.optimizeBitmap reduce la foto
     * para no dar problemas con la memoria OutOfMemory exceptions
     */
    private void previewCapturedImage() {
        try {
            fotoperfil.setVisibility(View.VISIBLE);
            bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
            fotoperfil.setImageBitmap(CameraUtils.redondearEsquinas(bitmap,50));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra un dialog con los requisitos a activar en caso de no tener acceso a la cámara
     */
    private void dialogPermisos() {
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


