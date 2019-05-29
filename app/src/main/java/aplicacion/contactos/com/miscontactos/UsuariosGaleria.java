package aplicacion.contactos.com.miscontactos;

public class UsuariosGaleria {
    String nombre;
    String email;
    String path;
    String UUID;

    public UsuariosGaleria(String nombre, String email, String path, String UUID) {
        this.nombre = nombre;
        this.email = email;
        this.path = path;
        this.UUID = UUID;

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
