package aplicacion.contactos.com.miscontactos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    BDInterna bdinterna;
    luissancar luissancar;

    ArrayList<Contacto> contactos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //LUIS EL PT.AMO
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        //Instancio la clase BDInterna para crear la BD y tener los métodos para manejarla
        bdinterna = new BDInterna(this);
        //bdinterna.insertarContacto("es mi url de foto","ruben","segura","jardines","5454545", "a@b.c"); //para insertar
        // bdinterna.insertarContacto("antonio","gutierrez","arena","6767676", "b@e.d");



        actualizar();
        //CopiarArchivo("drawable/perfil.png","/storage/emulated/0/Pictures/Hello Camera/perfil.png");
    }

    private void actualizar() {
        //Me traigo los contactos de BD (en objetos) //es mi POJO personalizado
        bdinterna.actualizaContactos();
        contactos=bdinterna.contactos;

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        RVAdapter adapter = new RVAdapter(contactos);
        rv.setAdapter(adapter);

        //esto es parte del Swype
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_anadir) {
            Intent i = new Intent(this, Anadir.class);
            //i.putExtra("contactos", contactos);
            startActivity(i);
            return true;

        }
        if (id == R.id.exportar) {

            luissancar = new luissancar();

            luissancar.borrartodo();

            for (Contacto contacto: contactos) {
                String varId = Integer.toString(contacto.getId());
                String varFoto = '"' + contacto.getFoto() + '"';
                String varNombre =  contacto.getNombre();
                String varApellidos = contacto.getApellidos();
                String varDireccion = contacto.getDireccion();
                String varTelefono = contacto.getTelefono();
                String varCorreo = contacto.getCorreo();
                if (varId==null || varId.length()==0) { varId =""; }
                if (varFoto==null || varFoto.length()==0) { varFoto =""; }
                if (varNombre==null || varNombre.length()==0) { varNombre =""; }
                if (varApellidos==null || varApellidos.length()==0) { varApellidos =""; }
                if (varDireccion==null || varDireccion.length()==0) { varDireccion =""; }
                if (varTelefono==null || varTelefono.length()==0) { varTelefono =""; }
                if (varCorreo==null || varCorreo.length()==0) { varCorreo =""; }

                luissancar.insertar(varId,varFoto,varNombre,varApellidos,varDireccion,varTelefono,varCorreo);
            }

            Toast.makeText(MainActivity.this, "Contactos exportados", Toast.LENGTH_LONG).show();


        }
        return super.onOptionsItemSelected(item);
    }


    public void URLJsonObjeto() {
        Log.d("RESULTADO", "URL TO JSON 3");


        Gson gson = new Gson();
        Log.d("RESULTADO", "val gson");

    }

    private void CopiarArchivo(String sourceFile, String destinationFile) {

        try{

            File inFile = new File(sourceFile);
            File outFile = new File(destinationFile);

            if (!outFile.exists()){
                FileInputStream in = new FileInputStream(inFile);
                FileOutputStream out =new FileOutputStream(outFile);

                byte[] buffer = new byte[1024];
                int c;

                while( (c = in.read(buffer) ) != -1)
                    out.write(buffer, 0, c);

                out.flush();
                in.close();
                out.close();
            }

        } catch(IOException e) {
            Log.e("DEBUG", "Hubo un error de entrada/salida!!!");
        }
    }

    private String leerUrl(String urlString) {
        String response = "tuuuuuuuuu";

        return response;
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Toast.makeText(MainActivity.this, "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            Toast.makeText(MainActivity.this, "on Swiped ", Toast.LENGTH_SHORT).show();
            //Remove swiped item from list and notify the RecyclerView
            //int position = viewHolder.getAdapterPosition();

            int position = bdinterna.devuelvoIDborrado(viewHolder.getAdapterPosition());

            bdinterna.borraContacto(position);
            actualizar();

        }
    };
}

