package aplicacion.contactos.com.miscontactos;

public class BDExternaLinks {


    static String LINKS = "http://iesayala.ddns.net/BDSegura/misContactos/";   //1 IESAyala
    // static String LINKS = "http://iesayala.ddns.net/BDSegura/misContactos/";   //2 Miarroba

    static String vercontactos = LINKS + "vercontactos.php/?UUIDUNIQUE=";
    static String vergaleria = LINKS + "vergaleria.php/?UUIDUNIQUE=";
    static String verdomicilio = LINKS + "verdomicilio.php/?UUIDUNIQUE=";
    static String vertelefono = LINKS + "vertelefono.php/?UUIDUNIQUE=";

    static String upload_php = LINKS + "upload.php";
    static String imageStoragePath = LINKS + "fotosperfiles/perfil.png";

    static String insertarcontacto = LINKS + "insertarcontacto.php/?ID=";
    static String insertargaleria = LINKS + "insertargaleria.php/?ID=";
    static String insertardomicilio = LINKS + "insertardomicilio.php/?ID=";
    static String insertartelefono = LINKS + "insertartelefono.php/?ID=";
    static String eliminatodo = LINKS + "eliminatodo.php?UUIDUNIQUE=";
    static String verUUID = LINKS + "verUUID.php";


}
