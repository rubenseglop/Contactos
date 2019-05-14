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

public class DMAdapter extends RecyclerView.Adapter<DMAdapter.ViewHolder> {


    public static ArrayList<String> mDatasetDOM;



    public DMAdapter(ArrayList<String> myDataset) {

        mDatasetDOM = myDataset;

    }
    @Override
    public DMAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.filas_domicilio, parent, false);
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
        holder.mEditText.setText(mDatasetDOM.get(holder.getAdapterPosition()));

        holder.menosDomicilio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Aqui se elimina una de las tuplas de domicilio
                mDatasetDOM.remove(position);
                Anadir.actualizarAdaptador();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mDatasetDOM.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView menosDomicilio;
        public EditText mEditText;
        public MyCustomEditTextListener myCustomEditTextListener;

        @SuppressLint("ResourceAsColor")
        public ViewHolder(View v, MyCustomEditTextListener myCustomEditTextListener) {
            super(v);

            this.menosDomicilio = v.findViewById(R.id.menosDomicilio);
            this.mEditText = (EditText) v.findViewById(R.id.id_rvdomicilio);
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
            mDatasetDOM.set(position, charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

}