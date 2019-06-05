package aplicacion.contactos.com.miscontactos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ImageView;

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

    BDInterna bdInterna;
    Context mContext;

    public BDExterna(Context mContext) {
        this.mContext = mContext;
        bdInterna = new BDInterna(mContext);
    }

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


        System.out.println("DEBUG " + url);
        return leerUrl(url);
    }

    /**
     * Método para insertar en tabla Galeria
     *
     * @param path String con la url
     * @param uuid String con el numero de uuid
     * @return devuelve "OK o "ERROR"
     */
    public String insertarGaleria(String idusuario, String path, String uuid) {
        String url = BDExternaLinks.insertargaleria + idusuario +
                "&PATH=" + path +
                "&UUIDUNIQUE=" + uuid;

        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20");

        return leerUrl(url);
    }

    /**
     * Método que inserta un domicilio en la base de datos externa
     * @param id
     * @param direccion
     * @param uuid
     * @return devuelve "OK o "ERROR"
     */
    public String insertarDomicilio(int id, String direccion, String uuid) {
        String url = BDExternaLinks.insertardomicilio + id +
                "&DIRECCION=" + direccion +
                "&UUIDUNIQUE=" + uuid;
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20");

        return leerUrl(url);
    }

    /**
     * Método que inserta un telefono en la base de datos externa
     * @param id
     * @param numero
     * @param uuid
     * @return devuelve "OK o "ERROR"
     */
    public String insertarTelefono(int id, String numero, String uuid) {
        String url = BDExternaLinks.insertartelefono + id +
                "&NUMERO=" + numero.replace("+", "%2B") +
                "&UUIDUNIQUE=" + uuid;
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20");

        return leerUrl(url);
    }

    /**
     * Método que elimina toda la base de datos externa del usuario UUID escogido
     *
     * @param uuid
     * @return devuelve "OK o "ERROR"
     */
    public String borrartodo(String uuid) {
        String url = BDExternaLinks.eliminacontacto + uuid;
        return leerUrl(url);
    }

    /**
     * Método que convierte el resultado de una URL (http) en un String
     *
     * @param pagina
     * @return Devuelve la cadena de texto proporcionado por el php
     */
    public String leerUrl(String pagina) {

        String inputLine = "OK";
        try {
            URL url = new URL(pagina);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

     /*       while ((inputLine = in.readLine()) != null);
            System.out.println("DEBUG input " + inputLine + " url " + url);
            inputLine = inputLine + inputLine;*/

            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            inputLine = "ERROR";
        } catch (IOException e) {
            e.printStackTrace();
            inputLine= "ERROR";
        }

        System.out.println("DEBUG SALIDA INPUTLINE " + inputLine);
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
     * @return devuelve "OK o "ERROR"
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



    public String insertarClave(CharSequence email,Context mContext) {

        String url = BDExternaLinks.enviaemail + email;
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20");
        return leerUrl(url);
    }


    /**
     * Método que elimina a un usuario
     * @param uuid
     */
    public void borrarUsuario(String uuid) {

        System.out.println("DEBUG BORRADO USUARIO " + uuid);
        String url = BDExternaLinks.borrarUsuario + uuid;
        url = url.replace(" ", "%20");
        leerUrl(url);
    }

    /**
     * Mñetodo statico que devuelve un ArrayList de UsuariosGaleria
     * @param mContext
     * @return
     */
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
                            json_data.getString("UUIDUNIQUE"),
                            json_data.getString("CLAVE")
                            ));


                }
            } catch (JSONException e) {
                ToastCustomizado.tostada(mContext, R.string.error_metodo);
            }
        } catch (MalformedURLException e) {
            ToastCustomizado.tostada(mContext, R.string.errorserver);
        } catch (IOException e) {
            ToastCustomizado.tostada(mContext, R.string.errorconex);
        }
        return devuelta;
    }

    /**
     * Método statico que devuelve un ArrayList de GaleriaCompartir
     * @param mContext
     * @return
     */
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
                ToastCustomizado.tostada(mContext, R.string.error_metodo);
            }
        } catch (MalformedURLException e) {
            ToastCustomizado.tostada(mContext, R.string.errorserver);
        } catch (IOException e) {
            ToastCustomizado.tostada(mContext, R.string.errorconex);
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
                ToastCustomizado.tostada(mContext, R.string.error_metodo);
            }
        } catch (MalformedURLException e) {
            ToastCustomizado.tostada(mContext, R.string.errorserver);
        } catch (IOException e) {
            ToastCustomizado.tostada(mContext, R.string.errorconex);
        }
        return devuelta;
    }

    public String borraGaleria(String idusuario, String path, String uuid) {

        String url = BDExternaLinks.eliminaGaleria + idusuario +
                "&PATH=" + path +
                "&UUIDUNIQUE=" + uuid;

        url = url.replace(" ", "%20");
        return leerUrl(url);
    }

    public static boolean hayconexion(Context context)
    {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

    public static boolean hayservidor(String servidorping){
        boolean servidor = false;
        Runtime runtime = Runtime.getRuntime();
        try {
            System.out.println("DEBUG ping a " + servidorping);
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 " + servidorping);
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
