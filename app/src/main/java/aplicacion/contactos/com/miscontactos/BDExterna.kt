package aplicacion.contactos.com.miscontactos

import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.gson.Gson
import java.io.IOException
import java.net.URL
import android.content.Intent
import android.net.Uri


class BDExterna : AppCompatActivity() {
    fun insertar(id:String, foto:String, nombre:String,apellidos:String, domicilio:String, telefono:String, email:String) {

//val url= 'http://iesayala.ddns.net/BDSegura/misContactos/insertarcontacto.php/?ID="2"&FOTO="una"&NOMBRE="RUBEN"&APELLIDOS="DOS"&DOMICILIO="TRES"&TELEFONO="CUATRO"&EMAIL="JAJAJA"'

        /*
        http://iesayala.ddns.net/BDSegura/misContactos/insertarcontacto.php/?ID="2"&FOTO="una"&NOMBRE="RUBEN"&APELLIDOS="DOS"&DOMICILIO="TRES"&TELEFONO="CUATRO"&EMAIL="JAJAJA"
        **/

        val url = "http://iesayala.ddns.net/BDSegura/misContactos/insertarcontacto.php/?ID=" + id +
                "&FOTO=" + foto +
                "&NOMBRE=" + nombre +
                "&APELLIDOS=" + apellidos +
                "&DOMICILIO=" + domicilio +
                "&TELEFONO=" + telefono +
                "&EMAIL=" + email

        println("DEBUG INSERTAR")

        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)

    }

    private fun leerUrl(urlString: String): String {

        val response = try {
            URL(urlString)
                    .openStream()
                    .bufferedReader()
                    .use { it.readText() }
        } catch (e: IOException) {
            "Error with ${e.message}."
        }

        return response
    }


    fun leerFichero(fichero: String): String {
        var stringFichero = ""
        try {
            val stream = assets.open(fichero)
            val tamano = stream.available()
            val buffer = ByteArray(tamano)
            stream.read(buffer)
            stream.close()
            stringFichero = String(buffer)

        } catch (e: IOException) {

        }
        return stringFichero

    }
}