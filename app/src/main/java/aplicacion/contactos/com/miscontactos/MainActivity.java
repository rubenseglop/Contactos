package aplicacion.contactos.com.miscontactos;

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

    private String orderby, ordertype;
    private RVAdapter adapter;
    private TextView tv_orden;

    private long inicio_tiempo;  // Usado en el método sobrescrito onBackPressed...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establecer la política de permisos necesarios para leer JSON (mas adelante usado para la BDExterna)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,Anadir.class)));

        /*Menu lateral Navigation View*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Instancio la clase BDInterna y BDExterna
        bdInterna = new BDInterna(this);
        bdExterna = new BDExterna(this);

        orderby ="Nombre";
        tv_orden = findViewById(R.id.ordenadopor);

        ordertype="ASC";
        actualizar();
    }

    /**
     * Menú Navigation Drawler
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
     * Método actualizar que lee de la BDInterna y actualiza los POJOS de contactos, galerías
     * domicilios y teléfonos
     */
    private void actualizar() {
        //Me traigo los contactos de BD (en objetos) //es mi POJO personalizado
        bdInterna.actualizaContactos();
        contactos = bdInterna.devuelveContactos();

        RecyclerView rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        adapter = new RVAdapter(contactos,this);
        rv.setAdapter(adapter);
        tv_orden.setText(R.string.by + orderby);

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
        int id = item.getItemId();

        if (id == R.id.action_compartir) {
           IrCompartir();
        }
        if (id == R.id.exportar) {
            ExportarWebService();
        }
        if (id == R.id.importar) {
            RestaurarWebService();
        }

        if (id == R.id.configuracion) {
            if (BDExterna.hayconexion(this)) {
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                return true;

            } else {
                ToastCustomizado.tostada(this, R.string.errorconex);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Abre el layout de compartir
     */
    private void IrCompartir() {
        if (BDExterna.hayconexion(this)) {
            if (bdInterna.hayUUID()) {
                if (BDExterna.hayservidor(BDExternaLinks.SERVIDOR)) {
                    Intent i = new Intent(this, Compartir.class);
                    startActivity(i);
                }
            } else ToastCustomizado.tostada(this, R.string.entrar_config);
        } else ToastCustomizado.tostada(this, R.string.no_conexion);
    }

    /**
     * Método que lee la base de datos externa y la clona en la interna SQLITE
     */
    private void RestaurarWebService() {

        if (BDExterna.hayconexion(this)) {
            if (bdInterna.hayUUID()) {
                if (BDExterna.hayservidor(BDExternaLinks.SERVIDOR)) {

                    // muestra un dialogo con aceptar o cancelar
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle(R.string.importante);
                    dialogo1.setMessage(R.string.mensaje_restaurar);
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton(R.string.confirmar, (dialogo112, id) -> {
                        // en el caso de aceptar el dialog
                        bdInterna.borrarTodo();
                        String UUID = BDInterna.getUniqueID();
                        WebSerTabla("CON", BDExternaLinks.vercontactos + UUID);
                        WebSerTabla("DOM", BDExternaLinks.verdomicilio + UUID);
                        WebSerTabla("TEL", BDExternaLinks.vertelefono + UUID);
                    });
                    dialogo1.setNegativeButton(R.string.cancel, (dialogo11, id) -> {
                        // en el caso de cancelar (no hago nada)
                    });
                    dialogo1.show();// fin muestra dialog aceptar o cancelar}


                }
            } else {
                ToastCustomizado.tostada(this, R.string.entrar_config);
            }

        } else {
            ToastCustomizado.tostada(this, R.string.no_conexion);
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
            url = new URL(sUrl);
            URLConnection request = null;
            request = url.openConnection();
            request.connect();
            // Convierte el contenido de la URL en un String
            JsonElement root = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));

            StringToBaseDatosInterna(tabla, root.toString()); // convierto esa String en un ArrayList de esa Tabla
        } catch (MalformedURLException e) {
            ToastCustomizado.tostada(MainActivity.this, R.string.errorserver);
        } catch (IOException e) {
            ToastCustomizado.tostada(MainActivity.this, R.string.errorconex);
        }
    }

    /**
     * Método que convierte un String en formato JSON. Posteriormente es insertado en la BDInterna
     * @param tabla Es la tabla que va a ser leida (la arrastro del método WebSerTabla)
     * @param jsonString Es el String que le paso de WebSerTabla con el String JSON a convertir
     */
    private void StringToBaseDatosInterna(String tabla, String jsonString) {

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
                            json_data.getString("EMAIL")
                    );
                    //System.out.println("DEBUG FOTO" + json_data.getString("FOTO"));
                }
            } catch (JSONException e) {
                ToastCustomizado.tostada(MainActivity.this, R.string.error_metodo);
                error_conexion = true;
            }

        } else if (!error_conexion && tabla == "GAL") {
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
                ToastCustomizado.tostada(MainActivity.this, R.string.error_metodo);
                error_conexion = true;
            }
        } else if (!error_conexion && tabla == "DOM") {
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
                ToastCustomizado.tostada(MainActivity.this, R.string.error_metodo);
                error_conexion = true;
            }
        } else if (!error_conexion && tabla == "TEL") {
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
                ToastCustomizado.tostada(MainActivity.this, R.string.error_metodo);
                error_conexion = true;
            }
        }

        if (!error_conexion) {
            ToastCustomizado.tostada(MainActivity.this, R.string.contactos_import);
            if (tabla == "TEL") actualizar();
        } else {
            ToastCustomizado.tostada(MainActivity.this, R.string.errorconex);
        }
    }

    /**
     * Método para clonar la base de datos interna SQLITE a la base de datos externa
     */
    private void ExportarWebService() {

        boolean conexion = true;

        // Comprueba si hay conexión
        if (BDExterna.hayconexion(this)) {
            // Comprueba si el terminal está identificado
            if (bdInterna.hayUUID()) {
                // Comprueba si hay ping externo
                if (BDExterna.hayservidor(BDExternaLinks.SERVIDOR)) {

                    // Comprueba si existen contactos... en caso de no haberlo, elimino la base de datos externa
                    if (contactos.size()==0){

                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                        dialogo1.setTitle(R.string.importante);
                        dialogo1.setMessage(R.string.mensaje_borrado);
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton(R.string.confirmar, (dialogo112, id) -> {
                            // en el caso de aceptar el dialog

                            bdExterna.borrartodo(BDInterna.getUniqueID());
                        });
                        dialogo1.setNegativeButton(R.string.cancel, (dialogo11, id) -> {
                            // en el caso de cancelar (no hago nada)
                        });
                        dialogo1.show();// fin muestra dialog aceptar o cancelar}

                    }

                    actualizaContactos();
                }
            } else {
                ToastCustomizado.tostada(MainActivity.this, R.string.entrar_config);
                conexion = false;
            }

        } else {
            ToastCustomizado.tostada(MainActivity.this, R.string.no_conexion);
            conexion = false;
        }
    }

    /**
     * Método que recorre los objetos instanciados de contactos y los guarda en la base de datos externa
     */
    private void actualizaContactos() {
        boolean conexion;
        for (Contacto contacto : contactos) {
            String varId = Integer.toString(contacto.getId()),
                    varFoto = contacto.getFoto(),
                    varNombre = contacto.getNombre(),
                    varApellidos = contacto.getApellidos(),
                    varCorreo = contacto.getCorreo(),
                    varUUID = BDInterna.getUniqueID();
            int varGaleria = contacto.getGaleria_id(),
                    varDireccion = contacto.getDireccion_id(),
                    varTelefono = contacto.getTelefono_id();
            ArrayList<Galeria> galerias = contacto.getGalerias();
            ArrayList<Domicilio> domicilios = contacto.getDomicilios();
            ArrayList<Telefono> telefonos = contacto.getTelefonos();

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
                ToastCustomizado.tostada(MainActivity.this, R.string.error_contacto);
                conexion = false;
            }

            if (conexion) {
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
                        ToastCustomizado.tostada(MainActivity.this, R.string.error_domicilio);
                        conexion = false;
                    }
                }
            }

            if (conexion) {
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
                        ToastCustomizado.tostada(MainActivity.this, R.string.error_telefono);
                        conexion = false;
                    }
                }
            }
        }
        if (contactos.size() != 0) {
            ToastCustomizado.tostada(MainActivity.this, R.string.contactos_export);
        }
    }

    private final ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        private Drawable icon;
        private ColorDrawable background=null;

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            ToastCustomizado.tostada(MainActivity.this, R.string.moveswype);
            return false;
        }

        /**
         * Método onSwiped de la clase ItemTouchHelper para añadirle opciones para el borrado de mis contactos
         * @param viewHolder
         * @param swipeDir
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

            /*
              Diálogo que muestra si deseamos o no borrar a un contacto
             */
            new AlertDialog.Builder(viewHolder.itemView.getContext())
                    .setMessage(R.string.borrar_contacto)
                    .setPositiveButton(R.string.boton_si, (dialog, which) -> {

                        /* Al tener la lista desordenada, utilizo un HashMap con (Posicion, ID)
                         * para descubrir la ID de la posición eliminada con Swipe
                         */
                        HashMap deId_Posicion;
                        deId_Posicion = RVAdapter.deId_Posicion;
                        int id = (int) deId_Posicion.get(viewHolder.getAdapterPosition());
                        bdInterna.borraContacto(id);
                        ToastCustomizado.tostada(MainActivity.this, R.string.delswype);
                        actualizar();
                    })
                    .setNegativeButton(R.string.boton_no, (dialog, id) -> adapter.notifyItemChanged(viewHolder.getAdapterPosition()))

                    /*Bug que presentaba si no escogía ninguna opción (pulsando fuera del dialog)
                    con .setCancelable impide que se pueda presionar en otra parte de la pantalla*/
                    .setCancelable(false)
                    .create().show();
        }

        /**
         * Método onChildDraw de la clase ItemTouchHelper que nos dibuja un cuadro de color rojo
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
                    escribe_texto(getString(R.string.borrar), c, rightButton, paint);
                }else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        /**
         * Método que escribe texto en una caja
         * @param text
         * @param c
         * @param button
         * @param p
         */
        private void escribe_texto(String text, Canvas c, RectF button, Paint p) {
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
    private void ordenarAlfabeticamente() {
        switch (orderby) {
            case "Nombre":
                Collections.sort(contactos, new CompararNombre());
                adapter.notifyDataSetChanged();
                break;
            case "Apellidos":
                Collections.sort(contactos, new CompararApellido());
                adapter.notifyDataSetChanged();
                break;
            case "Domicilio":
                Collections.sort(contactos, new CompararDomicilio());
                adapter.notifyDataSetChanged();
                break;
            default:
                Collections.sort(contactos, new CompararTelefono());
                adapter.notifyDataSetChanged();
                break;
        }
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

    /**
     * Método que se acciona al presionar el boton atrás
     */
    @Override
    public void onBackPressed(){

        final int duracion = 2000; //2 seg
        if (inicio_tiempo + duracion > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else {
            ToastCustomizado.tostada(MainActivity.this, R.string.salir);
        }
        inicio_tiempo = System.currentTimeMillis();
    }

}

