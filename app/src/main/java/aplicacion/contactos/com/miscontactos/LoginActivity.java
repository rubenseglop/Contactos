package aplicacion.contactos.com.miscontactos;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static String imageStoragePath;
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";
    public static final int BITMAP_SAMPLE_SIZE = 8;

    TextView tv_nombreUsuario,tv_emailUsuario;
    ImageView fotoUsuario;
    Button bt_fotoUsuario,bt_aceptaConfig;
    MetodoFTP myftp;

    BDInterna bdInterna;
    BDExterna bdExterna;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bdInterna = new BDInterna(this);
        bdExterna = new BDExterna(this);

        myftp = new MetodoFTP(this);

        tv_nombreUsuario = (TextView) findViewById(R.id.tv_nombreUsuario);
        tv_emailUsuario = (TextView) findViewById(R.id.tv_emailUsuario);
        fotoUsuario = (ImageView) findViewById(R.id.fotoUsuario);
        bt_fotoUsuario = (Button) findViewById(R.id.bt_fotoUsuario);
        bt_aceptaConfig = (Button) findViewById(R.id.bt_aceptaConfig);


        boolean intent_edit = false;
        try {
            intent_edit = (boolean) getIntent().getSerializableExtra("EDIT");
        } catch (NullPointerException e) {
            intent_edit = false;
        }

        if (intent_edit) {
            ArrayList<UsuariosGaleria> usuarios = bdExterna.devuelveUsuarios(LoginActivity.this);

            for (int i = 0; i < usuarios.size(); i++) {
                if (usuarios.get(i).getUUID().equals(BDInterna.getUniqueID())) {
                    tv_nombreUsuario.setText(usuarios.get(i).getNombre());
                    tv_emailUsuario.setText(usuarios.get(i).getEmail());
                    if (!usuarios.get(i).getPath().equals("NO")) {
                        Glide.with(LoginActivity.this)
                                .load(usuarios.get(i).getPath())
                                .into(fotoUsuario);
                    }
                }
            }
        }

        bt_aceptaConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_nombreUsuario.length() != 0) {
                    if (tv_emailUsuario.length() != 0) {
                        if (!bdInterna.hayUUID()) {  // intenta generar un UUID
                            bdInterna.crearUUID();
                        }
                        String url = "";
                        try {
                            imageStoragePath.length();
                            url = BDExternaLinks.URLFTP + "perfil/" + new File(imageStoragePath).getName();
                        } catch (NullPointerException e) {
                            url = "NO";
                        }
                        //TODO COMPRUEBA INTERNET
                        if (BDExterna.hayconexion(LoginActivity.this)) {
                            if (BDExterna.hayservidor(BDExternaLinks.SERVIDOR)) {

                                try {
                                    if ((boolean) getIntent().getSerializableExtra("EDIT")) {
                                        bdExterna.borrarUsuario(BDInterna.getUniqueID());
                                    }
                                } catch (NullPointerException e) {
                                    // No esta siendo editado, no lo borro
                                } finally {

                                    System.out.println("DEBUG INSERTA " + tv_nombreUsuario.getText() + url + BDInterna.getUniqueID());
                                    bdExterna.insertarUsuario(
                                            tv_nombreUsuario.getText(),
                                            tv_emailUsuario.getText(),
                                            url,
                                            fotoUsuario,
                                            BDInterna.getUniqueID(),
                                            LoginActivity.this
                                    );
                                }

                                //TODO verificar que tengo internet
                                try {
                                    imageStoragePath.length();
                                    if (!imageStoragePath.equals("NO")) {
                                        myftp.uploadFile(new File(imageStoragePath), "perfil");
                                        Toast.makeText(LoginActivity.this, "Guardada la configuracion", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (NullPointerException e) {
                                    // va sin imagen
                                }
                                finish();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "No hay conexion", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Debes introducir un email", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Debes introducir un nombre", Toast.LENGTH_SHORT).show();
                }
            }

        });

        bt_fotoUsuario.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CameraUtils.checkPermissions(getApplicationContext())) {
                    captureImage();
                } else {
                    requestCameraPermission(MEDIA_TYPE_IMAGE);
                }
            }
        });
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
     * Revisa los permisos de la cámara (en caso de no tener permiso
     * hacia la cámara, dexter nos preguntará si deseamos abrirlos)
     * @param type
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

                            // En el caso de que no estén, los preguntamos por un dialog
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
     * Muestra un dialog con los requisitos a activar en caso de no tener acceso a la cámara
     */
    private void dialogPermisos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permisos_camara)
                .setMessage(R.string.mensaje_error_camara)
                .setPositiveButton(R.string.ir_config, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(LoginActivity.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
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
            Toast.makeText(getApplicationContext(),
                    R.string.canceladoFoto, Toast.LENGTH_SHORT)
                    .show();
        } else {
            // Fallo al tomar la foto, muestro un Toast
            Toast.makeText(getApplicationContext(),
                    R.string.errorfoto, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Muestra la foto en el layout
     */
    private void previewCapturedImage() {
        try {
            fotoUsuario.setVisibility(View.VISIBLE);
            bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
            fotoUsuario.setImageBitmap(CameraUtils.redondearEsquinas(bitmap,50));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
