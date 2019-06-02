package aplicacion.contactos.com.miscontactos;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BDInterna bdInterna;
    private BDExterna bdExterna;
    private ArrayList<Contacto> contactos;
    private ArrayList<Galeria> galerias;
    private ArrayList<Domicilio> domicilios;
    private ArrayList<Telefono> telefonos;

    private String orderby;
    private String ordertype;
    private RVAdapter adapter;
    private RecyclerView rv;
    private TextView tv_orden;

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
                startActivity(new Intent(MainActivity.this,Anadir.class));
            }
        });

        /*Menu lateral Navigation View*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Instancio la clase BDInterna y BDExterna para crear una BD  en caso de no tenerla y tener los métodos para manejarla
        bdInterna = new BDInterna(this);
        bdExterna = new BDExterna(this);

        orderby ="Nombre";
        tv_orden = findViewById(R.id.ordenadopor);

        ordertype="ASC";
        actualizar();
    }

    /**
     * Menu Navigation Drawler
     * @param item
     * @return
     */

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_nombre) {
            orderby = "Nombre";
        } else if (id == R.id.nav_apellido) {
            orderby = "Apellidos";
        } else if (id == R.id.nav_domicilio) {
            orderby = "Domicilio";
        } else if (id == R.id.nav_telefono) {
            orderby = "Telefono";
        }

        actualizar();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Método actualizar que lee de la BDInterna y actualiza los POJOS de contactos, galerias
     * domicilios y telefonos
     */
    private void actualizar() {
        //Me traigo los contactos de BD (en objetos) //es mi POJO personalizado
        bdInterna.actualizaContactos(orderby, ordertype);
        contactos = bdInterna.devuelveContactos();

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        adapter = new RVAdapter(contactos,this);
        rv.setAdapter(adapter);
        tv_orden.setText("por " + orderby);

        //esto es parte del Swype
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);

        ordenarAlfabeticamente();
    }

    /**
     * Crea un menú en la parte superior derecha con el Layout de menu_main
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds StringDomicilio to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Opciones del menú superior derecha
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_compartir) {
            Intent i = new Intent(this, Compartir.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.exportar) {
            ExportarWebService();
        }
        if (id == R.id.importar) {
            RestaurarWebService();
        }

        if (id == R.id.configuracion) {
            if (BDExterna.hayconexion(this)){
                if (bdInterna.hayUUID()) {

                    // Si tiene identificador, ya fue creado previamente, por lo tanto edito
                    Intent i = new Intent(this, LoginActivity.class);
                    i.putExtra("EDIT", true);
                    startActivity(i);
                    return true;
                } else {

                    // No tiene un identificador, por lo tanto es nuevo.
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                    return true;
                }
            } else {
                Toast.makeText(this, "No hay conexion a internet", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Método que lee la base de datos externa y la clona en la interna SQLITE
     */
    private void RestaurarWebService() {

        if (BDExterna.hayconexion(this)) {
            if (bdInterna.hayUUID() == true) {
                if (BDExterna.hayservidor(BDExternaLinks.SERVIDOR)) {

                    // muestra un dialogo con aceptar o cancelar
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle(R.string.importante);
                    dialogo1.setMessage(R.string.mensaje_restaurar);
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialogo1, int id) {

                            // en el caso de aceptar el dialog
                            if (bdExterna.leerUrl(BDExternaLinks.conexion) != null) { //comprobar conexion
                                bdInterna.borrarTodo();

                                String UUID = bdInterna.getUniqueID();

                                if (UUID.length() != 0) {
                                    WebSerTabla("CON", BDExternaLinks.vercontactos + UUID);
                                    WebSerTabla("DOM", BDExternaLinks.verdomicilio + UUID);
                                    WebSerTabla("TEL", BDExternaLinks.vertelefono + UUID);
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.error_configurar_perfil, Toast.LENGTH_SHORT).show();
                                }

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
                    dialogo1.show();// fin muestra dialog aceptar o cancelar}


                }
            } else {
                Toast.makeText(this, "Debes entrar a CONFIGURACION", Toast.LENGTH_SHORT).show();

            }

        } else {
            Toast.makeText(this, "No hay conexion", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Método que lee el contenido web (interpretado por el PHP a un JSON) y posteriormente llamo al método StringToBaseDatosInterna con dicho contenido web
     * @param tabla Tabla que va a ser leida
     * @param sUrl Direccion URL en la que está ubicada el .php
     */
    private void WebSerTabla(String tabla, String sUrl) {
        URL url = null;
        try {
            String sURL = sUrl;
            url = new URL(sURL);
            URLConnection request = null;
            request = url.openConnection();
            request.connect();
            // Convierte el contenido de la URL en un String
            JsonElement root = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));

            StringToBaseDatosInterna(tabla, root.toString()); // convierto esa String en un ArrayList de esa Tabla
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, R.string.errorserver, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, R.string.errorconex, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Método que convierte un String en formato JSON. Posteriormente es insertado en la BDInterna
     * @param tabla Es la tabla que va a ser leida (la arrastro del método WebSerTabla)
     * @param jsonString Es el String que le paso de WebSerTabla con el String JSON a convertir
     */
    public void StringToBaseDatosInterna(String tabla, String jsonString) {

        boolean error_conexion=false;
        if (tabla == "CON") {
            try {
                JSONArray jArray = new JSONArray(jsonString);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    // add to list
                    bdInterna.insertarContacto(
                            json_data.getInt("ID"),
                            json_data.getString("FOTO"),
                            json_data.getString("NOMBRE"),
                            json_data.getString("APELLIDOS"),
                            json_data.getInt("GALERIAID"),
                            json_data.getInt("DOMICILIOID"),
                            json_data.getInt("TELEFONOID"),
                            json_data.getString("EMAIL"),
                            json_data.getString("UUIDUNIQUE")
                    );
                    //System.out.println("DEBUG FOTO" + json_data.getString("FOTO"));
                }
            } catch (JSONException e) {
                Toast.makeText(this, R.string.error_metodo, Toast.LENGTH_SHORT).show();
                error_conexion = true;
            }

        } else if (error_conexion == false && tabla == "GAL") {
            try {
                JSONArray jArray = new JSONArray(jsonString);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    // add to list
                    bdInterna.insertarGaleria(
                            json_data.getInt("ID"),
                            json_data.getString("PATH")
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
                    bdInterna.insertarDomicilio(
                            json_data.getInt("ID"),
                            json_data.getString("DIRECCION")
                    );
                }
            } catch (JSONException e) {
                Toast.makeText(this, R.string.error_metodo, Toast.LENGTH_SHORT).show();
                error_conexion = true;
            }
        } else if (error_conexion == false && tabla == "TEL") {
            try {
                JSONArray jArray = new JSONArray(jsonString);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    // add to list
                    bdInterna.insertarTelefono(
                            json_data.getInt("ID"),
                            json_data.getString("NUMERO")
                    );
                }
            } catch (JSONException e) {
                Toast.makeText(this, R.string.error_metodo, Toast.LENGTH_SHORT).show();
                error_conexion = true;
            }
        }

        if (error_conexion == false) {
            Toast.makeText(MainActivity.this, R.string.contactos_import, Toast.LENGTH_LONG).show();
            if (tabla == "TEL") actualizar();
        } else {
            Toast.makeText(MainActivity.this, R.string.errorconex, Toast.LENGTH_LONG).show();
        }
    }

    /*private boolean hasLogin() {

        if (BDExterna.compruebaConexion(this)){
            if (bdInterna.hayUUID()) {
                return true;
            } else {
                Toast.makeText(this, "Error, servidor base de datos caido. Inténtelo más adelante", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(this, "No tienes conexión internet", Toast.LENGTH_SHORT).show();
            return false;
        }
    }*/

    private void ExportarWebService() {

        boolean conexion = true;

        if (BDExterna.hayconexion(this)) {
            if (bdInterna.hayUUID() == true) {
                if (BDExterna.hayservidor(BDExternaLinks.SERVIDOR)) {
                    bdExterna.borrarUsuario(BDInterna.getUniqueID());

                    for (Contacto contacto : contactos) {
                        String varId = Integer.toString(contacto.getId()),
                                varFoto = contacto.getFoto(),
                                varNombre = contacto.getNombre(),
                                varApellidos = contacto.getApellidos(),
                                varCorreo = contacto.getCorreo(),
                                varUUID = bdInterna.getUniqueID();
                        int varGaleria = contacto.getGaleria_id(),
                                varDireccion = contacto.getDireccion_id(),
                                varTelefono = contacto.getTelefono_id();
                        galerias = contacto.getGalerias();
                        domicilios = contacto.getDomicilios();
                        telefonos = contacto.getTelefonos();

                        if (varId == null || varId.length() == 0) {
                            varId = "";
                        }
                        if (varFoto == null || varFoto.length() == 0) {
                            varFoto = "";
                        }
                        if (varNombre == null || varNombre.length() == 0) {
                            varNombre = "";
                        }
                        if (varApellidos == null || varApellidos.length() == 0) {
                            varApellidos = "";
                        }
                        if (varCorreo == null || varCorreo.length() == 0) {
                            varCorreo = "";
                        }

                        String error = bdExterna.insertarContacto(varId, varFoto, varNombre, varApellidos,
                                varGaleria, varDireccion, varTelefono, varCorreo, varUUID);

                        String drbug = error;
                        if (error.equals("OK")) {
                            conexion = true;
                        } else {
                            Toast.makeText(this, "Se produjo un error al introducir un contacto", Toast.LENGTH_SHORT).show();
                            conexion = false;
                        }

                        if (conexion == true) {
                            for (Domicilio domicilio : domicilios) {

                                int varIdDOM = domicilio.getId();
                                String varDireccionDOM = domicilio.getDireccion();
                                if (varDireccionDOM == null || varDireccionDOM.length() == 0) {
                                    varDireccionDOM = "";
                                }

                                error = bdExterna.insertarDomicilio(varIdDOM, varDireccionDOM, varUUID);
                                if (error.equals("OK")) {
                                    conexion = true;
                                } else {
                                    Toast.makeText(this, "Se produjo un error al introducir un domicilio", Toast.LENGTH_SHORT).show();
                                    conexion = false;
                                }
                            }
                        }

                        if (conexion == true) {
                            for (Telefono telefono : telefonos) {
                                int varIdTel = telefono.getId();
                                String varNumeroTel = telefono.getNumero();

                                if (varNumeroTel == null || varNumeroTel.length() == 0) {
                                    varNumeroTel = "";
                                }

                                error = bdExterna.insertarTelefono(varIdTel, varNumeroTel, varUUID);
                                if (error.equals("OK")) {
                                    conexion = true;
                                } else {
                                    Toast.makeText(this, "Se produjo un error al introducir un telefono", Toast.LENGTH_SHORT).show();
                                    conexion = false;
                                }
                            }
                        }
                    }
                    if (contactos.size() != 0) {
                        Toast.makeText(this, "Contactos exportados", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No habia contactos que exportar. Se inicializó tu copia de seguridad externa", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Debes entrar a CONFIGURACION", Toast.LENGTH_SHORT).show();
                conexion = false;
            }

        } else {
            Toast.makeText(this, "No hay conexion", Toast.LENGTH_SHORT).show();
            conexion = false;
        }


    }


    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        private Drawable icon;
        private ColorDrawable background=null;

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Toast.makeText(MainActivity.this, R.string.moveswype, Toast.LENGTH_SHORT).show();
            return false;
        }

        /**
         * Método onSwiped de la clase ItemTouchHelper para añadirle opciones para el borrado de mis contactos
         * @param viewHolder
         * @param swipeDir
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

            /**
             * Diálogo que muestra si deseamos o no borrar a un contacto
             */
            new AlertDialog.Builder(viewHolder.itemView.getContext())
                    .setMessage(R.string.borrarcontacto)
                    .setPositiveButton(R.string.boton_si, (dialog, which) -> {

                        /* Al tener la lista desordenada, utilizo un HashMap con (Posicion, ID)
                         * para descubrir la ID de la posición eliminada con Swipe
                         */
                        HashMap deId_Posicion;
                        deId_Posicion = RVAdapter.deId_Posicion;
                        int id = (int) deId_Posicion.get(viewHolder.getAdapterPosition());
                        bdInterna.borraContacto(id);
                        Toast.makeText(MainActivity.this, R.string.delswype, Toast.LENGTH_SHORT).show();
                        actualizar();
                    })
                    .setNegativeButton(R.string.boton_no, (dialog, id) -> adapter.notifyItemChanged(viewHolder.getAdapterPosition()))

                    /*Bug que presentaba si no escogía ninguna opción (pulsando fuera del dialog)
                    con .setCancelable impide que se pueda presionar en otra parte de la pantalla*/
                    .setCancelable(false)
                    .create().show();
        }

        /**
         * Método onChildDraw de la clase ItemTouchHelper por el cual se dibuja por debajo del elemento onMove
         * @param c
         * @param recyclerView
         * @param viewHolder
         * @param dX
         * @param dY
         * @param actionState
         * @param isCurrentlyActive
         */

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {

            try {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                float buttonWidthWithoutPadding = 280; // esto es lo que ocupa aprox. el texto "BORRAR"
                float corners = 16;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    View itemView = viewHolder.itemView;
                    Paint paint = new Paint();
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

    /**
     * Método que ordena alfabeticamente por el contenido de "orderby"
     */
    public void ordenarAlfabeticamente() {
        if (orderby.equals("Nombre")){Collections.sort(contactos, new CompararNombre());adapter.notifyDataSetChanged();}
        else if (orderby.equals("Apellidos")){Collections.sort(contactos, new CompararApellido());adapter.notifyDataSetChanged();}
        else if (orderby.equals("Domicilio")){Collections.sort(contactos, new CompararDomicilio());adapter.notifyDataSetChanged();}
        else {Collections.sort(contactos, new CompararTelefono());adapter.notifyDataSetChanged();}
    }

    @Override
    protected void onPause() {
        actualizar();
        super.onPause();
    }

    @Override
    protected void onResume() {
        actualizar();
        super.onResume();
    }
}

