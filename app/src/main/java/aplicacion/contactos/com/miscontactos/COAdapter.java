package aplicacion.contactos.com.miscontactos;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class COAdapter extends RecyclerView.Adapter<COAdapter.GalleryViewHolder> {
    ArrayList<GaleriaCompartir> galeriaCompatir;

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        CardView cv;

        ImageView fotogaleria;
        TextView tv_nombrefoto;


        @SuppressLint("ResourceAsColor")
        GalleryViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            fotogaleria = (ImageView)itemView.findViewById(R.id.fotogaleria);
            tv_nombrefoto = (TextView)itemView.findViewById(R.id.id_nombrefoto);

        }
    }
    COAdapter(ArrayList<GaleriaCompartir> galeriaCompartir){
        this.galeriaCompatir = galeriaCompartir;
    }


    @NonNull
    @Override
    public COAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filas_compartir, viewGroup, false);
        COAdapter.GalleryViewHolder pvh = new COAdapter.GalleryViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull COAdapter.GalleryViewHolder galleryViewHolder, int i) {

        try {
            galleryViewHolder.fotogaleria.setImageBitmap(recogerImagen(galeriaCompatir.get(i).getPathFoto()));
            galleryViewHolder.tv_nombrefoto.setText(nombreArchivo(galeriaCompatir.get(i).getPathFoto()));
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

    private Bitmap recogerImagen(String c){
        //System.out.println("DEBUG FOTO " + c);
        Bitmap bitmap = BitmapFactory.decodeFile(c);
        return  bitmap;
    }

    private String nombreArchivo(String path) {
        File f = new File(path);
        return f.getName();
    }
}

