package aplicacion.contactos.com.miscontactos;

import java.io.Serializable;

class Telefono implements Serializable {

    private int id;
    private String numero;

    // Constructor
    public Telefono(int id, String numero){
        this.setId(id);
        this.setNumero(numero);
    }


    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    private void setNumero(String numero) {
        this.numero = numero;
    }
}
