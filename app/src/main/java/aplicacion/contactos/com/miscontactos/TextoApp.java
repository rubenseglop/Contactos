package aplicacion.contactos.com.miscontactos;

public class TextoApp {
    public static int tamaio = 1;

    public static int nombre_texto;
    public static int texto_normal;

    public static void cambiarTexto() {
        if (tamaio == 1) {
            nombre_texto = 16;
            texto_normal = 14;
            tamaio = 2;
        } else {
            nombre_texto = 32;
            texto_normal = 28;
            tamaio = 1;
        }
    }
}
