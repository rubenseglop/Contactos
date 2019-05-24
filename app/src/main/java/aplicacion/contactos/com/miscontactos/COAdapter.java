package aplicacion.contactos.com.miscontactos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class COAdapter extends RecyclerView.Adapter<COAdapter.GalleryViewHolder> {
    ArrayList<GaleriaCompartir> galeriaCompatir;
    public static HashMap deIdGaleria_Path;

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView fotogaleria;
        TextView tv_nombrefoto;

        GalleryViewHolder(View itemView) {
            super(itemView);
            fotogaleria = itemView.findViewById(R.id.fotogaleria);
            tv_nombrefoto = itemView.findViewById(R.id.id_nombrefoto);
        }
    }
    public COAdapter(ArrayList<GaleriaCompartir> galeriaCompartir){
        this.galeriaCompatir = galeriaCompartir;
        deIdGaleria_Path = new HashMap();
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
            galleryViewHolder.fotogaleria.setImageBitmap(recogerImagen(galeriaCompatir.get(i).getPathFoto()));
            galleryViewHolder.tv_nombrefoto.setText(nombreArchivo(galeriaCompatir.get(i).getPathFoto()));
            deIdGaleria_Path.put(i,galeriaCompatir.get(i).getPathFoto());
        } catch (Exception e) {
            System.out.println("Problema detectado: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        int result = 0;
        try {
            result = galeriaCompatir.size();
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

