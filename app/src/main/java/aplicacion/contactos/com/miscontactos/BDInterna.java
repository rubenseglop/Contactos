package aplicacion.contactos.com.miscontactos;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;
import java.util.ArrayList;

public class BDInterna extends SQLiteOpenHelper {

    private static final int VERSION_BASEDATOS = 1;
    private static final String NOMBRE_BASEDATOS = "BDinterna.db";

    // Creacion de tabla para la BDen formato SQL en caso de no tenerla
    private static final String TABLA_USUARIOS =
            "CREATE TABLE USUARIOS (" +
                    "ID VARCHAR(20) PRIMARY KEY," +
                    "NOMBRE VARCHAR(100)," +
                    "APELLIDOS VARCHAR(100)," +
                    "DOMICILIO VARCHAR(100)," +
                    "TELEFONO VARCHAR(20)," +
                    "EMAIL VARCHAR(100)" +
                    ");";

    ArrayList<Contacto> contactos = new ArrayList<>();

    // Constructor de la clase
    public BDInterna(Context context) {
        super(context, NOMBRE_BASEDATOS, null, VERSION_BASEDATOS);

        // Recorro todos los contactos de mi SQLite y los guardo en un ArrayList de Contacto
        Cursor bus;
        for (int i = 0; i < numerodeFilas(); i++) {
            System.out.println("FILAS:" + i);
            bus = busquedaContacto(Integer.toString(i));

            if (bus.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros (creo POJOS)
                do {
                    int id= bus.getInt(0);
                    String nombre = bus.getString(1);
                    String apellidos = bus.getString(2);
                    String domicilio = bus.getString(3);
                    String telefono = bus.getString(4);
                    String email = bus.getString(5);

                    contactos.add(new Contacto(id,nombre,apellidos,domicilio,telefono,email));
                } while(bus.moveToNext());
            }




            //num = c.getString(c.getColumnIndex("_id"));

            //num = c.getString(c.getColumnIndex("_id"));

//            contactos.add(new Contacto(
//                    numerodeFilas(),
//                    bus.getString(bus.getColumnIndex("NOMBRE")),
//                    bus.getString(bus.getColumnIndex("APELLIDOS")),
//                    bus.getString(bus.getColumnIndex("DOMICILIO")),
//                    bus.getString(bus.getColumnIndex("TELEFONO")),
//                    bus.getString(bus.getColumnIndex("EMAIL")))
//            );
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLA_USUARIOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS USUARIOS");
        onCreate(db);
    }

    // Inserta un contactos a la BD
    public void insertarContacto(String nombre, String apellidos, String domicilio, String telefono, String email) {

        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            // Creamos el registro a insertar
            ContentValues valores = new ContentValues();
            int id = numerodeFilas();
            valores.put("ID", id);
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
    }



    // Da unos valores a la clase de Contacto


    // Devuelve datos de un Contacto dado un nombre, los devuelve de forma ordenada por nombre
    public Cursor busquedaContacto(String id) {

        SQLiteDatabase db = getReadableDatabase();

        String[] valores_recuperar = {"ID", "NOMBRE", "APELLIDOS", "DOMICILIO", "TELEFONO","EMAIL"};

        String[] args = new String[] {id};

        Cursor c = db.query("USUARIOS", valores_recuperar, "ID=?", args, null, null,
                null,null);
        return c;



    }

    // Devuelve un cursor con todos los atributos elegidos de la BD para ponerlos en la Lista
    public Cursor recuperarCursordeContactos() {

        SQLiteDatabase db = getWritableDatabase();

        String[] valores_recuperar = {"ID", "NOMBRE", "APELLIDOS", "DOMICILIO", "TELEFONO","EMAIL"};

        // Ordena al recuperarlos
        Cursor c = db.query("USUARIOS", valores_recuperar,null,null,null,null,"nombre ASC",null);

        return c;
    }

    // Devuelve el numero de filas de la tabla
    public int numerodeFilas(){
        int dato= (int) DatabaseUtils.queryNumEntries(this.getWritableDatabase(), "USUARIOS");
        return dato;
    }

    // Guarda en un array de IDs todos los ID de la tabla contactos
    public int [] recuperaIds(){
        int [] datosId;
        int i;

        SQLiteDatabase db = getReadableDatabase();
        String[] valores_recuperar = {"_id"};

        // Cuando recupera los IDS lo tiene que hacer ordenado por el nombre como la lista
        Cursor cursor = db.query("contactos",valores_recuperar,null,null,null,null,"nombre ASC",null);

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

}