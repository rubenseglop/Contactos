package aplicacion.contactos.com.miscontactos;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

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

        // verificar que no existe esa UUID
        URL url=null;
        try {
            url = new URL(BDExternaLinks.verUsuario + uuid);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        URLConnection request = null;
        JsonElement root=null;
        try {
            request = url.openConnection();
            request.connect();
            // Convierte el contenido de la URL en un String
            root = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            JSONArray jArray = new JSONArray(root.toString());
            if (jArray.length() == 0) {
                //No existe (es su primer acceso)

                // Creo el usuario y subo su foto
                String urlString = BDExternaLinks.insertarusuario + nombre +
                        "&EMAIL=" + email +
                        "&FOTO=" + path +
                        "&UUIDUNIQUE=" + uuid;
                // Solución a los espacios (reemplazar por su valor hex)
                urlString = urlString.replace(" ", "%20");

                //reduzco la foto a lo minimo
                try {
                    BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    bitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, true);
                    imagen.setImageBitmap(bitmap);

                } catch (Exception e) {

                }
               // todo BDExternaLinks.
            }
        } catch (JSONException e) {
            System.out.println("DEBUG ERROR " + e.getMessage());
        }
        //TODO SUBIR IMAGEN PHP
        insertarFoto2("12","223", "34");

     return "";
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


