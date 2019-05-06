package aplicacion.contactos.com.miscontactos;

import java.io.Serializable;

public class Telefono implements Serializable {

    private int id;
    private String numero;

    public Telefono(int id, String numero){
        this.setId(id);
        this.setNumero(numero);
    }


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
