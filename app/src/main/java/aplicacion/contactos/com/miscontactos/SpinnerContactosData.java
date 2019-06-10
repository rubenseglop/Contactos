package aplicacion.contactos.com.miscontactos;

class SpinnerContactosData {
    private final String nombre;
    private final String email;
    private final String imageId;

    /**
     * Constructor
     * @param nombre
     * @param email
     * @param imageId
     */
    public SpinnerContactosData(String nombre, String email, String imageId) {
        this.nombre = nombre;
        this.imageId = imageId;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImageId() {
        return imageId;
    }

    public String getemail() {
        return email;
    }
}

