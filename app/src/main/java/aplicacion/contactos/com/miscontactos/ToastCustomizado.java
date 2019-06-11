package aplicacion.contactos.com.miscontactos;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

class ToastCustomizado {

    /**
     * Método que saca en pantalla un Toast customizado al centro y de fondo color verde
     * @param mContext
     * @param texto
     */
    public static void tostada(Context mContext, int texto) {

        View view;
        TextView text;

        Toast toast = Toast.makeText(mContext, texto, Toast.LENGTH_LONG);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.texto_grande));
        toast.setGravity(Gravity.CENTER, 0, -100);
        view = toast.getView();
        text = view.findViewById(android.R.id.message);
        text.setTextColor(mContext.getResources().getColor(R.color.colorTextoToast));
        text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        view.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        view.setBackgroundResource(R.color.colorFondoToast);
        toast.show();
    }
}
