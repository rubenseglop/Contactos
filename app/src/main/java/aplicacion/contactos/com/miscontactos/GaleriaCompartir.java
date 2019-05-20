package aplicacion.contactos.com.miscontactos;

import android.os.Parcel;
import android.os.Parcelable;

    /* Implemento la interfaz Parcelable que me permitirá almacenar objetos de tipo GaleriaCompartir para
    * posteriormente usarlo en onSaveInstanceState (guardar el estado del objeto al destruir una Activity)*/

public class GaleriaCompartir implements Parcelable {

    String pathFoto;

    protected GaleriaCompartir(Parcel in) {
        pathFoto = in.readString();
    }

    public static final Creator<GaleriaCompartir> CREATOR = new Creator<GaleriaCompartir>() {
        @Override
        public GaleriaCompartir createFromParcel(Parcel in) {
            return new GaleriaCompartir(in);
        }

        @Override
        public GaleriaCompartir[] newArray(int size) {
            return new GaleriaCompartir[size];
        }
    };

    public String getPathFoto() {
        return pathFoto;
    }

    public void setPathFoto(String pathFoto) {
        this.pathFoto = pathFoto;
    }

    public GaleriaCompartir(String pathFoto) {
        this.pathFoto = pathFoto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pathFoto);
    }
}



