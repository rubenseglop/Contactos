package aplicacion.contactos.com.miscontactos;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.gson.Gson;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Instancio la clase BDInterna para crear la BD y tener los métodos para manejarla
        BDInterna bdinterna = new BDInterna(this);
//        bdinterna.insertarContacto("ruben","segura","jardines","5454545", "a@b.c"); //para insertar
//        bdinterna.insertarContacto("antonio","gutierrez","arena","6767676", "b@e.d");

        //Me traigo los contactos de BD (en objetos) //es mi POJO personalizado
        ArrayList<Contacto> contactos = bdinterna.devuelveContactos();

        // TODO Meter un reciclerView con el contenido de contactos
        /*recyclerView = (RecyclerView) findViewById(R.id.reciclerviewtoguapo);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new AdaptadorRecicler(contactos);
        recyclerView.setAdapter(mAdapter);*/

        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        RVAdapter adapter = new RVAdapter(contactos);
        rv.setAdapter(adapter);









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
        if (id == R.id.action_settings) {
            Intent i = new Intent(this,anadir.class);
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
        String response="tuuuuuuuuu";

        return response;
    }
}
