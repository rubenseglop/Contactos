package aplicacion.contactos.com.miscontactos;

import java.io.Serializable;

public class Domicilio implements Serializable {
    int id;
    String direccion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String URL) {
        this.direccion = direccion;
    }
}
