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

        Glide.with(mContext)
                .load(list.get(position).getImageId())
                .into(imageView);

        TextView textView= itemView.findViewById(R.id.txt);
        textView.setText(list.get(position).getText());
        return itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup
            parent){
        return getView(position,convertView,parent);
    }

}
class SpinnerContactosData {
    String text;
    String imageId;
    public SpinnerContactosData(String text, String imageId){
        this.text=text;
        this.imageId=imageId;
    }
    public String getText(){
        return text;
    }
    public String getImageId(){
        return imageId;
    }
}