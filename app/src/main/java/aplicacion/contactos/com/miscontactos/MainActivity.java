package aplicacion.contactos.com.miscontactos;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
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
    BDExterna bdexterna;

    ArrayList<Contacto> contactos;
    ArrayList<Galeria> galerias;
    ArrayList<Domicilio> domicilios;
    ArrayList<Telefono> telefonos;
    boolean error_conexion = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establecer la politica de permisos necesarios para leer JSON (mas adelante usado para la BDExterna)
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

        //Instancio la clase BDInterna y BDExterna para crear una BD  en caso de no tenerla y tener los métodos para manejarla
        bdinterna = new BDInterna(this);
        bdexterna = new BDExterna();
        bdinterna.insertarUUID(); //Busca si tengo una UUID (en caso de no tenerla genero uno aleatoriamente
        actualizar();
    }
    public String getURLForResource (int resourceId) {
        return Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +resourceId).toString();
    }

    /**
     * Método actualizar que lee de la BDInterna y actualiza los POJOS de contactos, galerias
     * domicilios y telefonos
     */
    private void actualizar() {
        //Me traigo los contactos de BD (en objetos) //es mi POJO personalizado
        bdinterna.actualizaContactos();
        contactos=bdinterna.contactos;
        galerias=bdinterna.galerias;

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        RVAdapter adapter = new RVAdapter(contactos,galerias);
        rv.setAdapter(adapter);

        //esto es parte del Swype
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds StringDomicilio to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_compartir) {
            Intent i = new Intent(this, Compartir.class);
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

        // muestra un dialogo con aceptar o cancelar
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle(R.string.importante);
        dialogo1.setMessage(R.string.mensaje_restaurar);
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogo1, int id) {
                error_conexion = false;
                // en el caso de aceptar el dialog

                if (bdexterna.leerUrl(BDExternaLinks.conexion)!=null) { //comprobar conexion
                    bdinterna.borrarTodo();
                    String UUID = bdinterna.getUniqueID();
                    WebSerTabla("CON", BDExternaLinks.vercontactos + UUID);
                    WebSerTabla("GAL", BDExternaLinks.vergaleria + UUID);
                    WebSerTabla("DOM", BDExternaLinks.verdomicilio + UUID);
                    WebSerTabla("TEL", BDExternaLinks.vertelefono + UUID);
                } else {
                    Toast.makeText(MainActivity.this, R.string.cancelconex, Toast.LENGTH_LONG).show();

                }
            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                // en el caso de cancelar (no hago nada)
            }
        });
        dialogo1.show();// fin muestra dialog aceptar o cancelar
    }

    /**
     * Método que lee el contenido web (interpretado por el PHP a un JSON) y posteriormente llamo al método StringObjeto con dicho contenido web
     * @param tabla Tabla que va a ser leida
     * @param sUrl Direccion URL en la que está ubicada el .php
     */
    private void WebSerTabla(String tabla, String sUrl) {
        // Connect to the URL using java's native library
        URL url = null;
        try {
            String sURL = sUrl;
            url = new URL(sURL);
            URLConnection request = null;
            request = url.openConnection();
            request.connect();
            // Convierte el contenido de la URL en un String
            JsonElement root = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));
            StringObjeto(tabla, root.toString()); // convierto esa String en un ArrayList de esa Tabla
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, R.string.errorserver, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, R.string.errorconex, Toast.LENGTH_LONG).show();
        }
    }

    private void exportarWebService() {
        error_conexion = false;
        String error;
        error_conexion = bdexterna.borrartodo(bdinterna.getUniqueID()).equals("Error");
        if(error_conexion) {
            error_conexion = true;
        }
        if (!error_conexion) {
            for (Contacto contacto: contactos) {
                String varId = Integer.toString(contacto.getId());
                String varFoto = contacto.getFoto();
                String varNombre =  contacto.getNombre();
                String varApellidos = contacto.getApellidos();
                int varGaleria = contacto.getGaleria_id();
                int varDireccion = contacto.getDireccion_id();
                int varTelefono = contacto.getTelefono_id();
                String varCorreo = contacto.getCorreo();
                String varUUID = bdinterna.getUniqueID();
                domicilios = contacto.getDomicilios();
                telefonos = contacto.getTelefonos();

                if (varId==null || varId.length()==0) { varId =""; }
                if (varFoto==null || varFoto.length()==0) { varFoto =""; }
                if (varNombre==null || varNombre.length()==0) { varNombre =""; }
                if (varApellidos==null || varApellidos.length()==0) { varApellidos =""; }
                if (varCorreo==null || varCorreo.length()==0) { varCorreo =""; }

                error = bdexterna.insertarContacto(varId,varFoto,varNombre,varApellidos,
                        varGaleria,varDireccion,varTelefono,varCorreo,varUUID);

                if (error.equals("ERROR") || error.isEmpty()){error_conexion = true; }

                if (error_conexion == false) {
                    for (Domicilio domicilio : domicilios) {

                        int varIdDOM = domicilio.getId();
                        String varDireccionDOM = domicilio.getDireccion();
                        if (varDireccionDOM==null || varDireccionDOM.length()==0) { varDireccionDOM=""; }

                        error = bdexterna.insertarDomicilio(varIdDOM,varDireccionDOM,varUUID);
                        if (error.equals("ERROR") || error.isEmpty()) {
                            error_conexion = true;
                        }
                    }
                }

                if (error_conexion == false) {
                    for (Telefono telefono : telefonos) {
                        int varIdTel = telefono.getId();
                        String varNumeroTel = telefono.getNumero();

                        if (varNumeroTel==null || varNumeroTel.length()==0) { varNumeroTel =""; }

                        error = bdexterna.insertarTelefono(varIdTel, varNumeroTel, varUUID);
                        if (error.equals("ERROR") || error.isEmpty()) {
                            error_conexion = true;
                        }
                    }
                }
            }
        }
        if (error_conexion == false) {
            /*for (Galeria galeria : galerias) {
                int varId = galeria.getId();
                String varUrl = galeria.getURL();
                String varUUID = bdInterna.getUniqueID();

                error = luissancar.insertarGaleria(varId, varUrl, varUUID);
                if (error == "Error") {
                    error_conexion = true;
                }
            }*/
        }



        if (error_conexion == false) {
            Toast.makeText(MainActivity.this, R.string.contactos_export, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, R.string.errorconex, Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Método que convierte un String en formato JSON a valores en la tabla que especifico. Posteriormente es insertado en la BDInterna
     * @param tabla Es la tabla que va a ser leida (la arrastro del método WebSerTabla)
     * @param jsonString Es el String que le paso de WebSerTabla con el String JSON a convertir
     */
    public void StringObjeto(String tabla, String jsonString) {

        if (tabla == "CON") {
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
                            json_data.getString("GALERIAID"),
                            json_data.getString("DOMICILIOID"),
                            json_data.getString("TELEFONOID"),
                            json_data.getString("EMAIL"),
                            json_data.getString("UUIDUNIQUE")
                    );
                    System.out.println("DEBUG FOTO" + json_data.getString("FOTO"));
                }
            } catch (Exception e) {
                Toast.makeText(this, R.string.error_metodo, Toast.LENGTH_SHORT).show();
                error_conexion = true;
            }

        } else if (error_conexion == false && tabla == "GAL") {
            try {
                JSONArray jArray = new JSONArray(jsonString);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    // add to list
                    bdinterna.insertarGaleria(
                            json_data.getInt("ID"),
                            json_data.getString("URL")
                    );
                }
            } catch (Exception e) {
                Toast.makeText(this, R.string.error_metodo, Toast.LENGTH_SHORT).show();
                error_conexion = true;
            }
        } else if (error_conexion == false && tabla == "DOM") {
            try {
                JSONArray jArray = new JSONArray(jsonString);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    // add to list
                    bdinterna.insertarDomicilio(
                            json_data.getInt("ID"),
                            json_data.getString("DIRECCION")
                    );
                }
            } catch (Exception e) {
                Toast.makeText(this, R.string.error_metodo, Toast.LENGTH_SHORT).show();
                error_conexion = true;
            }
        } else if (error_conexion == false && tabla == "TEL") {
            try {
                JSONArray jArray = new JSONArray(jsonString);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    // add to list
                    bdinterna.insertarTelefono(
                            json_data.getInt("ID"),
                            json_data.getString("NUMERO")
                    );


                }
            } catch (Exception e) {
                Toast.makeText(this, R.string.error_metodo, Toast.LENGTH_SHORT).show();
                error_conexion = true;
            }
        }
        if (error_conexion==false){
            Toast.makeText(MainActivity.this, R.string.contactos_import, Toast.LENGTH_LONG).show();
            if (tabla=="TEL")actualizar();
        } else {
            Toast.makeText(MainActivity.this, R.string.errorconex, Toast.LENGTH_LONG).show();
        }
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
            Toast.makeText(this, R.string.error_io, Toast.LENGTH_SHORT).show();
        }
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Toast.makeText(MainActivity.this, R.string.moveswype, Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            Toast.makeText(MainActivity.this, R.string.delswype, Toast.LENGTH_SHORT).show();
            //Remove swiped item from list and notify the RecyclerView
            //int position = viewHolder.getAdapterPosition();

            int position = bdinterna.devuelvoIDborrado(viewHolder.getAdapterPosition());

            bdinterna.borraContacto(position);
            actualizar();

        }
    };
}
