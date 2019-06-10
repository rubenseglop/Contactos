package aplicacion.contactos.com.miscontactos;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

public class DMAdapter extends RecyclerView.Adapter<DMAdapter.ViewHolder> {

    public static ArrayList<String> mDatasetDOM;

    /**
     * Constructor
     * @param myDataset
     */
    public DMAdapter(ArrayList<String> myDataset) {
        mDatasetDOM = myDataset;
    }

    @NonNull
    @Override
    public DMAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.filas_domicilio, parent, false);

        return new ViewHolder(v, new MyCustomEditTextListener());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.mEditText.setText(mDatasetDOM.get(holder.getAdapterPosition()));

        holder.menosDomicilio.setOnClickListener(view -> {
            //Aqui se elimina una de las tuplas de domicilio
            mDatasetDOM.remove(position);
            Anadir.actualizarAdaptador();
        });
    }

    @Override
    public int getItemCount() {
        return mDatasetDOM.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView menosDomicilio;
        final EditText mEditText;
        final MyCustomEditTextListener myCustomEditTextListener;

        ViewHolder(@NonNull View v, MyCustomEditTextListener myCustomEditTextListener) {
            super(v);

            this.menosDomicilio = v.findViewById(R.id.menosDomicilio);
            this.mEditText = v.findViewById(R.id.id_rvdomicilio);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.mEditText.addTextChangedListener(myCustomEditTextListener);
        }
    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(@NonNull CharSequence charSequence, int i, int i2, int i3) {
            mDatasetDOM.set(position, charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

}
