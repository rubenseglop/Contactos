package aplicacion.contactos.com.miscontactos;

public class BDExternaLinks {


    //static String SERVIDOR = "iesayala.ddns.net";
    static String SERVIDOR = "webcindario.com";

    //static String LINKS = "http://iesayala.ddns.net/BDSegura/misContactos/";   //1 IESAyala
    static String LINKS = "https://miscontactosrsl.webcindario.com/MisContactos/";   //2 Miarroba

    protected static String vercontactos = LINKS + "vercontactos.php?UUIDUNIQUE=";
    protected static String vergaleria = LINKS + "vergaleria.php?IDUSUARIO=";
    protected static String vergaleriacompartida = LINKS + "vergaleriacompartida.php?IDUSUARIO=";
    protected static String verdomicilio = LINKS + "verdomicilio.php?UUIDUNIQUE=";
    protected static String vertelefono = LINKS + "vertelefono.php?UUIDUNIQUE=";
    protected static String verUsuario = LINKS + "verusuario.php?UUIDUNIQUE=";
    protected static String verusuariogaleria = LINKS + "verusuariosgaleria.php";
    protected static String insertarusuario = LINKS +"insertarUsuario.php?NOMBRE=";
    protected static String borrarUsuario = LINKS + "eliminausuario.php?UUIDUNIQUE=";
    protected static String insertarcontacto = LINKS + "insertarcontacto.php?ID=";
    protected static String insertargaleria = LINKS + "insertargaleria.php?IDUSUARIO=";
    protected static String insertardomicilio = LINKS + "insertardomicilio.php?ID=";
    protected static String insertartelefono = LINKS + "insertartelefono.php?ID=";
    protected static String eliminacontacto = LINKS + "eliminacontacto.php?UUIDUNIQUE=";
    protected static String eliminaGaleria = LINKS + "eliminagaleria.php?IDUSUARIO=";
    protected static String enviaemail = LINKS + "email.php?EMAIL=";

    //FTP
    static final String FTP_HOST= "ftp.webcindario.com";
    static final String FTP_USER = "miscontactosrsl";
    static final String FTP_PASS  ="probando";

    static final String URLFTP = "https://miscontactosrsl.webcindario.com/uploads/";
}
