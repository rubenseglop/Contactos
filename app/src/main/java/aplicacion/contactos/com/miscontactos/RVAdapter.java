package aplicacion.contactos.com.miscontactos;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{

    ArrayList<Contacto> contactos;

    RVAdapter(ArrayList<Contacto> contactos){
        this.contactos = contactos;
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView tv_nombre;
        TextView tv_apellido;
        TextView tv_domicilio;
        TextView tv_telefono;
        TextView tv_email;

        ImageView personPhoto;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            tv_nombre = (TextView)itemView.findViewById(R.id.id_nombre);
            tv_apellido = (TextView)itemView.findViewById(R.id.id_apellido);
            tv_domicilio = (TextView)itemView.findViewById(R.id.id_domicilio);
            tv_telefono = (TextView)itemView.findViewById(R.id.id_telefono);
            tv_email = (TextView)itemView.findViewById(R.id.id_email);
            personPhoto = (ImageView)itemView.findViewById(R.id.foto);
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
        personViewHolder.tv_nombre.setText(contactos.get(i).getNombre());
        personViewHolder.tv_apellido.setText(contactos.get(i).getApellidos());

        personViewHolder.tv_domicilio.setText(contactos.get(i).getDireccion());
        personViewHolder.tv_telefono.setText(contactos.get(i).getTelefono());
        personViewHolder.tv_email.setText(contactos.get(i).getCorreo());

        //personViewHolder.personPhoto.setImageResource(contactos.get(i).photoId);
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
