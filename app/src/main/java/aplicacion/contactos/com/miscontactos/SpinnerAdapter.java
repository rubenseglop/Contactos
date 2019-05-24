package aplicacion.contactos.com.miscontactos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<SpinnerContactosData> {
    private int groupid;
    private ArrayList<SpinnerContactosData> list;
    private LayoutInflater inflater;
    Context context;

    public SpinnerAdapter(Activity context, int groupid, int id, ArrayList<SpinnerContactosData>
            list){
        super(context,id,list);
        this.context = context;
        this.list=list;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid=groupid;
    }

    public View getView(int position, View convertView, ViewGroup parent ){
        View itemView=inflater.inflate(groupid,parent,false);
        ImageView imageView= itemView.findViewById(R.id.img);

        imageView.setImageBitmap(recogerImagen(list.get(position).getImageId()));

        TextView textView= itemView.findViewById(R.id.txt);
        textView.setText(list.get(position).getText());
        return itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup
            parent){
        return getView(position,convertView,parent);

    }

    private Bitmap recogerImagen(String c) {
        //todo pq tantas veces?
        Bitmap scaled = null;
        Bitmap bitmapImage;
        if (c.equals("NO")) {
            bitmapImage = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.perfil);
        } else {
            try {
                bitmapImage = BitmapFactory.decodeFile(c); //todo muy lento esto
            } catch (Exception e) {
                bitmapImage = null;
            }
        }

        try {
            int nh = (int) (bitmapImage.getHeight() * (100.0 / bitmapImage.getWidth()));
            scaled = Bitmap.createScaledBitmap(bitmapImage, 100, nh, true);
        } catch (Exception e) {
            scaled = null;
        }
        return scaled;
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