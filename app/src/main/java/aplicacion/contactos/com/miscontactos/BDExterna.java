package aplicacion.contactos.com.miscontactos;

import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class BDExterna {
    /**
     * Método para insertar un contacto en la BD Externa
     * @param id
     * @param foto
     * @param nombre
     * @param apellidos
     * @param galeria
     * @param domicilio
     * @param telefono
     * @param email
     * @param uuid
     * @return devuelve un String con el resultado en JSON
     */
    public String insertarContacto(String id, String foto, String nombre, String apellidos, int galeria, int domicilio, int telefono, String email, String uuid){
        String url = BDExternaLinks.insertarcontacto + id +
                "&FOTO=" + foto +
                "&NOMBRE=" + nombre +
                "&APELLIDOS=" + apellidos +
                "&GALERIAID=" + galeria +
                "&DOMICILIOID=" + domicilio +
                "&TELEFONOID=" + telefono +
                "&EMAIL=" + email +
                "&UUIDUNIQUE=" + uuid;
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20");
        return leerUrl(url);
    }

    /**
     * Método para insertar en tabla Galeria
     * @param id int con el numero de id
     * @param path String con la url
     * @param uuid String con el numero de uuid
     * @return devuelve un String con el resultado en JSON
     */
    public String insertarGaleria(int id, String path, String uuid){
         String url = BDExternaLinks.insertargaleria + id +
                "&PATH=" + path +
                "&UUIDUNIQUE=" + uuid;
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20");

        return leerUrl(url);
    }
    public String insertarDomicilio(int id, String direccion, String uuid){
        String url = BDExternaLinks.insertardomicilio + id +
                "&DIRECCION=" + direccion +
                "&UUIDUNIQUE=" + uuid;
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20");
        //System.out.println("DEBUG EXPORTAR " + url);
        return leerUrl(url);
    }
    public String insertarTelefono(int id, String numero, String uuid){
        String url = BDExternaLinks.insertartelefono + id +
                "&NUMERO=" + numero.replace("+", "%2B") +
                "&UUIDUNIQUE=" + uuid;
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20");
        System.out.println("DEBUG Telefono " + url);
        return leerUrl(url);
    }

    /**
     * Método que elimina toda la base de datos externa del usuario UUID escogido
     * @param uuid
     * @return Devuelve la cadena String "Error" (determinado en el php) si algo falla
     */
    public String borrartodo(String uuid){

        String url = BDExternaLinks.eliminatodo + uuid;
        return leerUrl(url);
    }

    /**
     * Método que convierte el resultado de una URL (http) en un String
     * @param pagina
     * @return Devuelve la cadena de texto proporcionado por el php
     */
    public String leerUrl(String pagina) {

        String inputLine ="";
        try {
            URL url = new URL(pagina);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((inputLine = in.readLine()) != null)
                System.out.println("DEBUG URL++ " + inputLine);
            inputLine += inputLine;

            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();        }

        return inputLine;
    }

    public String insertarUsuario(CharSequence nombre, CharSequence email, String path, ImageView imagen, String uuid) {
        String url = BDExternaLinks.insertarusuario + nombre +
                "&EMAIL=" + email +
                "&FOTO=" + path +
                "&UUIDUNIQUE=" + uuid;
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20");
        //TODO SUBIR IMAGEN PHP

        return leerUrl(url);
    }
}
