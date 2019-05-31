package aplicacion.contactos.com.miscontactos;

class SpinnerContactosData {
    String nombre;
    String email;
    String imageId;

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

