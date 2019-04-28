package aplicacion.contactos.com.miscontactos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{

    ArrayList<Contacto> contactos;
    RVAdapter(ArrayList<Contacto> contactos){ this.contactos = contactos;}
    Context context;

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView personPhoto;
        TextView tv_nombre;
        TextView tv_apellido;
        TextView tv_domicilio;
        TextView tv_telefono;
        TextView tv_email;


        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            personPhoto = (ImageView)itemView.findViewById(R.id.foto);
            tv_nombre = (TextView)itemView.findViewById(R.id.id_nombre);
            tv_apellido = (TextView)itemView.findViewById(R.id.id_apellido);
            tv_domicilio = (TextView)itemView.findViewById(R.id.id_domicilio);
            tv_telefono = (TextView)itemView.findViewById(R.id.id_telefono);
            tv_email = (TextView)itemView.findViewById(R.id.id_email);

        }
    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }
    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filas_usuarios, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {


        //TODO AQUI PETA AL MOSTRAR UN CONTACTO SIN FOTO
        /*if (contactos.get(i).getFoto()!=null) {
            personViewHolder.personPhoto.setImageBitmap(recogerImagen(contactos.get(i).getFoto()));
        }*/

        personViewHolder.tv_nombre.setText(contactos.get(i).getNombre());
        personViewHolder.tv_apellido.setText(contactos.get(i).getApellidos());

        personViewHolder.tv_domicilio.setText(contactos.get(i).getDireccion());
        personViewHolder.tv_telefono.setText(contactos.get(i).getTelefono());
        personViewHolder.tv_email.setText(contactos.get(i).getCorreo());



        // TODO hacer que aparezca el Toast de cada contacto aqui

    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Dado un string de ruta devuelve un bitmap con la imagen
    private Bitmap recogerImagen(String c){
        File ruta = Environment.getExternalStorageDirectory();
        File file = new File(ruta.getAbsolutePath(), c);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        return  bitmap;
    }
}
