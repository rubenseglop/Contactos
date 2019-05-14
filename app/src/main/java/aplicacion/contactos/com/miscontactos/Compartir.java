package aplicacion.contactos.com.miscontactos;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

    Spinner spinner;
    ArrayList uuidSpinner = new ArrayList();

    String UUID;
    ArrayList<Contacto> contactos;

    BDInterna bdInterna;
    BDExterna bdExterna;

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

        bdInterna.actualizaContactos("NOMBRE","ASC");
        UUID = bdInterna.getUniqueID();

        //saco todos los UUID
        URL url = null;
        try {
            String sURL = BDExternaLinks.verUUID;
            url = new URL(sURL);
            URLConnection request = null;
            request = url.openConnection();
            request.connect();
            // Convierte el contenido de la URL en un String
            JsonElement root = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));
            StringObjeto(root.toString()); // convierto esa String en un ArrayList de esa Tabla
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(Compartir.this, R.string.errorserver, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(Compartir.this, R.string.errorconex, Toast.LENGTH_LONG).show();
        }

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, uuidSpinner));
    }

    public void StringObjeto(String jsonString) {

        try {
            JSONArray jArray = new JSONArray(jsonString);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                // add to list
                json_data.getString("UUIDUNIQUE");
                if (!json_data.getString("UUIDUNIQUE").equals(UUID)) {
                    uuidSpinner.add(json_data.getString("UUIDUNIQUE"));
                }
            }
        } catch (Exception e) {
            System.out.println("DEBUG catch " + e.getMessage());
            Toast.makeText(Compartir.this, R.string.errorconex, Toast.LENGTH_LONG).show();
        }
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

            }


//                Uri u = Uri.parse(uri.getPath()); File f = new File("" + u); f.getName();
//
//                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//                Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(),bmOptions);
//                String imagen = getStringImagen(bitmap);
//                bdExterna.insertarFoto2(bdInterna.getUniqueID(),imagen,f.getName());


            Intent intentShareFile = new Intent(Intent.ACTION_SEND_MULTIPLE);


            //if you need
            //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
            //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");


            intentShareFile.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayUri);
            intentShareFile.setType("text/plain");
            startActivity(intentShareFile);


        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}


