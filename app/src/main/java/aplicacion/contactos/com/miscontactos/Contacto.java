package aplicacion.contactos.com.miscontactos;

import java.io.Serializable;
import java.util.ArrayList;

class Contacto implements Serializable {
    private int id;
    private String foto;
    private String nombre;
    private String apellidos;
    private int galeria_id;
    private int direccion_id;
    private int telefono_id;
    private String correo;

    private ArrayList<Galeria> galerias;
    private ArrayList<Domicilio> domicilios;
    private ArrayList<Telefono> telefonos;

    /**
     * Constructor
     * @param id
     * @param foto
     * @param nombre
     * @param apellidos
     * @param galeria_id
     * @param direccion_id
     * @param telefono_id
     * @param correo
     * @param galerias
     * @param domicilios
     * @param telefonos
     */
    public Contacto(int id, String foto, String nombre, String apellidos, int galeria_id, int direccion_id, int telefono_id, String correo, ArrayList<Galeria> galerias, ArrayList<Domicilio> domicilios, ArrayList<Telefono> telefonos) {
        this.setId(id);
        this.setFoto(foto);
        this.setNombre(nombre);
        this.setApellidos(apellidos);
        this.setGaleria_id(galeria_id);
        this.setDireccion_id(direccion_id);
        this.setTelefono_id(telefono_id);
        this.setCorreo(correo);
        this.galerias = galerias;
        this.domicilios = domicilios;
        this.telefonos = telefonos;
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getFoto() {
        return foto;
    }

    private void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }

    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    private void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getGaleria_id() {
        return galeria_id;
    }

    private void setGaleria_id(int galeria_id) {
        this.galeria_id = galeria_id;
    }

    public int getDireccion_id() {
        return direccion_id;
    }

    private void setDireccion_id(int direccion_id) {
        this.direccion_id = direccion_id;
    }

    public int getTelefono_id() {
        return telefono_id;
    }

    private void setTelefono_id(int telefono_id) {
        this.telefono_id = telefono_id;
    }

    public String getCorreo() {
        return correo;
    }

    private void setCorreo(String correo) {
        this.correo = correo;
    }

    public ArrayList<Domicilio> getDomicilios() {  return domicilios;   }

    public void setDomicilios(ArrayList<Domicilio> domicilios) {
        this.domicilios = domicilios;
    }

    public ArrayList<Telefono> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(ArrayList<Telefono> telefonos) {
        this.telefonos = telefonos;
    }

    public ArrayList<Galeria> getGalerias() { return galerias; }

    public void setGalerias(ArrayList<Galeria> galerias) {   this.galerias = galerias;   }

}