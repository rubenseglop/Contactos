package aplicacion.contactos.com.miscontactos;

import java.io.Serializable;

class Galeria implements Serializable {
    private int id;
    private String path;

    /**
     * Constructor
     * @param id
     * @param URL
     */
    public Galeria(int id, String URL){
        this.setId(id);
        this.setPath(URL);
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getPath() {   return path;    }

    private void setPath(String path) {
        this.path = path;
    }

}



