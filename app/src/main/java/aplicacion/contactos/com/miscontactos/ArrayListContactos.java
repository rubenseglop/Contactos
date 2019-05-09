package aplicacion.contactos.com.miscontactos;

import java.util.ArrayList;

public class ArrayListContactos {
    ArrayList<Contacto> con = new ArrayList<Contacto>();

    public ArrayList<Contacto> getContacto() {
        return con;
    }

    public void setContacto(ArrayList<Contacto> con) {
        this.con = con;
    }
}
