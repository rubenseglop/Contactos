package aplicacion.contactos.com.miscontactos

import android.util.Log
import android.view.View
import com.google.gson.Gson
import java.io.IOException
import java.net.URL
import kotlin.collections.ArrayList

class luissancar (){

    lateinit var con:Contacto

    fun luisancar(con:Contacto){
        this.con = con

    }

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


    // http://iesayala.ddns.net/BDSegura/misContactos/vercontactos.php
    public fun leerUrl(urlString:String): String{

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

    fun URLJsonObjeto() {

        val gson = Gson()
        try {

            val json = leerUrl("http://iesayala.ddns.net/BDSegura/misContactos/vercontactos.php")

            val uncontacto = gson.fromJson(json, ArrayContactos::class.java)
            println("DEBUG uncontacto" + uncontacto)

            for (item in uncontacto.contactos!!.iterator()) {
                println("DEBUG por aqui")
                var newContacto = Contacto(item.id,
                        item.foto,
                        item.nombre,
                        item.apellidos,
                        item.direccion,
                        item.telefono,
                        item.correo)
            }
        } catch (e: Exception) {
            Log.d("RESULTADO", "error")
        }
    }


}