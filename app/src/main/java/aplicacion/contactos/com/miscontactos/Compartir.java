package aplicacion.contactos.com.miscontactos;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;


public class Compartir extends AppCompatActivity {
    static final ArrayList<GaleriaCompartir> ESTADO_ACTIVITY=new ArrayList<>();

    Spinner spinner;
    ArrayList uuidSpinner = new ArrayList();
    String UUID;
    ArrayList<Contacto> contactos;

    BDInterna bdInterna;
    BDExterna bdExterna;
    private ArrayList<GaleriaCompartir> galeriaCompartir;
    private RecyclerView co;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public Compartir() {
        galeriaCompartir = new ArrayList<GaleriaCompartir>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartir);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bdInterna = new BDInterna(this);
        bdExterna = new BDExterna();

        bdInterna.actualizaContactos("NOMBRE", "ASC");
        contactos = bdInterna.contactos;
        UUID = bdInterna.getUniqueID();
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            galeriaCompartir = savedInstanceState.getParcelableArrayList(String.valueOf(ESTADO_ACTIVITY));
        }

        actualizar();

        for (int i = 0; i < contactos.size(); i++) {
            uuidSpinner.add(contactos.get(i).getNombre());
        }
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, uuidSpinner));
    }

    private void actualizar() {
        co = (RecyclerView) findViewById(R.id.recyfotos);
        co.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        co.setLayoutManager(llm);
        COAdapter adapter = new COAdapter(galeriaCompartir);
        co.setAdapter(adapter);
    }

    public void clickPulsar(View v) {

        // muestra un dialogo con aceptar o cancelar
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle(R.string.importante);
        dialogo1.setMessage(R.string.compartir_foto);
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogo1, int id) {

                // todo aqui para compartir

                bdInterna.getUniqueID();

            }
        });
        dialogo1.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                // en el caso de cancelar (no hago nada)
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



}




