package aplicacion.contactos.com.miscontactos

import java.io.IOException
import java.net.URL


class luissancar (){

    lateinit var con:Contacto

    fun luisancar(con:Contacto){
        this.con = con
    }

    fun insertarContacto(id:String, foto:String, nombre:String, apellidos:String, galeria:Int, domicilio:Int, telefono:Int, email:String, uuid:String):String{
        var url = BDExternaLinks.insertarcontacto + id +
                "&FOTO=" + foto +
                "&NOMBRE=" + nombre +
                "&APELLIDOS=" + apellidos +
                "&GALERIAID=" + galeria +
                "&DOMICILIOID=" + domicilio +
                "&TELEFONOID=" + telefono +
                "&EMAIL=" + email +
                "&UUIDUNIQUE=" + uuid
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20")
        println("DEBUG Contacto " + url)
        return leerUrl(url)
    }
    fun insertarGaleria(id:Int, url:String, uuid:String):String{
        var url = BDExternaLinks.insertargaleria + id +
                "&URL=" + url +
                "&UUIDUNIQUE=" + uuid
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20")
        println("DEBUG Galeria " + url)
        return leerUrl(url)
    }
    fun insertarDomicilio(id:Int, direccion:String, uuid:String):String{
        var url = BDExternaLinks.insertardomicilio + id +
                "&DIRECCION=" + direccion +
                "&UUIDUNIQUE=" + uuid
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20")
        println("DEBUG Domicilio " + url)
        return leerUrl(url)
    }
    fun insertarTelefono(id:Int, numero:String, uuid:String):String{
        var url = BDExternaLinks.insertartelefono + id +
                "&NUMERO=" + numero +
                "&UUIDUNIQUE=" + uuid
        // Solución a los espacios (reemplazar por su valor hex)
        url = url.replace(" ", "%20")
        println("DEBUG Telefono " + url)
        return leerUrl(url)
    }

    fun borrartodo(uuid: String):String{

        var url = BDExternaLinks.eliminatodo + uuid
        return leerUrl(url);
    }

    // http://iesayala.ddns.net/BDSegura/misContactos/vercontactos.php
     fun leerUrl(urlString:String): String{

        val response = try {
            URL(urlString)
                    .openStream()
                    .bufferedReader()
                    .use { it.readText() }
        } catch (e: IOException) {
            "Error"
        }
        println("DEBUG response " + response)
        return response
    }
}