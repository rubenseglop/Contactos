package aplicacion.contactos.com.miscontactos

import android.util.Log
import android.view.View
import com.google.gson.Gson
import java.io.IOException
import java.net.URL

class luissancar {

    fun insertar(id:String, foto:String, nombre:String,apellidos:String,domicilio:String,telefono:String,email:String){

        var url = "http://iesayala.ddns.net/BDSegura/misContactos/insertarcontacto.php/?ID=" + id +
                "&FOTO=" + foto +
                "&NOMBRE=" + nombre +
                "&APELLIDOS=" + apellidos +
                "&DOMICILIO=" + domicilio +
                "&TELEFONO=" + telefono +
                "&EMAIL=" + email
        println("DEBUG url " + url)
        leerUrl(url)

    }
    fun borrartodo(){

        var url = "http://iesayala.ddns.net/BDSegura/misContactos/eliminatodo.php"
        leerUrl(url);
    }

    private fun leerUrl(urlString:String): String{

        val response = try {
            URL(urlString)
                    .openStream()
                    .bufferedReader()
                    .use { it.readText() }
        } catch (e: IOException) {
            "Error with ${e.message}."
        }
        println("DEBUG response " + response)

        return response
    }

}