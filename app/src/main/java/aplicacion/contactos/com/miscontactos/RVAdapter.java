package aplicacion.contactos.com.miscontactos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{

    ArrayList<Contacto> contactos;

    RVAdapter(ArrayList<Contacto> contactos){
        this.contactos = contactos;
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView personPhoto;
        TextView tv_nombre;
        TextView tv_apellido;
        TextView tv_domicilio;
        TextView tv_telefono;
        TextView tv_email;

        @SuppressLint("ResourceAsColor")
        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            personPhoto = (ImageView)itemView.findViewById(R.id.foto);
            tv_nombre = (TextView)itemView.findViewById(R.id.id_nombre);
            tv_nombre.setTextColor(ColoresApp.colorTexto);
            tv_apellido = (TextView)itemView.findViewById(R.id.id_apellido);
            tv_apellido.setTextColor(ColoresApp.colorTexto);
            tv_domicilio = (TextView)itemView.findViewById(R.id.id_domicilio);
            tv_domicilio.setTextColor(ColoresApp.colorTexto);
            tv_telefono = (TextView)itemView.findViewById(R.id.id_telefono);
            tv_domicilio.setTextColor(ColoresApp.colorTexto);
            tv_email = (TextView)itemView.findViewById(R.id.id_email);
            tv_email.setTextColor(ColoresApp.colorTexto);
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

        //TODO en cuanto todo sea online, quedarme con el Picasso del if
        if (contactos.get(i).getFoto().equals("http://iesayala.ddns.net/BDSegura/misContactos/fotosperfiles/perfil.png")){
            Picasso.get().load("http://iesayala.ddns.net/BDSegura/misContactos/fotosperfiles/perfil.png").into(personViewHolder.personPhoto);
        } else {
            personViewHolder.personPhoto.setImageBitmap(recogerImagen(contactos.get(i).getFoto()));
        }
        try {
            personViewHolder.tv_nombre.setText(contactos.get(i).getNombre());
            personViewHolder.tv_apellido.setText(contactos.get(i).getApellidos());
            personViewHolder.tv_domicilio.setText(contactos.get(i).getDomicilios().get(0).getDireccion());
            personViewHolder.tv_telefono.setText(contactos.get(i).getTelefonos().get(0).getNumero());
        } catch (Exception e) {
            System.out.println("Problema detectado: " + e.getMessage()); //TODO ARREGLAR PROBLEMA DE INDICE CERO EN DOMICILIO Y TELEFONO
        }
        personViewHolder.tv_email.setText(contactos.get(i).getCorreo());
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Método que devuelve un Bitmap de un String pasado por parámetro
     * @param c String que le pasa el adaptador
     * @return Bitmap con la imagen
     */
    private Bitmap recogerImagen(String c){
        System.out.println("DEBUG FOTO " + c);
        Bitmap bitmap = BitmapFactory.decodeFile(c);
        return  bitmap;
    }

}
