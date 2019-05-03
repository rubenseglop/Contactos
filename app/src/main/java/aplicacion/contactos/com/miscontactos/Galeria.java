package aplicacion.contactos.com.miscontactos;

import java.io.Serializable;

public class Galeria implements Serializable {
    private int id;
    private String URL;

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

