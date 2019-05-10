package aplicacion.contactos.com.miscontactos;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DMAdapter extends RecyclerView.Adapter<DMAdapter.DMViewHolder> {


    public ArrayList<String> items;
    public static ArrayList<String> salida;

    public static class DMViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public TextView domicilio;
        public ImageView mas;

        public DMViewHolder(View v) {
            super(v);
            domicilio = (TextView) v.findViewById(R.id.id_rvdomicilio);
            mas = (ImageView) v.findViewById(R.id.fotoperfil);
        }
    }

    public DMAdapter(ArrayList<String> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public DMViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.filas_domicilio, viewGroup, false);

        return new DMViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DMViewHolder viewHolder, int i) {
        viewHolder.domicilio.setText(items.get(i));
        salida = items;

    }

    public ArrayList<String> getItems() {
        System.out.println("DEBUG GUARDANDO " + salida.get(0));
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public interface OnItemClickListener {
        public void onClick(View view, int position);
    }

}
