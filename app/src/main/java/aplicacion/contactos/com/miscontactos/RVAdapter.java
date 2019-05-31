package aplicacion.contactos.com.miscontactos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {

    public static HashMap deId_Posicion; //Mapeo de ID Contacto con la posicion ocupada en el RecyclerView
    private ArrayList<Contacto> contactos;
    private Context mContext;

    /**
     * Constructor del adaptador
     * @param contactos
     * @param context
     */
    public RVAdapter(ArrayList<Contacto> contactos , Context context){
        this.contactos = contactos;
        mContext = context;
        deId_Posicion = new HashMap();
    }

    public class PersonViewHolder extends RecyclerView.ViewHolder {
        Context context;
        CardView cv;
        ImageView personPhoto;
        TextView tv_nombre;
        TextView tv_apellido;
        TextView tv_domicilio;
        TextView tv_telefono;
        TextView tv_email;
        ImageView ImagenEditContacto;

        PersonViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            cv = itemView.findViewById(R.id.cv);
            personPhoto = itemView.findViewById(R.id.foto);
            tv_nombre = itemView.findViewById(R.id.id_nombre);
            tv_apellido = itemView.findViewById(R.id.id_apellido);
            tv_domicilio = itemView.findViewById(R.id.id_domicilio);
            tv_telefono = itemView.findViewById(R.id.id_telefono);
            tv_email = itemView.findViewById(R.id.id_email);
            ImagenEditContacto = itemView.findViewById(R.id.ImagenEditarContacto);
        }
    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }

    /**
     * Se llama cuando se crea un RecyclerView y se usa para inicializar el ViewHolder
     * @param viewGroup
     * @param i
     * @return
     */
    @Override
    public PersonViewHolder onCreateViewHolder (ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filas_usuarios, viewGroup, false);
        PersonViewHolder personViewHolder = new PersonViewHolder(view);
        return personViewHolder;
    }

    /**
     * Llamado por RecyclerView para mostrar los datos en la posición especificada.
     * @param personViewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {

        if (contactos.get(i).getFoto().equals("NO")){
            personViewHolder.personPhoto.setImageResource(R.drawable.perfil);
        } else {
            personViewHolder.personPhoto.setImageBitmap(recogerImagen(contactos.get(i).getFoto()));
        }
            personViewHolder.tv_nombre.setText(contactos.get(i).getNombre());
            personViewHolder.tv_apellido.setText(contactos.get(i).getApellidos());

        try {
            personViewHolder.tv_domicilio.setText(contactos.get(i).getDomicilios().get(0).getDireccion());
        } catch (Exception e) {
            personViewHolder.tv_domicilio.setText("");
        }
        try {
            personViewHolder.tv_telefono.setText(contactos.get(i).getTelefonos().get(0).getNumero());
            personViewHolder.tv_telefono.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(android.content.Intent.ACTION_CALL,
                            Uri.parse("tel:" + contactos.get(i).getTelefonos().get(0).getNumero()));
                    mContext.startActivity(intent);
                }
            });
        }catch (Exception e) {
            personViewHolder.tv_telefono.setText("");
        }
        personViewHolder.tv_email.setText(contactos.get(i).getCorreo());
        deId_Posicion.put(i,contactos.get(i).getId());
        personViewHolder.ImagenEditContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, Anadir.class);
                intent.putExtra("EDIT", contactos.get(i));
                mContext.startActivity(intent);
            }
        });
    }

    /**
     * Llamado por RecyclerView cuando comienza a observar este adaptador.
     * @param recyclerView
     */
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
        Bitmap bitmapImage;
        Bitmap scaled=null;
        try {
            bitmapImage = BitmapFactory.decodeFile(c);
            int nh = (int) (bitmapImage.getHeight() * (100.0 / bitmapImage.getWidth()));
            scaled = Bitmap.createScaledBitmap(bitmapImage, 100, nh, true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //antes de devolverlo, le redondeo las esquinas
        return CameraUtils.redondearEsquinas(scaled, 20);
    }


}
