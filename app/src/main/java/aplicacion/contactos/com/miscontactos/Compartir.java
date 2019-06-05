package aplicacion.contactos.com.miscontactos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;

public class Compartir extends AppCompatActivity {

    private Spinner spinner; //Declaro un Spinner
    private RecyclerView co; //Declaro un RecyclerView
    private ArrayList idSpinner = new ArrayList(); //Copia del ArrayList anterior, pero almacenando las ids
    private String selectedIdSpinner; //Contendrá la id del Spinner seleccionado en el adaptador

    private ArrayList<Contacto> contactos; //Declaro un ArrayList de Contactos
    private ArrayList<GaleriaCompartir> galeriaCompartir; //Declaro un ArrayList de GaleriaCompartir
    private ArrayList<UsuariosGaleria> usuarios;

    private BDInterna bdInterna; //Declaro un objeto de tupo BDInterna para usar los métodos SQLITE interna
    private BDExterna bdExterna;

    private COAdapter adapter;
    private Button bt_anadirGaleria;
    private ImageButton bt_verCompartidos;
    private ImageButton bt_share;

    private MetodoFTP myftp = new MetodoFTP(this);

    public Compartir() {
        if (galeriaCompartir == null) {
            galeriaCompartir = new ArrayList<GaleriaCompartir>();
            selectedIdSpinner = "";
        }
    }

    /**
     * Método de la clase Activity que se ejecuta al iniciar una actividad (por un Intent)
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compartir);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bdInterna = new BDInterna(this);
        bdExterna = new BDExterna(this);
        bdInterna.actualizaContactos("NOMBRE", "ASC");
        bdInterna.hayUUID();
        contactos = bdInterna.contactos;

        bt_anadirGaleria = (Button) findViewById(R.id.bt_anadirGaleria);
        bt_verCompartidos = (ImageButton) findViewById(R.id.bt_vercompartido);
        /*bt_share = (ImageButton) findViewById(R.id.bt_share);*/

        galeriaCompartir.clear();

        // Carga un ArrayList para el Spinner de usuarios cogidos de la BDExterna (los toma a todos excepto al del móvil propietario)
        usuarios = BDExterna.devuelveUsuarios(this);
        ArrayList<SpinnerContactosData> spinnerContactosData = new ArrayList<>();
        for (int i = 0; i < usuarios.size(); i++) {

            idSpinner.add(usuarios.get(i).getUUID());
            spinnerContactosData.add(i, new SpinnerContactosData(usuarios.get(i).getNombre(), usuarios.get(i).getEmail(), usuarios.get(i).getPath()));

        }
        //Si hay menos de dos usuarios (no es posible compartir nada)
        if (spinnerContactosData.size()<2) {
            bt_anadirGaleria.setEnabled(false);
            bt_verCompartidos.setEnabled(false);
            /*bt_share.setEnabled(false);*/
        } else {
            bt_anadirGaleria.setEnabled(true);
            bt_verCompartidos.setEnabled(true);
            /*bt_share.setEnabled(true);*/
        }

        //Elimino el usuario del movil

        for (int i = 0; i < usuarios.size(); i++) {
            if (BDInterna.getUniqueID().equals(usuarios.get(i).getUUID())) {
                idSpinner.remove(i);
                spinnerContactosData.remove(i);
            }
        }

        spinner = findViewById(R.id.spinner);
        SpinnerAdapter adapterSpinner = new SpinnerAdapter(this, R.layout.spinner_layout, R.id.spinner_nombre, spinnerContactosData);
        spinner.setAdapter(adapterSpinner);

        actualizarSpinner();


        bt_anadirGaleria.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (BDExterna.hayconexion(Compartir.this)) {
                    if (BDExterna.hayservidor(BDExternaLinks.SERVIDOR)) {
                        // https://github.com/myinnos/AwesomeImagePicker
                        Intent intent = new Intent(Compartir.this, AlbumSelectActivity.class);
                        intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 15); // set limit for image selection
                        startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
                    } else ToastCustomizado.tostada(Compartir.this, R.string.problema_servidor);
                } else ToastCustomizado.tostada(Compartir.this, R.string.errorconex);
            }
        });

        bt_verCompartidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BDExterna.hayconexion(Compartir.this)) {
                    if (BDExterna.hayservidor(BDExternaLinks.SERVIDOR)) {
                        Intent intent = new Intent(Compartir.this, verCompartidos.class);
                        startActivity(intent);
                    } else ToastCustomizado.tostada(Compartir.this, R.string.problema_servidor);
                } else ToastCustomizado.tostada(Compartir.this, R.string.errorconex);
            }
        });

        /*
        YA NO ME SIRVE PUESTO QUE LEO URL HTTP EN LUGAR DE DIRECCIONES URI

        bt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Uri> arrayUri = new ArrayList<>();

                for (int i = 0; i < galeriaCompartir.size(); i++) {
                    Uri uri = Uri.fromFile(new File(galeriaCompartir.get(i).getPathFoto()));
                    // start play with image uri
                    System.out.println("DEBUG URI " + uri.getPath());
                    arrayUri.add(uri);
                }
                Intent intentShareFile = new Intent(Intent.ACTION_SEND_MULTIPLE);
                intentShareFile.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayUri);
                intentShareFile.setType("text/plain");
                startActivity(intentShareFile);
                actualizarGaleriadeSQL();
            }
        });*/

    }

    private void actualizarSpinner() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                selectedIdSpinner = idSpinner.get(pos).toString();
                actualizarGaleriadeSQL();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void actualizarGaleriadeSQL() {
        // Actualiza la galeriaCompartir con el contenido de la SQLite

        galeriaCompartir.clear();

        galeriaCompartir = BDExterna.devuelveGaleria(this, selectedIdSpinner);

        actualizarAdapter();
    }

    /**
     * Método de la clase Activity que se ejecuta al finalizar / rotar
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        actualizarGaleriadeSQL();
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        actualizarGaleriadeSQL();
        super.onRestoreInstanceState(outState);
    }

    /**
     * Método que actualiza del RecyclerView
     */
    private void actualizarAdapter() {
        co = findViewById(R.id.recyfotos);
        co.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        co.setLayoutManager(llm);
        adapter = new COAdapter(galeriaCompartir, this);
        co.setAdapter(adapter);

        //esto es parte del Swype
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(co);
    }

    /**
     * Al regresar de la activity de la cámara, recojo las fotos seleccionadas y las añado a galeriaCompartir
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantsCustomGallery.REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            //The array list has the image paths of the selected imagenes
            ArrayList<Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);

            ArrayList<Uri> arrayUri = new ArrayList<>();

            //Recorro todas las fotos seleccionadas
            for (int i = 0; i < images.size(); i++) {
                Uri uri = Uri.fromFile(new File(images.get(i).path));

                //Busco si ya la tenemos repetida
                Boolean repetido = false;
                for (int j = 0; j < galeriaCompartir.size(); j++) {
                    if (images.get(i).path.equals(galeriaCompartir.get(j).getPathFoto())) {
                        repetido = true;
                    }
                }
                if (repetido == false) {
                    arrayUri.add(uri);
                    // Guardo la informacion IDUSUARIO - PATH - USUARIOALQUECOMPARTO
                    // https://miscontactosrsl.webcindario.com/uploads/ee158af8-7798-4463-b48e-3dcc6224c22f/IMG-20190529-WA0003.jpg

                    // sacar la carpeta y nombrefichero

                    String url = BDExternaLinks.URLFTP + BDInterna.getUniqueID() + "/" + new File(uri.getPath()).getName();

                    bdExterna.insertarGaleria(BDInterna.getUniqueID(),url,selectedIdSpinner);

                    myftp.uploadFile(new File(uri.getPath()),BDInterna.getUniqueID());
                }
            }
        }
        actualizarGaleriadeSQL();
    }


    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        private Drawable icon;
        private ColorDrawable background = null;

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            ToastCustomizado.tostada(Compartir.this, R.string.movegale);
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

            HashMap selected = COAdapter.selected;
            GaleriaCompartir sel = (GaleriaCompartir) selected.get(viewHolder.getAdapterPosition());
            bdExterna.borraGaleria(sel.getId(),sel.getPathFoto(),sel.getUuid());
            myftp.deleteFile(new File(sel.getPathFoto()), sel.getId());
            ToastCustomizado.tostada(Compartir.this, R.string.swypefoto);
            actualizarGaleriadeSQL();
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {

            try {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                float buttonWidthWithoutPadding = 300 - 20;
                float corners = 16;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);

                    RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    paint.setColor(Color.RED);
                    c.drawRoundRect(rightButton, corners, corners, paint);
                    drawText(getString(R.string.borrar), c, rightButton, paint);
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void drawText(String text, Canvas c, RectF button, Paint p) {
            float textSize = 40;
            p.setColor(Color.WHITE);
            p.setAntiAlias(true);
            p.setTextSize(textSize);

            float textWidth = p.measureText(text);
            c.drawText(text, button.centerX() - (textWidth / 2), button.centerY() + (textSize / 2), p);
        }
    };
}




