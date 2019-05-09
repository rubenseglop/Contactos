package aplicacion.contactos.com.miscontactos;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    public String insertarFoto(String uuid, String image, String archivo){
        String url = BDExternaLinks.uploadgaleria +
                "?UUID=" + uuid +
                "&IMAGEN=" + image +
                "&ARCHIVO=" + archivo;
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20");

        System.out.println("DEBUG URI " +   url);
        return leerUrl(url);
    }


    public String insertarFoto2(String uuid, String image, String archivo) {

        try {
            /*
             * Creamos el objeto de HttpClient que nos permitira conectarnos
             * mediante peticiones http.
             */
            HttpClient httpclient = new DefaultHttpClient();

            /*
             * El objeto HttpPost permite que enviemos una peticion de tipo POST
             * a una URL especificada
             */
            HttpPost httppost = new HttpPost(BDExternaLinks.insertargaleria);

            // Una lista de parametros,
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            // Se agregan parametros.
            params.add(new BasicNameValuePair("UUID", uuid));
            params.add(new BasicNameValuePair("IMAGEN", image));
            params.add(new BasicNameValuePair("ARCHIVO", archivo));

            /*
             * Una vez añadidos los parametros actualizamos la entidad de
             * httppost, esto quiere decir en pocas palabras anexamos los
             * parametros al objeto para que al enviarse al servidor envien los
             * datos que hemos añadido
             */
            httppost.setEntity(new UrlEncodedFormEntity(params));

            // Eejecutamos enviando la informacion al Server.
            HttpResponse resp = httpclient.execute(httppost);

            // Obtenemos una respuesta.
            HttpEntity ent = resp.getEntity();

            String text = EntityUtils.toString(ent);
            // Envia la respuesta del Server.
            return text;

        } catch (Exception e) {
            // Devuelve el mensaje de error, en caso que lo haya.
            return e.getMessage();
        }

    }

}
