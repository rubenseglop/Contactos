package aplicacion.contactos.com.miscontactos;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
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
    private static GaleriaCompartir selectedUsuarioGaleriaRecyclerView;
    private final ArrayList<GaleriaCompartir> galeriaCompartida;
    private final Context mContext;

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        final ImageView fotogaleria;
        final TextView tv_nombrefoto;

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
        return new GalleryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(COAdapter.GalleryViewHolder galleryViewHolder, int i) {

        try {
            Glide.with(mContext)
                    .load(galeriaCompartida.get(i).getPathFoto())
                    .into(galleryViewHolder.fotogaleria);

            galleryViewHolder.tv_nombrefoto.setText(nombreArchivo(galeriaCompartida.get(i).getPathFoto()));
            selectedUsuarioGaleriaRecyclerView = new GaleriaCompartir(galeriaCompartida.get(i).getId(),
                    galeriaCompartida.get(i).getPathFoto(),galeriaCompartida.get(i).getUuid());


            galleryViewHolder.fotogaleria.setOnClickListener(v -> clickDialog(mContext, galeriaCompartida.get(i).getPathFoto()));

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
            e.printStackTrace();
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
        return Bitmap.createScaledBitmap(bitmapImage, 100, nh, true);
    }

    private String nombreArchivo(String path) {
        File f = new File(path);
        return f.getName();
    }

    private void clickDialog(Context mContext, String imagen) {
        AlertDialog.Builder alertadd = new AlertDialog.Builder(mContext);
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View view = factory.inflate(R.layout.imagen_dialog, null);
        ImageView imageView = view.findViewById(R.id.dialog_imageview);

        Glide.with(mContext)
                .load(imagen)
                .into(imageView);

        alertadd.setView(view);
        alertadd.setNeutralButton(R.string.cerrar, (dlg, sumthin) -> {

        });

        alertadd.show();
    }
}

