package aplicacion.contactos.com.miscontactos;

public class GaleriaCompartir{

    String id;
    String pathFoto;
    String uuid;

    /**
     * Constructor
     * @param id
     * @param pathFoto
     * @param uuid
     */
    public GaleriaCompartir(String id, String pathFoto, String uuid) {
        this.id = id;
        this.pathFoto = pathFoto;
        this.uuid = uuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPathFoto() {
        return pathFoto;
    }

    public void setPathFoto(String pathFoto) {
        this.pathFoto = pathFoto;
    }

    public GaleriaCompartir(String pathFoto) {
        this.pathFoto = pathFoto;
    }

}



