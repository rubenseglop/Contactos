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

    private static String uniqueID = UUID.randomUUID().toString(); // creo un identificador unico
    private static final int VERSION_BASEDATOS = 1;
    private static final String NOMBRE_BASEDATOS = "BDinterna.db";

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
                    "ID INTEGER(6) PRIMARY KEY," +
                    "URL VARCHAR(200));";
    private static final String TABLA_DOMICILIO =
            "CREATE TABLE DOMICILIO(" +
                    "ID INTEGER(6) PRIMARY KEY," +
                    "DIRECCION VARCHAR(200))";
    private static final String TABLA_TELEFONO =
            "CREATE TABLE TELEFONO(" +
                    "ID INTEGER(6) PRIMARY KEY," +
                    "NUMERO VARCHAR(15))";

    ArrayList<Contacto> contactos = new ArrayList<>();

    // Constructor de la clase
    public BDInterna(Context context) {
        super(context, NOMBRE_BASEDATOS, null, VERSION_BASEDATOS);

        // Recorro todos los contactos de mi SQLite y los guardo en un ArrayList de Contacto
        //actualizaContactos();
    }

    public void actualizaContactos() {
        Cursor bus;
        contactos.clear();
        int [] datosId = recuperaIds();

        for (int i = 0; i < numerodeFilas(); i++) {

            bus = busquedaContacto(Integer.toString(datosId[i]));

            if (bus.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros (creo POJOS)

                String foto;
                String nombre;
                String apellidos;
                int galeria_id;
                String domicilio_id;
                String telefono_id;
                String email;

                do {
                    int id = bus.getInt(0);
                    foto = bus.getString(1);
                    nombre = bus.getString(2);
                    apellidos = bus.getString(3);
                    galeria_id = bus.getInt(4);
                    domicilio_id = bus.getString(5);
                    telefono_id = bus.getString(6);
                    email = bus.getString(7);

                    contactos.add(new Contacto(id,foto,nombre,apellidos,domicilio_id,telefono_id,email)); //TODO ¡¡aviso aqui no pongo la galeria!!

                } while(bus.moveToNext());
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DEBUG LA ABUELA FUMA PORROS");

        db.execSQL(TABLA_GALERIA);
        db.execSQL(TABLA_DOMICILIO);
        db.execSQL(TABLA_TELEFONO);
        db.execSQL(TABLA_USUARIOS);
        db.execSQL(UNIQUE_UUID);
        //insertarUUID(); // aqui lo petamos

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS USUARIOS");
        onCreate(db);
    }

    // Inserta el identificador unico a la BD
    public void insertarUUID() {

        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery("SELECT ID FROM UNIQUE_UUID", null);

        System.out.println("DEBUG UUID");
        String id = "";
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                id = c.getString(0);
                if (id != null) { uniqueID = id;}
            } while(c.moveToNext());
        }


        if (id.length() == 0) {

            if (db != null) {
                // Creamos el registro a insertar
                ContentValues valores = new ContentValues();
                valores.put("ID", uniqueID);

                //insertamos el registro en la Base de Datos
                db.insert("UNIQUE_UUID", null, valores);
            }


        }
        db.close();
    }
    public String getUniqueID(){
        return uniqueID;
    }

    // Inserta un contactos a la BD
    // TODO AÑADIR LA GALERIAID Y EL RESTO QUE HE MODIFICADO
    public void insertarContacto(String foto, String nombre, String apellidos, String domicilio, String telefono, String email, String uuid) {

        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            // Creamos el registro a insertar
            ContentValues valores = new ContentValues();
            int id = ultimo_id("USUARIOS");
            valores.put("ID", id);
            valores.put("FOTO", foto);
            valores.put("NOMBRE", nombre);
            valores.put("APELLIDOS", apellidos);
            valores.put("DOMICILIO", domicilio);
            valores.put("TELEFONO", telefono);
            valores.put("EMAIL", email);

            //insertamos el registro en la Base de Datos
            db.insert("USUARIOS", null, valores);
        }

        db.close();
    }


    // SOBRECARGA DE METODO - Inserta un contactos a la BD (restauracion con ID)
    public void insertarContacto(String id, String foto, String nombre, String apellidos, String domicilio, String telefono, String email, String uuid) {

        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            // Creamos el registro a insertar
            ContentValues valores = new ContentValues();
            valores.put("ID", id);
            valores.put("FOTO", foto);
            valores.put("NOMBRE", nombre);
            valores.put("APELLIDOS", apellidos);
            valores.put("DOMICILIO", domicilio);
            valores.put("TELEFONO", telefono);
            valores.put("EMAIL", email);

            //insertamos el registro en la Base de Datos
            db.insert("USUARIOS", null, valores);
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

        db.delete("USUARIOS", "ID=" + id, null);

        db.close();
        actualizaContactos();
    }



    // ultimo id
    public int ultimo_id(String usuario) {

        SQLiteDatabase db = getReadableDatabase();

        String[] valores_recuperar = {"ID"};

        /*Cursor c = db.query("USUARIOS", valores_recuperar, null, null, null, null,
                null,null);*/

        Cursor c = db.rawQuery("SELECT MAX(ID) FROM " + usuario, null);
        c.moveToFirst();
        return c.getInt(0)+1;

    }
    public Cursor todoContacto(String id) {

        SQLiteDatabase db = getReadableDatabase();

        String[] valores_recuperar = {"ID", "NOMBRE", "APELLIDOS", "DOMICILIO", "TELEFONO","EMAIL"};

        String[] args = new String[] {id};

        Cursor c = db.query("USUARIOS", valores_recuperar, "*", null, null,
                "NOMBRE ASC",null);
        return c;

    }


    // Devuelve datos de un Contacto dado un nombre, los devuelve de forma ordenada por nombre
    public Cursor busquedaContacto(String id) {

        SQLiteDatabase db = getReadableDatabase();

        String[] valores_recuperar = {"ID", "FOTO", "NOMBRE", "APELLIDOS", "DOMICILIO", "TELEFONO","EMAIL"};

        String[] args = new String[] {id};

        Cursor c = db.query("USUARIOS", valores_recuperar, "ID=?", args, null, null,
                "NOMBRE ASC",null);
        return c;
    }

    // Devuelve un cursor con todos los atributos elegidos de la BD para ponerlos en la Lista
    public Cursor recuperarCursordeContactos() {

        SQLiteDatabase db = getWritableDatabase();

        String[] valores_recuperar = {"ID", "FOTO", "NOMBRE", "APELLIDOS", "DOMICILIO", "TELEFONO","EMAIL"};

        // Ordena al recuperarlos
        Cursor c = db.query("USUARIOS", valores_recuperar,null,null,null,null,"nombre ASC",null);


        return c;
    }

    // Devuelve el numero de filas de la tabla
    public int numerodeFilas(){
        int dato= (int) DatabaseUtils.queryNumEntries(this.getWritableDatabase(), "USUARIOS");

        return dato;
    }

    // Guarda en un array de IDs todos los ID de la tabla
    public int [] recuperaIds(){
        int [] datosId;
        int i;

        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"ID"};

        // Cuando recupera los IDS lo tiene que hacer ordenado por el nombre como la lista
        Cursor cursor = db.query("USUARIOS",valores_recuperar,null,null,null,null,"nombre ASC",null);

        if (cursor.getCount()>0){
            datosId= new int[cursor.getCount()];
            i=0;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                datosId[i] = cursor.getInt(0);
                i++;
                cursor.moveToNext();
            }
        }
        else datosId = new int [0];
        cursor.close();
        return datosId;
    }

    public ArrayList<Contacto> devuelveContactos(){
        return contactos;
    }

    public int devuelvoIDborrado(int id){
        int [] datosId = recuperaIds();

        return datosId[id];
    }
}
