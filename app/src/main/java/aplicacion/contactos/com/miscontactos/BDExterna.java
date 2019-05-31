package aplicacion.contactos.com.miscontactos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class BDExterna {

    /**
     * Método para insertar un contacto en la BD Externa
     *
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
    public String insertarContacto(String id, String foto, String nombre, String apellidos, int galeria, int domicilio, int telefono, String email, String uuid) {
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
     *
     * @param path String con la url
     * @param uuid String con el numero de uuid
     * @return devuelve un String con el resultado en JSON
     */
    public String insertarGaleria(String idusuario, String path, String uuid) {
        String url = BDExternaLinks.insertargaleria + idusuario +
                "&PATH=" + path +
                "&UUIDUNIQUE=" + uuid;

        System.out.println("DEBUG URL " + url);
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20");

        return leerUrl(url);
    }

    public String insertarDomicilio(int id, String direccion, String uuid) {
        String url = BDExternaLinks.insertardomicilio + id +
                "&DIRECCION=" + direccion +
                "&UUIDUNIQUE=" + uuid;
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20");
        //System.out.println("DEBUG EXPORTAR " + url);
        return leerUrl(url);
    }

    public String insertarTelefono(int id, String numero, String uuid) {
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
     *
     * @param uuid
     * @return Devuelve la cadena String "Error" (determinado en el php) si algo falla
     */
    public String borrartodo(String uuid) {
        String url = BDExternaLinks.eliminatodo + uuid;
        return leerUrl(url);
    }

    /**
     * Método que convierte el resultado de una URL (http) en un String
     *
     * @param pagina
     * @return Devuelve la cadena de texto proporcionado por el php
     */
    public String leerUrl(String pagina) {

        String inputLine = "";
        try {
            URL url = new URL(pagina);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((inputLine = in.readLine()) != null)
            inputLine += inputLine;
            in.close();
        } catch (MalformedURLException e) {
            return "ERROR";
        } catch (IOException e) {
            return "ERROR";
        }
        return inputLine;
    }

    /**
     * Método que genera un nuevo Usuario de telefono
     * Comprueba que no existe en la BDExtena y lo genera en caso de no hacerlo
     * Sube la foto tomada del perfil
     *
     * @param nombre
     * @param email
     * @param path
     * @param imagen
     * @param uuid
     * @param mContext
     * @return
     */
    public String insertarUsuario(CharSequence nombre, CharSequence email, String path, ImageView imagen, String uuid, Context mContext) {

        // verificar que no existe esa UUID
        URL url = null;
        try {
            url = new URL(BDExternaLinks.verUsuario + uuid);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        URLConnection request = null;
        JsonElement root = null;
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
                //No existe (es su primer acceso). Creo el usuario y subo su foto
                String urlString = BDExternaLinks.insertarusuario + nombre +
                        "&EMAIL=" + email +
                        "&FOTO=" + path +
                        "&UUIDUNIQUE=" + uuid;
                // Solución a los espacios (reemplazar por su valor hex)
                urlString = urlString.replace(" ", "%20");
                leerUrl(urlString);

                //reduzco la foto a lo minimo
                try {
                    BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    bitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, true);
                    imagen.setImageBitmap(bitmap);

                } catch (Exception e) {

                }
            }
        } catch (JSONException e) {
            System.out.println("DEBUG ERROR " + e.getMessage());
        }
        return "";
    }


    public void borrarUsuario(String uuid) {
        String url = BDExternaLinks.borrarUsuario + uuid;
        url = url.replace(" ", "%20");
        System.out.println("DEBUG BORRATE " + url);
        leerUrl(url);
    }

    public static ArrayList<UsuariosGaleria> devuelveUsuarios(Context mContext) {

        ArrayList<UsuariosGaleria> devuelta = new ArrayList<>();
        String UUID = BDInterna.getUniqueID();
        URL url = null;
        try {
            String sURL = BDExternaLinks.verusuariogaleria;
            url = new URL(sURL);
            URLConnection request = null;
            request = url.openConnection();
            request.connect();
            // Convierte el contenido de la URL en un String
            JsonElement root = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));

            try {
                JSONArray jArray = new JSONArray(root.toString());
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    devuelta.add(new UsuariosGaleria(
                            json_data.getString("NOMBRE"),
                            json_data.getString("EMAIL"),
                            json_data.getString("FOTO"),
                            json_data.getString("UUIDUNIQUE")
                            ));


                }
            } catch (JSONException e) {
                Toast.makeText(mContext, R.string.error_metodo, Toast.LENGTH_SHORT).show();
            }
        } catch (
                MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(mContext, R.string.errorserver, Toast.LENGTH_LONG).show();
        } catch (
                IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, R.string.errorconex, Toast.LENGTH_LONG).show();
        }
        return devuelta;
    }

    public static ArrayList<GaleriaCompartir> devuelveGaleriaCompleta(Context mContext) {
        ArrayList<GaleriaCompartir> devuelta = new ArrayList<>();
        String UUID = BDInterna.getUniqueID();
        URL url = null;
        try {
            String sURL = BDExternaLinks.vergaleriacompartida + UUID;
            url = new URL(sURL);
            URLConnection request = null;
            request = url.openConnection();
            request.connect();
            // Convierte el contenido de la URL en un String
            JsonElement root = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));

            try {
                JSONArray jArray = new JSONArray(root.toString());
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);

                    devuelta.add(new GaleriaCompartir(
                            json_data.getString("IDUSUARIO"),
                            json_data.getString("PATH"),
                            json_data.getString("UUIDUNIQUE")
                    ));

                }
            } catch (JSONException e) {
                Toast.makeText(mContext, R.string.error_metodo, Toast.LENGTH_SHORT).show();
            }
        } catch (
                MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(mContext, R.string.errorserver, Toast.LENGTH_LONG).show();
        } catch (
                IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, R.string.errorconex, Toast.LENGTH_LONG).show();
        }
        return devuelta;
    }



    public static ArrayList<GaleriaCompartir> devuelveGaleria(Context mContext, String selectUUID) {
        ArrayList<GaleriaCompartir> devuelta = new ArrayList<>();
        String UUID = BDInterna.getUniqueID();
        URL url = null;
        try {
            String sURL = BDExternaLinks.vergaleria + UUID;
            url = new URL(sURL);
            URLConnection request = null;
            request = url.openConnection();
            request.connect();
            // Convierte el contenido de la URL en un String
            JsonElement root = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));

            try {
                JSONArray jArray = new JSONArray(root.toString());
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    if (json_data.getString("UUIDUNIQUE").equals(selectUUID)) {
                        devuelta.add(new GaleriaCompartir(
                                json_data.getString("IDUSUARIO"),
                                json_data.getString("PATH"),
                                json_data.getString("UUIDUNIQUE")
                        ));
                    }


                }
            } catch (JSONException e) {
                Toast.makeText(mContext, R.string.error_metodo, Toast.LENGTH_SHORT).show();
            }
        } catch (
                MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(mContext, R.string.errorserver, Toast.LENGTH_LONG).show();
        } catch (
                IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, R.string.errorconex, Toast.LENGTH_LONG).show();
        }
        return devuelta;
    }

    public String borraGaleria(String idusuario, String path, String uuid) {

        String url = BDExternaLinks.eliminaGaleria + idusuario +
                "&PATH=" + path +
                "&UUIDUNIQUE=" + uuid;

        url = url.replace(" ", "%20");
        System.out.println("DEBUG BORRATE " + url);
        return leerUrl(url);
    }
}




