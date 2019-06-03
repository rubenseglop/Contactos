package aplicacion.contactos.com.miscontactos;

import android.content.Context;
import java.io.File;
import java.io.IOException;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

public class MetodoFTP {

    static final String FTP_HOST = BDExternaLinks.FTP_HOST;
    static final String FTP_USER = BDExternaLinks.FTP_USER;
    static final String FTP_PASS = BDExternaLinks.FTP_PASS;
    static Context mContext;

    public MetodoFTP(Context mContext) {
        this.mContext = mContext;
    }

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



    private boolean comprobarCarpeta(FTPClient client, String carpeta) throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException {
        Boolean result = false;
        FTPFile[] list = client.list();
        for (int i = 0; i < list.length; i++) {
            if (list[i].getName().equals(carpeta) && list[i].getSize() == 0 && list[i].getType() == 1) {
                result = true;
            }

        }
        return result;
    }

    public void deleteFile(File fileName, String carpeta) {

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
