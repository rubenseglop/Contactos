package aplicacion.contactos.com.miscontactos;

import java.io.Serializable;

public class Galeria implements Serializable {
    private int id;
    private String URL;

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
}
