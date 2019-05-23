package aplicacion.contactos.com.miscontactos;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;

public class Compartir extends AppCompatActivity {

    private ArrayList<GaleriaCompartir> ESTADO_ACTIVITY = new ArrayList<>(); // ArrayList (será una copia del contenido GaleriaCompartir) para recuperar en caso de rotación pantalla (Bundle)
    private Button bt_compartirAceptar; //Declaro un Button
    private Spinner spinner; //Declaro un Spinner
    private RecyclerView co; //Declaro un RecyclerView

    private ArrayList idSpinner = new ArrayList(); //Copia del ArrayList anterior, pero almacenando las ids
    private String selectedIdSpinner; //Contendrá la id del Spinner seleccionado en el adaptador

    private ArrayList<Contacto> contactos; //Declaro un ArrayList de Contactos
    private ArrayList<Galeria> galerias; //Declaro un ArrayList de Contactos
    private ArrayList<GaleriaCompartir> galeriaCompartir; //Declaro un ArrayList de GaleriaCompartir

    private BDInterna bdInterna; //Declaro un objeto de tupo BDInterna para usar los métodos SQLITE interna

    public Compartir() {
        galeriaCompartir = new ArrayList<GaleriaCompartir>();
        selectedIdSpinner=new String();
    }

    /**
     * Método de la clase Activity que se ejecuta al finalizar / rotar
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
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

        bt_compartirAceptar = (Button) findViewById(R.id.aceptarCompartir);
        bt_compartirAceptar.setEnabled(false); // deshabilito el boton de compartir

        bdInterna = new BDInterna(this);
        bdInterna.actualizaContactos("NOMBRE", "ASC");
        contactos = bdInterna.contactos;

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            galeriaCompartir = savedInstanceState.getParcelableArrayList(String.valueOf(ESTADO_ACTIVITY));
            //System.out.println("DEBUG COMPARTIR SAVEDI" +galeriaCompartir.size() );
        } else {
           galeriaCompartir.clear();
        }
        ArrayList<SpinnerContactosData> spinnerContactosData = new ArrayList<>();
        for (int i = 0; i < contactos.size(); i++) {
            idSpinner.add(contactos.get(i).getId());
            spinnerContactosData.add(i, new SpinnerContactosData(contactos.get(i).getNombre(), contactos.get(i).getFoto()));
        }
        spinner = (Spinner) findViewById(R.id.spinner);
        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.spinner_layout, R.id.txt, spinnerContactosData);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                galeriaCompartir.clear();
                selectedIdSpinner = idSpinner.get(pos).toString();

                Cursor c = bdInterna.busquedaGaleria(selectedIdSpinner);
                while (c.moveToNext()) {
                    galeriaCompartir.add(new GaleriaCompartir(c.getString(1)));
                }
                bt_compartirAceptar.setEnabled(false);
                actualizar(); // todo arreglar por aqui
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        actualizar();
    }

    /**
     * Método que actualiza el RecyclerView
     */
    private void actualizar() {
        co = (RecyclerView) findViewById(R.id.recyfotos);
        co.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        co.setLayoutManager(llm);
        COAdapter adapter = new COAdapter(galeriaCompartir);
        co.setAdapter(adapter);

        //esto es parte del Swype
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(co);
    }

    /**
     * Método que muestra un Dialog y en caso de aceptar nos guarda galeriaCompartir en nuestra BDInterna
     *
     * @param v
     */
    public void clickPulsar(View v) {

        // Muestra un dialogo con aceptar o cancelar
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle(R.string.importante);
        dialogo1.setMessage(R.string.compartir_foto);
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {

            /**
             * Listener onClick que se ejecuta al pulsar sobre el boton 'Confirmar' del dialogo
             * @param dialogo1
             * @param id
             */
            public void onClick(DialogInterface dialogo1, int id) {

                for (int i = 0; i < galeriaCompartir.size(); i++) {
                    bdInterna.insertarGaleria(Integer.parseInt(selectedIdSpinner), galeriaCompartir.get(i).getPathFoto());
                }
                Toast.makeText(Compartir.this, "Fotos compartidas", Toast.LENGTH_SHORT).show();
                bt_compartirAceptar.setEnabled(false);
            }
        });
        dialogo1.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                Toast.makeText(Compartir.this, "Proceso cancelado", Toast.LENGTH_SHORT).show();
            }
        });
        dialogo1.show();
        // fin muestra dialogo aceptar o cancelar
    }

    public void clickGaleria(View v) {
        // https://github.com/myinnos/AwesomeImagePicker
        Intent intent = new Intent(this, AlbumSelectActivity.class);
        intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 15); // set limit for image selection
        startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
    }

    public void clickCompartirAndroid(View v){

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
                    galeriaCompartir.add(new GaleriaCompartir(uri.getPath()));
                    bt_compartirAceptar.setEnabled(true);
                }
            }
            actualizar();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putParcelableArrayList(String.valueOf(ESTADO_ACTIVITY), galeriaCompartir);
        System.out.println("DEBUG DESTRUYO " + galeriaCompartir.size());
        super.onSaveInstanceState(outState);
        // Always call the superclass so it can save the view hierarchy state
        actualizar();
    }
    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        //galeriaCompartir = outState.getParcelableArrayList(String.valueOf(ESTADO_ACTIVITY));
        System.out.println("DEBUG RESTAURA " + galeriaCompartir.size()) ;
        super.onRestoreInstanceState(outState);
        // Always call the superclass so it can save the view hierarchy state
        actualizar();
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        private Drawable icon;
        private ColorDrawable background = null;

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Toast.makeText(Compartir.this, R.string.movegale, Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

            HashMap deIdGaleria_Path = COAdapter.deIdGaleria_Path;
            String path = (String) deIdGaleria_Path.get(viewHolder.getAdapterPosition());


            galeriaCompartir.remove(viewHolder.getAdapterPosition());
            bt_compartirAceptar.setEnabled(true);
            bdInterna.borraGaleria((String) deIdGaleria_Path.get(viewHolder.getAdapterPosition()));


            System.out.println("DEBUG PATH " + path + " tam: " + galeriaCompartir.size());
            Toast.makeText(Compartir.this, R.string.swypefoto, Toast.LENGTH_SHORT).show();
            actualizar();
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
                    drawText(getString(R.string.borrado), c, rightButton, paint);
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




