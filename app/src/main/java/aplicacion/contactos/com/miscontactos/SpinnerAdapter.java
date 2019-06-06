package aplicacion.contactos.com.miscontactos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<SpinnerContactosData> {
    private int groupid;
    private ArrayList<SpinnerContactosData> list;
    private LayoutInflater inflater;
    private Context mContext;

    public SpinnerAdapter(Activity mContext, int groupid, int id, ArrayList<SpinnerContactosData> list){
        super(mContext,id,list);
        this.mContext = mContext;
        this.list=list;
        inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid=groupid;
    }

    public View getView(int position, View convertView, ViewGroup parent ){
        View itemView=inflater.inflate(groupid,parent,false);
        ImageView imageView= itemView.findViewById(R.id.img);

        if (list.get(position).getImageId().equals("NO")) {
            imageView.setImageResource(R.drawable.perfil);
        } else {
            Glide.with(mContext)
                    .load(list.get(position).getImageId())
                    .thumbnail(0.1f)
                    .into(imageView);
        }

        TextView tv_nombre= itemView.findViewById(R.id.spinner_nombre);
        TextView tv_email = itemView.findViewById(R.id.spinneremail);
        tv_nombre.setText(list.get(position).getNombre());
        tv_email.setText(list.get(position).getemail());
        return itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup
            parent){
        return getView(position,convertView,parent);
    }
}
