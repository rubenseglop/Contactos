package aplicacion.contactos.com.miscontactos;

import java.io.Serializable;

public class Telefono implements Serializable {

    private int id;
    private String numero;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
