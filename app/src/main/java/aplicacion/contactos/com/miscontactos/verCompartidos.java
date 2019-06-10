package aplicacion.contactos.com.miscontactos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class verCompartidos extends AppCompatActivity {

    private ArrayList<GaleriaCompartir> galeriaCompartir; //Declaro un ArrayList de GaleriaCompartir
    private ArrayList<UsuariosGaleria> usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_compartidos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

galeriaCompartir=new ArrayList<>();
        actualizarGaleriadeSQL();
    }

    /**
     * Actualiza los usuarios de la base de datos externa
     */
    private void actualizarGaleriadeSQL() {
        galeriaCompartir.clear();
        galeriaCompartir = BDExterna.devuelveGaleriaCompleta(this);
        usuarios = BDExterna.devuelveUsuarios(this);

        actualizarAdapter();
    }

    /**
     * Actualiza el adaptador del Recyclerview
     */
    private void actualizarAdapter() {
        //Declaro un RecyclerView
        RecyclerView verCompartidos = findViewById(R.id.recyclerVerCompartidos);
        verCompartidos.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        verCompartidos.setLayoutManager(llm);
        VERAdapter adapter = new VERAdapter(galeriaCompartir, usuarios, this);
        verCompartidos.setAdapter(adapter);
    }

}
