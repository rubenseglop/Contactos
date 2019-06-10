package aplicacion.contactos.com.miscontactos;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

class MetodoFTP {

    private static final String FTP_HOST = BDExternaLinks.FTP_HOST;
    private static final String FTP_USER = BDExternaLinks.FTP_USER;
    private static final String FTP_PASS = BDExternaLinks.FTP_PASS;
    private static Context mContext;

    /**
     * Constructor
     * @param mContext
     */
    public MetodoFTP(Context mContext) {
        MetodoFTP.mContext = mContext;
    }

    /**
     * Método para subir un archivo
     * @param fileName
     * @param carpeta
     */
    public void uploadFile(File fileName, String carpeta){

        File reducido = new File(ComprimeFoto.reducirfoto(String.valueOf(fileName), mContext));


        FTPClient client = new FTPClient();
        try {
            client.connect(FTP_HOST,21);
            client.login(FTP_USER, FTP_PASS);
            client.setType(FTPClient.TYPE_BINARY);
            client.changeDirectory("/web/uploads");

            if (!comprobarCarpeta(client, carpeta)) {
                client.createDirectory("/web/uploads/" + carpeta);
            }

            client.changeDirectory("/web/uploads/"+carpeta);

            client.upload(reducido);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }


    /**
     * Método que comprueba si existe una carpeta en el servidor ftp
     * @param client
     * @param carpeta
     * @return
     * @throws IOException
     * @throws FTPIllegalReplyException
     * @throws FTPException
     * @throws FTPDataTransferException
     * @throws FTPAbortedException
     * @throws FTPListParseException
     */
    private boolean comprobarCarpeta(FTPClient client, String carpeta) throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException {
        boolean result = false;
        FTPFile[] list = client.list();
        for (FTPFile ftpFile : list) {
            if (ftpFile.getName().equals(carpeta) && ftpFile.getSize() == 0 && ftpFile.getType() == 1) {
                result = true;
            }

        }
        return result;
    }

    /**
     * Método que elimina un archivo del servidor de ftp
     * @param fileName
     * @param carpeta
     */
    public void deleteFile(@NonNull File fileName, String carpeta) {

        FTPClient client = new FTPClient();
        try {
            client.connect(FTP_HOST, 21);
            client.login(FTP_USER, FTP_PASS);
            client.setType(FTPClient.TYPE_BINARY);
            client.changeDirectory("/web/uploads/");

            if (comprobarCarpeta(client, carpeta)) {

                client.changeDirectory("/web/uploads/" + carpeta);
                client.deleteFile(fileName.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

}
