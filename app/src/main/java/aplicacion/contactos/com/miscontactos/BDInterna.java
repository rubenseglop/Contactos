package aplicacion.contactos.com.miscontactos;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class BDInterna extends SQLiteOpenHelper {

    private static String uniqueID;
    private static final int VERSION_BASEDATOS = 1;
    private static final String NOMBRE_BASEDATOS = "BDinterna.db";
    private int [] datosId; // Array con el número de tuplas (de contactos, de galeria, domicilio, etc...)
    List<Contacto> list = new ArrayList();

    // Creación de tabla para la BD en formato SQL en caso de no tenerla
    private static final String UNIQUE_UUID =
            "CREATE TABLE UNIQUE_UUID(ID VARCHAR(30));";
    private static final String TABLA_USUARIOS =
            "CREATE TABLE USUARIOS (" +
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

    ArrayList<Contacto> contactos = new ArrayList<>();
    ArrayList<Galeria> galerias = new ArrayList<>();

    // Constructor de la clase
    public BDInterna(Context context) {
        super(context, NOMBRE_BASEDATOS, null, VERSION_BASEDATOS);
    }

    /**
     * Lee todos los contactos de la BDInterna y los guarda en ArrayLists de Contactos
     * @param orderby Columna de la tabla USUARIOS a ordenar
     * @param order Debes indicar si es ASC o DESC
     */
    public void actualizaContactos(String orderby, String order) {
        Cursor cContactos;
        //limpio todos los ArrayLists
        contactos.clear();

        datosId = recuperaIds("USUARIOS", null);  // recorro todos los ID's de Usuario y guardo los ID's en un array (datosID)
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
                //int[] datosIdDom = recuperaIds("DOMICILIO", null);  // recorro todos los ID's de Domicilio y guardo los ID's en un array (datosIdDom)

                Cursor cContactosD = busquedaDomicilio(Integer.toString(domicilio_id));
                if (cContactosD.moveToFirst()) {
                    //Recorremos el cursor hasta que no haya más registros (creo POJOS)
                    int indice = 0;
                    do{
                        String result = cContactosD.getString(1);
                        if(result.length()==0){result ="";}
                        tempoDom.add(indice, new Domicilio(cContactosD.getInt(0), result));
                        indice++;
                    }while (cContactosD.moveToNext());
                }

                //Meto en un ArrayList los telefono con la ID del contacto
                //int[] datosIdTel = recuperaIds("TELEFONO", null);  // recorro todos los ID's de Domicilio y guardo los ID's en un array (datosIdDom)

                Cursor cContactosT = busquedaTelefono(Integer.toString(telefono_id));
                if (cContactosT.moveToFirst()) {
                    //Recorremos el cursor hasta que no haya más registros (creo POJOS)
                    int indice = 0;

                    do {
                        String result = cContactosT.getString(1);
                        if(result.length()==0){result ="";}
                        tempoTel.add(indice, new Telefono(cContactosT.getInt(0), result));
                        System.out.println("DEBUG CONTAC " + i + "TEL " + cContactosT.getString(1));
                        indice++;
                    }while (cContactosT.moveToNext());
                    System.out.println("DEBUG CONTAC TELEFO " + tempoTel.size() + " "+ tempoTel);
                }
            } while (cContactos.moveToNext());

            //Leidas todas las tablas, relacionamos las ID's (FOREIGN KEY)

            contactos.add(new Contacto(id, foto, nombre, apellidos, galeria_id, domicilio_id, telefono_id, email, tempoGal, tempoDom, tempoTel));
        }


        //Ordenar des_contactos
        Collections.sort(contactos, new Comparator<Contacto>() {
            @Override
            public int compare(Contacto p1, Contacto p2) {
                if (orderby.equals("NOMBRE")) {
                    System.out.println("DEBUG ORDER NOMBRE");
                    return new String(p1.getNombre()).compareTo(new String(p2.getNombre()));
                }
                if (orderby.equals("APELLIDOS")) {
                    System.out.println("DEBUG ORDER APELLIDOS");
                    return new String(p1.getApellidos()).compareTo(new String(p2.getApellidos()));
                }
                if (orderby.equals("DOMICILIO")) {
                    System.out.println("DEBUG ORDER DOMICILIO");

                    try {
                        return new String(p1.getDomicilios().get(0).getDireccion()).compareTo(new String(p2.getDomicilios().get(0).getDireccion()));
                    } catch (Exception e) {
                    } finally {
                        return 0;
                    }

                }
                if (orderby.equals("TELEFONO")) {
                    System.out.println("DEBUG ORDER TELEFONO");
                    try {
                        return new String(p1.getTelefonos().get(0).getNumero()).compareTo(new String(p2.getTelefonos().get(0).getNumero()));
                    } catch (Exception e) {
                    } finally {
                        return 0;
                    }
                }
                return 0;
            }
        });
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creamos todas las tablas internas en el caso de no existir
        db.execSQL(TABLA_GALERIA);
        db.execSQL(TABLA_DOMICILIO);
        db.execSQL(TABLA_TELEFONO);
        db.execSQL(TABLA_USUARIOS);
        db.execSQL(UNIQUE_UUID);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS USUARIOS");
        onCreate(db);
    }

    // Inserta el identificador unico a la BD
    public void insertarUUID() {

        SQLiteDatabase db = getWritableDatabase();

        //Leemos la Tabla UUID
        Cursor c = db.rawQuery("SELECT ID FROM UNIQUE_UUID", null);
        String id = "";
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                id = c.getString(0);
                if (id != null) { uniqueID = id;}
            } while(c.moveToNext());
        }

        //En el caso de no tener una UUID, creamos uno nuevo aleatoriamente
        if (id.length() == 0) {
            if (db != null) {
                // Creamos el registro a insertarContacto
                uniqueID = UUID.randomUUID().toString(); // creo un identificador unico
                ContentValues valores = new ContentValues();
                valores.put("ID", uniqueID);
                //insertamos el registro en la Base de Datos
                db.insert("UNIQUE_UUID", null, valores);
            }
        }
        db.close();
    }

    /**
     * Método que lee el UUID almacenado
     * @return Devuelvo el UUID
     */
    public String getUniqueID(){
        return uniqueID;
    }

    // Inserta un contactos a la BD
    public void insertarContacto(String foto, String nombre, String apellidos, int galeria_id, int domicilio_id, int telefono_id, String email, String uuid) {

        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            // Creamos el registro a insertarContacto
            ContentValues valores = new ContentValues();
            int id = ultimo_id("USUARIOS");
            valores.put("ID", id);
            valores.put("FOTO", foto);
            valores.put("NOMBRE", nombre);
            valores.put("APELLIDOS", apellidos);
            valores.put("GALERIA_ID", galeria_id);
            valores.put("DOMICILIO_ID", domicilio_id);
            valores.put("TELEFONO_ID", telefono_id);
            valores.put("EMAIL", email);

            //insertamos el registro en la Base de Datos
            db.insert("USUARIOS", null, valores);
        }

        db.close();
    }
    // SOBRECARGA DE METODO - Inserta un contactos a la BD (restauracion con ID para la BDExterna)
    public void insertarContacto(String id, String foto, String nombre, String apellidos, String galeria_id, String domicilio_id, String telefono_id, String email, String uuid) {

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
            db.insert("USUARIOS", null, valores);
        }

        db.close();
    }

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


    // Modifica un contacto del a BD
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

    // Borra un contacto de la BD dado un id
    public void borraContacto(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("GALERIA", "ID=" + id, null);
        db.delete("DOMICILIO", "ID=" + id, null);
        db.delete("TELEFONO", "ID=" + id, null);
        db.delete("USUARIOS", "ID=" + id, null);
        db.close();
    }

    public void borrarTodo() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("GALERIA", null, null);
        db.delete("DOMICILIO", null, null);
        db.delete("TELEFONO", null, null);
        db.delete("USUARIOS", null, null);
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

    public Cursor todoContacto(String id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"ID", "NOMBRE", "APELLIDOS", "DOMICILIO_ID", "TELEFONO_ID","EMAIL"};
        String[] args = new String[] {id};
        Cursor c = db.query("USUARIOS", valores_recuperar, "*", null, null,
                "NOMBRE ASC",null);
        return c;
    }


    // Devuelve datos de un Contacto dado un nombre, los devuelve de forma ordenada por nombre
    public Cursor busquedaContacto(String id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"ID", "FOTO", "NOMBRE", "APELLIDOS", "GALERIA_ID", "DOMICILIO_ID", "TELEFONO_ID", "EMAIL"};
        String[] args = new String[] {id};
        Cursor c = db.query("USUARIOS", valores_recuperar, "ID=?", args, null, null,
                "NOMBRE ASC",null);
        return c;
    }
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

    // Devuelve un cursor con todos los atributos elegidos de la BD para ponerlos en la Lista
    public Cursor recuperarCursordeContactos() {
        SQLiteDatabase db = getWritableDatabase();
        String[] valores_recuperar = {"ID", "FOTO", "NOMBRE", "APELLIDOS", "GALERIA_ID", "DOMICILIO_ID", "TELEFONO_ID", "EMAIL"};
        // Ordena al recuperarlos
        Cursor c = db.query("USUARIOS", valores_recuperar,null,null,null,null,"nombre ASC",null);
        return c;
    }

    // Devuelve el numero de filas de la tabla
    public int numerodeFilas(String tabla){
        int dato= (int) DatabaseUtils.queryNumEntries(this.getWritableDatabase(), tabla);
        System.out.println("DEBUG FILAS " +tabla+" " + dato);
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
        Cursor cursor;

        // Cuando recupera los IDS lo tiene que hacer ordenado por el nombre como la lista
        /*cursor = db.rawQuery("SELECT * FROM USUARIOS U " +
                "JOIN GALERIA G ON G.ID = U.GALERIA_ID " +
                "JOIN DOMICILIO D ON D.ID = U.DOMICILIO_ID " +
                "JOIN TELEFONO T ON T.ID = U.TELEFONO_ID " +
                "ORDER BY DIRECCION", null);*/
        cursor = db.query(tabla, valores_recuperar, null,null, null, null, orderby, null);
        orderby = null;
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

    public ArrayList<Contacto> devuelveContactos() {
        return contactos;
    }

    public int devuelvoIDborrado(int id){
        int [] datosId = recuperaIds("USUARIOS", "nombre ASC");
        return datosId[id];
    }


}
