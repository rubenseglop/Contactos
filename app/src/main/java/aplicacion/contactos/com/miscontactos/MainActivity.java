package aplicacion.contactos.com.miscontactos;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    BDInterna bdinterna;
    ArrayList<Contacto> contactos;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        //bdinterna.insertarContacto("ruben","segura","jardines","5454545", "a@b.c"); //para insertar
        // bdinterna.insertarContacto("antonio","gutierrez","arena","6767676", "b@e.d");



        actualizar();
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
            Intent i = new Intent(this, anadir.class);
            //i.putExtra("contactos", contactos);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void URLJsonObjeto() {
        Log.d("RESULTADO", "URL TO JSON 3");


        Gson gson = new Gson();
        Log.d("RESULTADO", "val gson");

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

            bdinterna.borraContacto(position); //todo arreglar aqui la posicion
            actualizar();

        }
    };
}

