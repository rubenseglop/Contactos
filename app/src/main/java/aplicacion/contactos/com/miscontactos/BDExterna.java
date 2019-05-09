package aplicacion.contactos.com.miscontactos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import static java.sql.DriverManager.println;

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
        System.out.println("DEBUG Contacto " + url);
        return leerUrl(url);
    }

    /**
     * Método para insertar en tabla Galeria
     * @param id int con el numero de id
     * @param url2 String con la url
     * @param uuid String con el numero de uuid
     * @return devuelve un String con el resultado en JSON
     */
    public String insertarGaleria(int id, String url2, String compartido, String uuid){
         String url = BDExternaLinks.insertargaleria + id +
                "&URL=" + url2 +
                "&COMPARTIDO=" + compartido +
                "&UUIDUNIQUE=" + uuid;
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20");
        println("DEBUG Galeria " + url);
        return leerUrl(url);
    }
    public String insertarDomicilio(int id, String direccion, String uuid){
        String url = BDExternaLinks.insertardomicilio + id +
                "&DIRECCION=" + direccion +
                "&UUIDUNIQUE=" + uuid;
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20");
        println("DEBUG Domicilio " + url);
        return leerUrl(url);
    }
    public String insertarTelefono(int id, String numero, String uuid){
        String url = BDExternaLinks.insertartelefono + id +
                "&NUMERO=" + numero +
                "&UUIDUNIQUE=" + uuid;
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20");
        println("DEBUG Telefono " + url);
        return leerUrl(url);
    }

    public String borrartodo(String uuid){

        String url = BDExternaLinks.eliminatodo + uuid;
        return leerUrl(url);
    }

    public String leerUrl(String pagina) {

        String inputLine ="";
        try {
            URL url = new URL(pagina.toString());
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((inputLine = in.readLine()) != null)
                System.out.println("DEBUG URL++ " + inputLine);
            inputLine += inputLine;

            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputLine;
    }
}
