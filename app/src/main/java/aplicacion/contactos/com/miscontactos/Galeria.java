package aplicacion.contactos.com.miscontactos;

import java.io.Serializable;

public class Galeria implements Serializable {
    private int id;
    private String URL;
    private String compartido;

    public Galeria(int id, String URL){
        this.setId(id);
        this.setURL(URL);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getCompartido() {
        return compartido;
    }

    public void setCompartido(String URL) {
        this.compartido = compartido;
    }
}



//http://iesayala.ddns.net/BDSegura/misContactos/GALERIA/uploadgaleria.php?CARPETAUUID=12231232-4243543543-435345345435-43543534