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
import java.util.HashMap;

public class COAdapter extends RecyclerView.Adapter<COAdapter.GalleryViewHolder> {
    public static HashMap selected;
    public static GaleriaCompartir selectedUsuarioGaleriaRecyclerView;
    ArrayList<GaleriaCompartir> galeriaCompartida;
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
    public COAdapter(ArrayList<GaleriaCompartir> galeriaCompartida, Context mContext){
        this.galeriaCompartida = galeriaCompartida;
        selectedUsuarioGaleriaRecyclerView = null;
        selected = new HashMap();
        this.mContext = mContext;

    }

    @Override
    public COAdapter.GalleryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filas_compartir, viewGroup, false);
        COAdapter.GalleryViewHolder pvh = new COAdapter.GalleryViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(COAdapter.GalleryViewHolder galleryViewHolder, int i) {

        try {
            Glide.with(mContext)
                    .load(galeriaCompartida.get(i).getPathFoto())
                    .into(galleryViewHolder.fotogaleria);

            //galleryViewHolder.fotogaleria.setImageBitmap(recogerImagen(galeriaCompartida.get(i).getPathFoto()));
            galleryViewHolder.tv_nombrefoto.setText(nombreArchivo(galeriaCompartida.get(i).getPathFoto()));

            selectedUsuarioGaleriaRecyclerView = new GaleriaCompartir(galeriaCompartida.get(i).getId(),galeriaCompartida.get(i).getPathFoto(),galeriaCompartida.get(i).getUuid());

            selected.put(i,selectedUsuarioGaleriaRecyclerView);
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

