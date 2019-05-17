package aplicacion.contactos.com.miscontactos;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.sql.SQLOutput;
import java.util.ArrayList;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;

public class Compartir extends AppCompatActivity {

    private final ArrayList<GaleriaCompartir> ESTADO_ACTIVITY = new ArrayList<>(); // ArrayList (será una copia del contenido GaleriaCompartir) para recuperar en caso de rotación pantalla (Bundle)
    private Button compartirfoto; //Declaro un Button
    private Spinner spinner; //Declaro un Spinner
    private RecyclerView co; //Declaro un RecyclerView

    private ArrayList usuarioSpinner = new ArrayList(); //Creo un ArrayList de usuarios para pasarselo al Adaptador del Spinner
    private ArrayList idSpinner = new ArrayList(); //Copia del ArrayList anterior, pero almacenando las ids
    private String selectedIdSpinner; //Contendrá la id del Spinner seleccionado en el adaptador

    private ArrayList<Contacto> contactos; //Declaro un ArrayList de Contactos
    private ArrayList<GaleriaCompartir> galeriaCompartir; //Declaro un ArrayList de GaleriaCompartir
    private BDInterna bdInterna; //Declaro un objeto de tupo BDInterna para usar los métodos SQLITE interna

    /**
     * Método de la clase Activity que se ejecuta al finalizar / rotar
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Constructor de la clase Compartir
     */
    public Compartir() {
        galeriaCompartir = new ArrayList<GaleriaCompartir>();
    }

    /**
     * Método de la clase Activity que se ejecuta al iniciar una actividad (por un Intent)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartir);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        compartirfoto = (Button)findViewById(R.id.aceptarCompartir);
        compartirfoto.setEnabled(false); // deshabilito el boton de compartir

        bdInterna = new BDInterna(this);
        bdInterna.actualizaContactos("NOMBRE", "ASC");
        contactos = bdInterna.contactos;

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            galeriaCompartir = savedInstanceState.getParcelableArrayList(String.valueOf(ESTADO_ACTIVITY));
        }

        //actualizar();

        for (int i = 0; i < contactos.size(); i++) {
            usuarioSpinner.add(contactos.get(i).getNombre());
            idSpinner.add(contactos.get(i).getId());
        }
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, usuarioSpinner));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedIdSpinner = idSpinner.get(pos).toString();
                galeriaCompartir.clear();

                Cursor c = bdInterna.busquedaGaleria(selectedIdSpinner);
                while (c.moveToNext()){
                    galeriaCompartir.add(new GaleriaCompartir(c.getString(1)));
                }
                compartirfoto.setEnabled(false);
                actualizar();
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

                for (int i = 0; i < galeriaCompartir.size() ; i++) {
                    bdInterna.insertarGaleria(Integer.parseInt(selectedIdSpinner),galeriaCompartir.get(i).getPathFoto());
                }
                Toast.makeText(Compartir.this, "Fotos compartidas", Toast.LENGTH_SHORT).show();
                compartirfoto.setEnabled(false);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantsCustomGallery.REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);

            ArrayList<Uri> arrayUri = new ArrayList<>();

            for (int i = 0; i < images.size(); i++) {
                Uri uri = Uri.fromFile(new File(images.get(i).path));
                // start play with image uri
                System.out.println("DEBUG URI " + uri.getPath());
                arrayUri.add(uri);
                galeriaCompartir.add(new GaleriaCompartir(uri.getPath()));
                compartirfoto.setEnabled(true);
            }
            actualizar();

            //if you need
            //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
            //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");


            /*
            Intent intentShareFile = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intentShareFile.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayUri);
            intentShareFile.setType("text/plain");
            startActivity(intentShareFile);*/

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(String.valueOf(ESTADO_ACTIVITY), galeriaCompartir);
        super.onSaveInstanceState(outState);
        // Always call the superclass so it can save the view hierarchy state

        actualizar();
    }


    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        private Drawable icon;
        private ColorDrawable background=null;

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Toast.makeText(Compartir.this, R.string.moveswype, Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

            Toast.makeText(Compartir.this, R.string.delswype, Toast.LENGTH_SHORT).show();
            //Remove swiped item from list and notify the RecyclerView
            //int position = viewHolder.getAdapterPosition();

            //todo aqui
            int position = bdInterna.devuelvoIDborrado(viewHolder.getAdapterPosition());
            actualizar();
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {

            try {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                float buttonWidthWithoutPadding = 300 - 20;
                float corners = 16;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);


                    RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    paint.setColor(Color.RED);
                    c.drawRoundRect(rightButton, corners, corners, paint);
                    drawText(getString(R.string.borrado), c, rightButton, paint);
                }else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        private void drawText(String text, Canvas c, RectF button, Paint p) {
            float textSize = 40;
            p.setColor(Color.WHITE);
            p.setAntiAlias(true);
            p.setTextSize(textSize);

            float textWidth = p.measureText(text);
            c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
        }
    };
}




