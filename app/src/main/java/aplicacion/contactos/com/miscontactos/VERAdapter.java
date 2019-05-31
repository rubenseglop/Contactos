package aplicacion.contactos.com.miscontactos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class VERAdapter extends RecyclerView.Adapter<VERAdapter.GalleryViewHolder>  {

    public static GaleriaCompartir selectedUsuarioGaleriaRecyclerView;
    ArrayList<GaleriaCompartir> galeriaCompartida;
    ArrayList<UsuariosGaleria> usuarios;
    Context mContext;

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView fotogaleria;
        TextView tv_nombrefoto;

        GalleryViewHolder(View itemView) {
            super(itemView);
            fotogaleria = itemView.findViewById(R.id.fotogaleria);
            tv_nombrefoto = itemView.findViewById(R.id.id_nombrefoto);
        }
    }
    public VERAdapter(ArrayList<GaleriaCompartir> galeriaCompartida, ArrayList<UsuariosGaleria> usuarios, Context mContext){
        this.galeriaCompartida = galeriaCompartida;
        selectedUsuarioGaleriaRecyclerView = null;
        this.usuarios=usuarios;
        this.mContext = mContext;

    }

    @Override
    public VERAdapter.GalleryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filas_compartir, viewGroup, false);
        VERAdapter.GalleryViewHolder pvh = new VERAdapter.GalleryViewHolder(v);
        return pvh;
    }


    @Override
    public void onBindViewHolder(VERAdapter.GalleryViewHolder galleryViewHolder, int i) {
        try {

            Glide.with(mContext)
                    .load(galeriaCompartida.get(i).getPathFoto())
                    .into(galleryViewHolder.fotogaleria);

            //galleryViewHolder.fotogaleria.setImageBitmap(recogerImagen(galeriaCompartida.get(i).getPathFoto()));
            String nombrecompleto = "";
            for (int j = 0; j < usuarios.size(); j++) {
                if (galeriaCompartida.get(i).getId().equals(usuarios.get(j).getUUID())) {
                    nombrecompleto = usuarios.get(j).getNombre() + "<" + usuarios.get(j).getEmail()+">";
                }
            }
            galleryViewHolder.tv_nombrefoto.setText(nombreArchivo(nombrecompleto));
        } catch (Exception e) {
            System.out.println("Problema detectado: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        int result = 0;
        try {
            result = galeriaCompartida.size();
        } catch (Exception e) {
        }
        //System.out.println("DEBUG SPINNER " + result);
        return result;
    }

    /**
     * Convierte la image en un Bitmap y la reduzco
     * @param c
     * @return
     */
    private Bitmap recogerImagen(String c){
        Bitmap bitmapImage = BitmapFactory.decodeFile(c);
        int nh = (int) ( bitmapImage.getHeight() * (100.0 / bitmapImage.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 100, nh, true);
        return scaled;
    }

    private String nombreArchivo(String path) {
        File f = new File(path);
        return f.getName();
    }
}
