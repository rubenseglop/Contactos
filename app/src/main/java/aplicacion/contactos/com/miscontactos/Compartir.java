package aplicacion.contactos.com.miscontactos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class Compartir extends AppCompatActivity {

    Spinner spinner;
    ArrayList uuidSpinner = new ArrayList();

    String UUID;
    ArrayList<Contacto> contactos;

    BDInterna bdinterna;
    luissancar luissancar;

    Button bt_aceptar;



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
        bdinterna = new BDInterna(this);

        bdinterna.actualizaContactos();
        UUID = bdinterna.getUniqueID();

        bt_aceptar = bt_aceptar = (Button)findViewById(R.id.action_compartir);

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
            Toast.makeText(Compartir.this, "Hubo un problema con el servidor", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(Compartir.this, "Hubo un problema con la conexión", Toast.LENGTH_LONG).show();
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
                if (!json_data.getString("UUIDUNIQUE").equals(UUID)) {uuidSpinner.add(json_data.getString("UUIDUNIQUE"));}
            }
        } catch (Exception e) {
            System.out.println("DEBUG catch " + e.getMessage());
            Toast.makeText(Compartir.this, "Hubo un problema con la conexión", Toast.LENGTH_LONG).show();
        }


    }
    public void clickPulsar(View v) {
        System.out.println("DEBUG HAS DADO ACEPTAR");

    }
}


