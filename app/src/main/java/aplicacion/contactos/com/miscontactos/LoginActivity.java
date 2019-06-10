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
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    public static final int MEDIA_TYPE_IMAGE = 1;
    private String imageStoragePath;
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";
    public static final int BITMAP_SAMPLE_SIZE = 8;

    TextView tv_nombreUsuario,tv_emailUsuario;
    ImageView fotoUsuario;
    Button bt_fotoUsuario,bt_aceptaConfig, bt_cuentaOlvidada;
    MetodoFTP myftp;

    BDInterna bdInterna;
    BDExterna bdExterna;

    private Bitmap bitmap;
    private String imageTempo;

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
        bt_cuentaOlvidada = (Button) findViewById(R.id.bt_cuentaOlvidada);
        imageStoragePath= null;

        // Botón de Aceptar
        bt_aceptaConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_nombreUsuario.length() != 0) {
                    if (tv_emailUsuario.length() != 0) {

                        if (esEmail(String.valueOf(tv_emailUsuario.getText()))) {
                            if (!bdInterna.hayUUID()) {  // intenta generar un UUID
                                bdInterna.crearUUID();
                            }
                            String url = "";
                            try {
                                if (!imageStoragePath.equals("NO")) {
                                    url = BDExternaLinks.URLFTP + "perfil/" + new File(imageStoragePath).getName();
                                } else url = "NO";

                            } catch (NullPointerException e) {
                                url = "NO";
                            }

                            if (BDExterna.hayconexion(LoginActivity.this)) {
                                if (BDExterna.hayservidor(BDExternaLinks.SERVIDOR)) {

                                    try {
                                        bdExterna.borrarUsuario(BDInterna.getUniqueID());

                                    } catch (NullPointerException e) {
                                        // No esta siendo editado, no lo borro
                                    } finally {
                                        bdExterna.insertarUsuario(
                                                tv_nombreUsuario.getText(),
                                                tv_emailUsuario.getText(),
                                                url,
                                                fotoUsuario,
                                                BDInterna.getUniqueID(),
                                                LoginActivity.this
                                        );
                                    }

                                    try {
                                        imageStoragePath.length();
                                        if (!imageStoragePath.equals("NO")) {
                                            myftp.uploadFile(new File(imageStoragePath), "perfil");
                                        }
                                    } catch (NullPointerException e) {
                                        // va sin imagen
                                    }
                                    ToastCustomizado.tostada(LoginActivity.this, R.string.guardada);
                                    imageStoragePath = null;
                                    finish();
                                }
                            } else
                                ToastCustomizado.tostada(LoginActivity.this, R.string.no_conexion);

                        } else {
                            ToastCustomizado.tostada(LoginActivity.this, R.string.noesemail);
                        }
                    } else {
                        ToastCustomizado.tostada(LoginActivity.this, R.string.introducir_email);
                    }
                } else {
                    ToastCustomizado.tostada(LoginActivity.this, R.string.introducir_nombre);
                }
            }

        });

        // Botón de Foto
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

        // Botón de Recuperar
        bt_cuentaOlvidada.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RecuperarCuenta.class));
            }
        });
    }

    /**
     * Método de capturar foto, y el link URI de la foto la envío mediante Bundle
     */
    private void captureImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
            imageTempo = imageStoragePath;
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
            ToastCustomizado.tostada(LoginActivity.this, R.string.foto_cancelada);

        } else {
            // Fallo al tomar la foto, muestro un Toast
            ToastCustomizado.tostada(LoginActivity.this, R.string.errorfoto);

        }
    }

    /**
     * Muestra la foto en el layout
     */
    private void previewCapturedImage() {
        try {
            fotoUsuario.setVisibility(View.VISIBLE);
            bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);

            fotoUsuario.setImageBitmap(CameraUtils.redondearEsquinas(bitmap, 50));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que comprueba si el text tiene el patrón de un email.
     * @param email
     * @return
     */
    private boolean esEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    @Override
    public void onResume() {
        boolean intent_edit=false;

        try {
            intent_edit = bdInterna.hayUUID();
        } catch (NullPointerException e) {
            intent_edit = true;
        }

        if (intent_edit==true) {

            bt_cuentaOlvidada.setEnabled(false);
            bt_cuentaOlvidada.setVisibility(View.GONE);
            ArrayList<UsuariosGaleria> usuarios = bdExterna.devuelveUsuarios(LoginActivity.this);

            actualiza_Perfil(usuarios);
        }
        super.onResume();
    }

    /**
     * Método que actualiza el perfil en pantalla
     * @param usuarios
     */
    private void actualiza_Perfil(ArrayList<UsuariosGaleria> usuarios) {
        for (int i = 0; i < usuarios.size(); i++) {
            bdInterna.hayUUID();
            if (usuarios.get(i).getUUID().equals(BDInterna.getUniqueID())) {
                tv_nombreUsuario.setText(usuarios.get(i).getNombre());
                tv_emailUsuario.setText(usuarios.get(i).getEmail());
                if (!usuarios.get(i).getPath().equals("NO")) {
                    imageStoragePath = usuarios.get(i).getPath();
                    try {
                        if (imageTempo != null) {
                            imageStoragePath = imageTempo;
                            imageTempo = null;
                        }
                    } catch (NullPointerException e) {
                    }
                    Glide.with(LoginActivity.this)
                            .load(imageStoragePath)
                            .into(fotoUsuario);
                }
            }
        }
    }
}
