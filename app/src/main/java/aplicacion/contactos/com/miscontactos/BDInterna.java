package aplicacion.contactos.com.miscontactos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.UUID;

public class BDInterna extends SQLiteOpenHelper {

    private static String uniqueID;
    private static final int VERSION_BASEDATOS = 1;
    private static final String NOMBRE_BASEDATOS = "BDinterna.db";
    private int [] datosId; // Array con el número de tuplas (de contactos, de galeria, domicilio, etc...)

    // Creación de tabla para la BD en formato SQL en caso de no tenerla
    private static final String UNIQUE_UUID =
            "CREATE TABLE UNIQUE_UUID(ID VARCHAR(30));";
    private static final String TABLA_CONTACTOS =
            "CREATE TABLE CONTACTOS (" +
                    "ID INTEGER PRIMARY KEY autoincrement," +
                    "FOTO VARCHAR(400)," +
                    "NOMBRE VARCHAR(100)," +
                    "APELLIDOS VARCHAR(100)," +
                    "GALERIA_ID INTEGER(6)," +
                    "DOMICILIO_ID INTEGER(6)," +
                    "TELEFONO_ID INTEGER(6)," +
                    "EMAIL VARCHAR(100)" +
                    ");";
    private static final String TABLA_GALERIA =
            "CREATE TABLE GALERIA (" +
                    "ID INTEGER(6)," +
                    "PATH VARCHAR(300)," +
                    "PRIMARY KEY (ID, PATH)," +
                    "FOREIGN KEY (ID) REFERENCES USUARIO(GALERIA_ID) ON DELETE CASCADE);";
    private static final String TABLA_DOMICILIO =
            "CREATE TABLE DOMICILIO(" +
                    "ID INTEGER(6)," +
                    "DIRECCION VARCHAR(200),"+
                    "FOREIGN KEY (ID) REFERENCES USUARIO(DOMICILIO_ID) ON DELETE CASCADE);";
    private static final String TABLA_TELEFONO =
            "CREATE TABLE TELEFONO(" +
                    "ID INTEGER(6)," +
                    "NUMERO VARCHAR(15)," +
                    "FOREIGN KEY (ID) REFERENCES USUARIO(TELEFONO_ID) ON DELETE CASCADE);";

    public ArrayList<Contacto> contactos = new ArrayList<>();

    // Constructor de la clase
    public BDInterna(Context context) {
        super(context, NOMBRE_BASEDATOS, null, VERSION_BASEDATOS);
        uniqueID=null;
    }

    /**
     * Lee todos los contactos de la BDInterna y los guarda en ArrayLists de Contactos
     * @param orderby Columna de la tabla CONTACTOS a ordenar
     * @param order Debes indicar si es ASC o DESC
     */
    public void actualizaContactos(String orderby, String order) {
        Cursor cContactos;
        //limpio todos los ArrayLists
        contactos.clear();

        datosId = recuperaIds("CONTACTOS", null);  // recorro todos los ID's de Usuario y guardo los ID's en un array (datosID)
        int id;
        //Leo todos los contactos leyendo desde la ID de datosID
        for (int i = 0; i < datosId.length; i++) {
            cContactos = busquedaContacto(Integer.toString(datosId[i]));
            cContactos.moveToNext();
            ArrayList<Galeria> tempoGal = new ArrayList<>();
            ArrayList<Domicilio> tempoDom = new ArrayList<>();
            ArrayList<Telefono> tempoTel = new ArrayList<>();

            //Recorremos el cursor hasta que no haya más registros (creo POJOS)
            String foto, nombre, apellidos, email;
            int galeria_id, domicilio_id, telefono_id;
            do {
                id = cContactos.getInt(0);
                foto = cContactos.getString(1);  // Esta es la foto interna
                nombre = cContactos.getString(2);
                apellidos = cContactos.getString(3);
                galeria_id = cContactos.getInt(4);
                domicilio_id = cContactos.getInt(5);
                telefono_id = cContactos.getInt(6);
                email = cContactos.getString(7);

                //Meto en un ArrayList las galerias con la ID del contacto
                Cursor cContactosG = busquedaGaleria(Integer.toString(galeria_id));
                if (cContactosG.moveToFirst()) {
                    //Recorremos el cursor hasta que no haya más registros (creo POJOS)
                    int indice = 0;
                    do{
                        String result = cContactosG.getString(1);
                        if(result.length()==0){result ="";}
                        tempoGal.add(indice, new Galeria(cContactosG.getInt(0), result));
                        indice++;
                    }while (cContactosG.moveToNext());
                }

                //Meto en un ArrayList los domicilios con la ID del contacto
                Cursor cContactosD = busquedaDomicilio(Integer.toString(domicilio_id));
                if (cContactosD.moveToFirst()) {
                    //Recorremos el cursor hasta que no haya más registros (creo POJOS)
                    int indice = 0;
                    do{
                        String result = cContactosD.getString(1);
                        if(result.length()==0 || result.isEmpty() || result == null){result = " ";}
                        tempoDom.add(indice, new Domicilio(cContactosD.getInt(0), result));
                        indice++;
                    }while (cContactosD.moveToNext());
                }

                //Meto en un ArrayList los telefono con la ID del contacto
                Cursor cContactosT = busquedaTelefono(Integer.toString(telefono_id));
                if (cContactosT.moveToFirst()) {
                    //Recorremos el cursor hasta que no haya más registros (creo POJOS)
                    int indice = 0;
                    do {
                        String result = cContactosT.getString(1);
                        if(result.length()==0 || result.isEmpty() || result == null){result = " ";}
                        tempoTel.add(indice, new Telefono(cContactosT.getInt(0), result));
                        indice++;
                    }while (cContactosT.moveToNext());
                }
            } while (cContactos.moveToNext());

            contactos.add(new Contacto(id, foto, nombre, apellidos, galeria_id, domicilio_id, telefono_id, email, tempoGal, tempoDom, tempoTel));
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creamos todas las tablas internas en el caso de no existir
        db.execSQL(TABLA_GALERIA);
        db.execSQL(TABLA_DOMICILIO);
        db.execSQL(TABLA_TELEFONO);
        db.execSQL(TABLA_CONTACTOS);
        db.execSQL(UNIQUE_UUID);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CONTACTOS");
        onCreate(db);
    }

    /**
     * Inserta el identificador unico a la BD y devuelve true si tenemos una UUID almacenada
     */
    public boolean hayUUID() {

        SQLiteDatabase db = getWritableDatabase();
        Boolean result = false;
        //Leemos la Tabla UUID
        Cursor c = db.rawQuery("SELECT ID FROM UNIQUE_UUID", null);
        String id = "";
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                id = c.getString(0);
                if (id != null) {
                    uniqueID = id;
                    result = true;
                }
            } while(c.moveToNext());
        }

        db.close();
        return result;
    }

    public void crearUUID() {
        SQLiteDatabase db = getWritableDatabase();
        //Leemos la Tabla UUID
        Cursor c = db.rawQuery("SELECT ID FROM UNIQUE_UUID", null);
        //En el caso de no tener una UUID, creamos uno nuevo aleatoriamente
        String id = "";
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                id = c.getString(0);
                if (id != null) {
                    uniqueID = id;
                }
            } while (c.moveToNext());
        }

        if (db != null) {
            // Creamos el registro a insertarContacto
            uniqueID = UUID.randomUUID().toString(); // creo un identificador unico
            ContentValues valores = new ContentValues();
            valores.put("ID", uniqueID);
            //insertamos el registro en la Base de Datos
            db.insert("UNIQUE_UUID", null, valores);
        }

        db.close();
    }

    public void crearUUID(String uuid) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("UNIQUE_UUID", null, null);

        if (db != null) {
            // Creamos el registro a insertarContacto
            uniqueID = uuid;
            ContentValues valores = new ContentValues();
            valores.put("ID", uniqueID);
            //insertamos el registro en la Base de Datos
            db.insert("UNIQUE_UUID", null, valores);
        }

        db.close();
    }



    /**
     * Método que lee el UUID almacenado
     * @return Devuelvo el UUID
     */
    static String getUniqueID(){
        return uniqueID;
    }

    /**
     * Inserta un contactos a la BD
     * @param foto
     * @param nombre
     * @param apellidos
     * @param galeria_id
     * @param domicilio_id
     * @param telefono_id
     * @param email
     * @param uuid
     */
    public void insertarContacto(String foto, String nombre, String apellidos, int galeria_id, int domicilio_id, int telefono_id, String email, String uuid) {

        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            // Creamos el registro a insertarContacto
            ContentValues valores = new ContentValues();
            int id = ultimo_id("CONTACTOS");
            valores.put("ID", id);
            valores.put("FOTO", foto);
            valores.put("NOMBRE", nombre);
            valores.put("APELLIDOS", apellidos);
            valores.put("GALERIA_ID", galeria_id);
            valores.put("DOMICILIO_ID", domicilio_id);
            valores.put("TELEFONO_ID", telefono_id);
            valores.put("EMAIL", email);

            //insertamos el registro en la Base de Datos
            db.insert("CONTACTOS", null, valores);
        }
        db.close();
    }

    /**
     * SOBRECARGA DE METODO - Inserta un contactos a la BD (restauracion con ID para la BDExterna)
     * Usado para insertarlo dada la ID
     * @param id
     * @param foto
     * @param nombre
     * @param apellidos
     * @param galeria_id
     * @param domicilio_id
     * @param telefono_id
     * @param email
     * @param uuid
     */
    public void insertarContacto(int id, String foto, String nombre, String apellidos, int galeria_id, int domicilio_id, int telefono_id, String email, String uuid) {

        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            // Creamos el registro a insertarContacto
            ContentValues valores = new ContentValues();
            valores.put("ID", id);
            valores.put("FOTO", foto);
            valores.put("NOMBRE", nombre);
            valores.put("APELLIDOS", apellidos);
            valores.put("GALERIA_ID", galeria_id);
            valores.put("DOMICILIO_ID", domicilio_id);
            valores.put("TELEFONO_ID", telefono_id);
            valores.put("EMAIL", email);

            //insertamos el registro en la Base de Datos
            db.insert("CONTACTOS", null, valores);
        }

        db.close();
    }

    /**
     * Inserta una galeria en la BD
     * @param id
     * @param path
     */
    public void insertarGaleria(int id, String path) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            // Creamos el registro a insertarContacto
            ContentValues valores = new ContentValues();
            valores.put("ID", id);
            valores.put("PATH", path);
            //insertamos el registro en la Base de Datos
            db.insert("GALERIA", null, valores);
        }
        db.close();
    }

    /**
     * Inserta un domicilio en la BD
     * @param id
     * @param domicilio
     */
    public void insertarDomicilio(int id, String domicilio) {

        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            // Creamos el registro a insertarContacto
            ContentValues valores = new ContentValues();
            valores.put("ID", id);
            valores.put("DIRECCION", domicilio);

            //insertamos el registro en la Base de Datos
            db.insert("DOMICILIO", null, valores);
        }

        db.close();
    }

    /**
     * Inserta un teléfono en la BD
     * @param id
     * @param telefono
     */
    public void insertarTelefono(int id, String telefono) {

        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            // Creamos el registro a insertarContacto
            ContentValues valores = new ContentValues();
            valores.put("ID", id);
            valores.put("NUMERO", telefono);

            //insertamos el registro en la Base de Datos
            db.insert("TELEFONO", null, valores);
        }
        db.close();
    }

    /**
     * Modifica un contacto del a BD
     * @param id
     * @param nombre
     * @param direccion
     * @param movil
     * @param mail
     */
    public void modificarContacto(int id, String nombre, String direccion,String movil,String mail){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("direccion", direccion);
        valores.put("movil", movil);
        valores.put("mail", mail);

        db.update("contactos", valores, "_id=" + id, null);

        db.close();
    }

    /**
     * Borra un contacto de la BD dado un id
     * @param id
     */
    public void borraContacto(int id) {

        // Van con ON DELETE CASCADE, pero por si acaso borro una a una todas las tablas
        SQLiteDatabase db = getWritableDatabase();
        db.delete("GALERIA", "ID=" + id, null);
        db.delete("DOMICILIO", "ID=" + id, null);
        db.delete("TELEFONO", "ID=" + id, null);
        db.delete("CONTACTOS", "ID=" + id, null);
        db.close();
    }

    public void borraGaleria( String path) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("GALERIA",  "PATH='" + path+"'", null);
        db.close();
    }

    /**
     * Método que elimina todas las tablas de la base de datos (excepto la del UUID)
     */
    public void borrarTodo() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("GALERIA", null, null);
        db.delete("DOMICILIO", null, null);
        db.delete("TELEFONO", null, null);
        db.delete("CONTACTOS", null, null);
        db.close();
    }

    /**
     * Método que devuelve el último ID de la tabla espeficicada
     * @param tabla
     * @return
     */
    public int ultimo_id(String tabla) {
        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"ID"};
        /*Cursor c = db.query("USUARIOS", valores_recuperar, null, null, null, null,
                null,null);*/
        Cursor c = db.rawQuery("SELECT MAX(ID) FROM " + tabla, null);
        c.moveToFirst();
        return c.getInt(0)+1;
    }

    /**
     * Busca el contacto con la ID proporcionada
     * @param id
     * @return Devuelve un cursor con el resultado
     */
    public Cursor busquedaContacto(String id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"ID", "FOTO", "NOMBRE", "APELLIDOS", "GALERIA_ID", "DOMICILIO_ID", "TELEFONO_ID", "EMAIL"};
        String[] args = new String[] {id};
        Cursor c = db.query("CONTACTOS", valores_recuperar, "ID=?", args, null, null,
                "NOMBRE ASC",null);
        return c;
    }

    /**
     * Devuelve datos de un Contacto dado un nombre, los devuelve de forma ordenada por nombre
     * @param id
     * @return
     */
    public Cursor busquedaGaleria(String id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"ID", "PATH"};
        String[] args = new String[] {id};
        Cursor c = db.query("GALERIA", valores_recuperar, "ID=?", args, null, null,
                null,null);
        return c;
    }

    /**
     * Método que busca en la tabla Domicilio todas las tuplas
     * @param id
     * @return Devuelve un cursor con el contenido del mismo
     */
    public Cursor busquedaDomicilio(String id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"ID", "DIRECCION"};
        String[] args = new String[] {id};
        Cursor c = db.query("DOMICILIO", valores_recuperar, "ID=?", args, null, null,
                null,null);
        return c;
    }
    public Cursor busquedaTelefono(String id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"ID", "NUMERO"};
        String[] args = new String[] {id};
        Cursor c = db.query("TELEFONO", valores_recuperar, "ID=?", args, null, null,
                null,null);
        return c;
    }


    // Devuelve el numero de filas de la tabla
    public int numerodeFilas(String tabla){
        int dato= (int) DatabaseUtils.queryNumEntries(this.getWritableDatabase(), tabla);
        //System.out.println("DEBUG FILAS " +tabla+" " + dato);
        return dato;
    }

    /**
     * Método que nos devuelve la cantidad de tuplas de una tabla (para posteriormente recorrerla)
     * @param tabla
     * @param orderby
     * @return Array de int[] con los numeros de ID (con .length sabemos su cantidad)
     */
    public int [] recuperaIds(String tabla, String orderby) {
        int[] datosId= null;
        int i;
        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"ID"};
        Cursor cursor = db.query(tabla, valores_recuperar, null,null, null, null, orderby, null);
        if (cursor.getCount() > 0) {
            datosId = new int[cursor.getCount()];
            i = 0;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                datosId[i] = cursor.getInt(0);
                i++;
                cursor.moveToNext();
            }
        } else datosId = new int[0];
        cursor.close();
        return datosId;
    }

    /**
     * Método getter que devuelve el ArrayList de contactos
     * @return
     */
    public ArrayList<Contacto> devuelveContactos() {
        return contactos;
    }

    public static void clearUUID(){
        uniqueID = null;
    }

}
