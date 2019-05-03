package aplicacion.contactos.com.miscontactos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


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
                startActivity(new Intent(MainActivity.this,Anadir.class));
            }
        });

        //Instancio la clase BDInterna para crear la BD y tener los métodos para manejarla
        bdinterna = new BDInterna(this);
        luissancar = new luissancar();
        //bdinterna.insertarContacto("es mi url de foto","ruben","segura","jardines","5454545", "a@b.c"); //para insertar
        //bdinterna.insertarContacto("antonio","gutierrez","arena","6767676", "b@e.d");


        bdinterna.insertarUUID();
        actualizar();

    }

    private void actualizar() {
        //Me traigo los contactos de BD (en objetos) //es mi POJO personalizado
        bdinterna.actualizaContactos();
        contactos=bdinterna.contactos;
        //bdinterna.insertarUUID();

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

            exportarWebService();


        }
        if (id == R.id.importar) {

            importarWebService();

        }
        return super.onOptionsItemSelected(item);
    }

    private void importarWebService() {

        String sURL = "http://iesayala.ddns.net/BDSegura/misContactos/vercontactos.php/?UUIDUNIQUE=" + bdinterna.getUniqueID();

        // Connect to the URL using java's native library
        URL url = null;
        try {
            url = new URL(sURL);
            URLConnection request = null;
            request = url.openConnection();
            request.connect();

            // Convierte el contenido de la URL en un String
            JsonElement root = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));

            StringObjeto(root.toString()); // convierto esa String en un ArrayList de Contactos

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Hubo un problema con el servidor", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Hubo un problema con la conexión", Toast.LENGTH_LONG).show();
        }
    }

    private void exportarWebService() {

        if(luissancar.borrartodo(bdinterna.getUniqueID())=="Error") {
            Toast.makeText(MainActivity.this, "Hubo un error en la conexión", Toast.LENGTH_LONG).show();
        } else {
            for (Contacto contacto: contactos) {
                String varId = Integer.toString(contacto.getId());
                String varFoto = contacto.getFoto();
                String varNombre =  contacto.getNombre();
                String varApellidos = contacto.getApellidos();
                String varDireccion = contacto.getDireccion();
                String varTelefono = contacto.getTelefono();
                String varCorreo = contacto.getCorreo();
                String varUUID = bdinterna.getUniqueID();

                if (varId==null || varId.length()==0) { varId =""; }
                if (varFoto==null || varFoto.length()==0) { varFoto =""; }
                if (varNombre==null || varNombre.length()==0) { varNombre =""; }
                if (varApellidos==null || varApellidos.length()==0) { varApellidos =""; }
                if (varDireccion==null || varDireccion.length()==0) { varDireccion =""; }
                if (varTelefono==null || varTelefono.length()==0) { varTelefono =""; }
                if (varCorreo==null || varCorreo.length()==0) { varCorreo =""; }

                luissancar.insertar(varId,varFoto,varNombre,varApellidos,varDireccion,varTelefono,varCorreo,varUUID);
                Toast.makeText(MainActivity.this, "Contactos exportados", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void StringObjeto(String jsonString) {

        // muestra un dialogo con aceptar o cancelar
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿ Vas a importar todos los contactos almacenados en el servidor ?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

                // en el caso de aceptar
                try {
                    JSONArray jArray = new JSONArray(jsonString);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        // add to list
                        bdinterna.insertarContacto(
                                json_data.getString("ID"),
                                json_data.getString("FOTO"),
                                json_data.getString("NOMBRE"),
                                json_data.getString("APELLIDOS"),
                                json_data.getString("DOMICILIO"),
                                json_data.getString("TELEFONO"),
                                json_data.getString("EMAIL"),
                                json_data.getString("UUIDUNIQUE")
                        );
                        System.out.println("DEBUG " + json_data.getString("FOTO"));
                        Toast.makeText(MainActivity.this, "Contactos importados", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    System.out.println("DEBUG catch "+ e.getMessage());
                    Toast.makeText(MainActivity.this, "Hubo un problema con la conexión", Toast.LENGTH_LONG).show();
                }
                actualizar();



            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                // en el caso de cancelar (no hago nada)
            }
        });
        dialogo1.show();
        // fin muestra dialogo aceptar o cancelar


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
