package aplicacion.contactos.com.miscontactos;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TFAdapter extends RecyclerView.Adapter<TFAdapter.ViewHolder> {


    public static ArrayList<String> mDatasetTEL;

    public TFAdapter(ArrayList<String> myDataset) {

        mDatasetTEL= myDataset;


    }
    @Override
    public TFAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.filas_telefono, parent, false);
        // pass MyCustomEditTextListener to viewholder in onCreateViewHolder
        // so that we don't have to do this expensive allocation in onBindViewHolder
        ViewHolder vh = new ViewHolder(v, new MyCustomEditTextListener());

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // update MyCustomEditTextListener every time we bind a new item
        // so that it knows what item in mDataset to update
        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.mEditText.setText(mDatasetTEL.get(holder.getAdapterPosition()));

        holder.menosTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatasetTEL.remove(position);
                Anadir.actualizarAdaptador();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatasetTEL.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView menosTelefono;
        public EditText mEditText;
        public MyCustomEditTextListener myCustomEditTextListener;

        @SuppressLint("ResourceAsColor")
        public ViewHolder(View v, MyCustomEditTextListener myCustomEditTextListener) {
            super(v);

            this.menosTelefono = v.findViewById(R.id.menosTelefono);
            this.mEditText = (EditText) v.findViewById(R.id.id_rvtelefono);
            this.mEditText.setTextColor(ColoresApp.colorTexto);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.mEditText.addTextChangedListener(myCustomEditTextListener);
        }
    }

    // we make TextWatcher to be aware of the position it currently works with
    // this way, once a new item is attached in onBindViewHolder, it will
    // update current position MyCustomEditTextListener, reference to which is kept by ViewHolder
    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            mDatasetTEL.set(position, charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

}
