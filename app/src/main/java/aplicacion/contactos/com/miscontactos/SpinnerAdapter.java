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

public class SpinnerAdapter extends ArrayAdapter<ItemData> {
    private int groupid;
    private ArrayList<ItemData> list;
    private LayoutInflater inflater;

    public SpinnerAdapter(Activity context, int groupid, int id, ArrayList<ItemData>
            list){
        super(context,id,list);
        this.list=list;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid=groupid;
    }

    public View getView(int position, View convertView, ViewGroup parent ){
        View itemView=inflater.inflate(groupid,parent,false);
        ImageView imageView=(ImageView)itemView.findViewById(R.id.img);

        imageView.setImageBitmap(recogerImagen(list.get(position).getImageId()));

        TextView textView=(TextView)itemView.findViewById(R.id.txt);
        textView.setText(list.get(position).getText());
        return itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup
            parent){
        return getView(position,convertView,parent);

    }

    private Bitmap recogerImagen(String c) {
        Bitmap scaled = null;
        Bitmap bitmapImage;
        try {
            bitmapImage = BitmapFactory.decodeFile(c);
        } catch (Exception e) {
            bitmapImage = null;
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
class ItemData {
    String text;
    String imageId;
    public ItemData(String text, String imageId){
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