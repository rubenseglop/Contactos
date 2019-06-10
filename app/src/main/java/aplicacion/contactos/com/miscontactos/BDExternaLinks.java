package aplicacion.contactos.com.miscontactos;

class BDExternaLinks {


    //static String SERVIDOR = "iesayala.ddns.net";
    static final String SERVIDOR = "webcindario.com";

    //static String LINKS = "http://iesayala.ddns.net/BDSegura/misContactos/";   //1 IESAyala
    private static final String LINKS = "https://miscontactosrsl.webcindario.com/MisContactos/";   //2 Miarroba

    static final String vercontactos = LINKS + "vercontactos.php?UUIDUNIQUE=";
    static final String vergaleria = LINKS + "vergaleria.php?IDUSUARIO=";
    static final String vergaleriacompartida = LINKS + "vergaleriacompartida.php?IDUSUARIO=";
    static final String verdomicilio = LINKS + "verdomicilio.php?UUIDUNIQUE=";
    static final String vertelefono = LINKS + "vertelefono.php?UUIDUNIQUE=";
    static final String verUsuario = LINKS + "verusuario.php?UUIDUNIQUE=";
    static final String verusuariogaleria = LINKS + "verusuariosgaleria.php";
    static final String insertarusuario = LINKS +"insertarUsuario.php?NOMBRE=";
    static final String borrarUsuario = LINKS + "eliminausuario.php?UUIDUNIQUE=";
    static final String insertarcontacto = LINKS + "insertarcontacto.php?ID=";
    static final String insertargaleria = LINKS + "insertargaleria.php?IDUSUARIO=";
    static final String insertardomicilio = LINKS + "insertardomicilio.php?ID=";
    static final String insertartelefono = LINKS + "insertartelefono.php?ID=";
    static final String eliminacontacto = LINKS + "eliminacontacto.php?UUIDUNIQUE=";
    static final String eliminaGaleria = LINKS + "eliminagaleria.php?IDUSUARIO=";
    static final String enviaemail = LINKS + "email.php?EMAIL=";

    //FTP
    static final String FTP_HOST= "ftp.webcindario.com";
    static final String FTP_USER = "miscontactosrsl";
    static final String FTP_PASS  ="probando";

    static final String URLFTP = "https://miscontactosrsl.webcindario.com/uploads/";

}
