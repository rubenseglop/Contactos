package aplicacion.contactos.com.miscontactos;

import java.io.Serializable;

class Domicilio implements Serializable {
    private int id;
    private String direccion;

    /**
     * Constructor
     * @param id
     * @param direccion
     */
    public Domicilio(int id, String direccion){
        this.setId(id);
        this.setDireccion(direccion);
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    private void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
