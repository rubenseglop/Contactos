package aplicacion.contactos.com.miscontactos;

public class BDExternaLinks {



    static String LINKS = "http://iesayala.ddns.net/BDSegura/misContactos/";   //1 IESAyala
    //static String LINKS = "https://miscontactosrsl.webcindario.com/";   //2 Miarroba


    static String conexion = LINKS + "conexion.php";
    static String vercontactos = LINKS + "vercontactos.php/?UUIDUNIQUE=";
    static String vergaleria = LINKS + "vergaleria.php/?IDUSUARIO=";
    static String vergaleriacompartida = LINKS + "vergaleriacompartida.php/?IDUSUARIO=";
    static String verdomicilio = LINKS + "verdomicilio.php/?UUIDUNIQUE=";
    static String vertelefono = LINKS + "vertelefono.php/?UUIDUNIQUE=";
    static String verUsuario = LINKS + "verusuario.php/?UUIDUNIQUE=";
    static String verusuariogaleria = LINKS + "verusuariosgaleria.php";
    static String insertarusuario = LINKS +"insertarUsuario.php/?NOMBRE=";
    static String insertarcontacto = LINKS + "insertarcontacto.php/?ID=";
    static String insertargaleria = LINKS + "insertargaleria.php/?IDUSUARIO=";
    static String insertardomicilio = LINKS + "insertardomicilio.php/?ID=";
    static String insertartelefono = LINKS + "insertartelefono.php/?ID=";
    static String eliminatodo = LINKS + "eliminatodo.php?UUIDUNIQUE=";
    static String eliminaGaleria = LINKS + "eliminagaleria.php?IDUSUARIO=";
    static String verUUID = LINKS + "verUUID.php";
    static String uploadgaleria = LINKS + "GALERIA/uploadgaleria.php";

    //FTP
    static final String FTP_HOST= "ftp.webcindario.com";
    static final String FTP_USER = "miscontactosrsl";
    static final String FTP_PASS  ="probando";

    static final String URLFTP = "https://miscontactosrsl.webcindario.com/uploads/";


}
