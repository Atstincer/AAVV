package com.example.usuario.aavv.Almacenamiento;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by usuario on 29/10/2021.
 */

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String BD_NAME = "MiBD";
    public static final int BD_VERSION = 10;
    private static AdminSQLiteOpenHelper instancia;

    private AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static AdminSQLiteOpenHelper getInstance(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        if(instancia==null){
            instancia = new AdminSQLiteOpenHelper(context,name,factory,version);
        }
        return instancia;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onCreate(SQLiteDatabase bd) {

        bd.execSQL("CREATE TABLE Touroperadores(" +
                "id INTEGER PRIMARY KEY," +
                "nombre TEXT)");

        bd.execSQL("CREATE TABLE Reservas(" +
                "id INTEGER PRIMARY KEY," +
                "TE TEXT," +
                "excursion TEXT," +
                "agencia TEXT," +
                "hab TEXT," +
                "cliente TEXT," +
                "hotel TEXT," +
                "fechaConfeccion TEXT," +
                "fechaEjecucion TEXT," +
                "fechaOrigEjec TEXT," +
                "fechaRepVenta TEXT," +
                "fechaCanc TEXT," +
                "adultos INTEGER," +
                "menores INTEGER," +
                "infantes INTEGER," +
                "acompanantes INTEGER," +
                "idioma TEXT," +
                "precio TEXT," +
                "estado INTEGER DEFAULT 0," +
                "fechaDevolucion TEXT," +
                "importeDevuelto TEXT," +
                "historial TEXT," +
                "incluirRepVenta INTEGER DEFAULT 1," +
                "observaciones TEXT)");

        bd.execSQL("CREATE TABLE Hoteles(" +
                "id INTEGER PRIMARY KEY," +
                "nombre TEXT)");

        bd.execSQL("CREATE TABLE Excursiones(" +
                "id INTEGER PRIMARY KEY," +
                "nombre TEXT," +
                "tipoPrecio INTEGER," +
                "precioAdulto TEXT," +
                "precioMenor TEXT," +
                "precioAcomp TEXT," +
                "precioRango TEXT," +
                "idiomaNecesario INTEGER," +
                "rango INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:
                db.execSQL("ALTER TABLE Reservas ADD COLUMN acompanantes INTEGER;");
                db.execSQL("ALTER TABLE Reservas ADD COLUMN estado INTEGER DEFAULT 0;");
            case 2:
                db.execSQL("CREATE TABLE Hoteles(" +
                        "id INTEGER PRIMARY KEY," +
                        "nombre TEXT)");
            case 3:
                db.execSQL("CREATE TABLE Excursiones(" +
                        "id INTEGER PRIMARY KEY," +
                        "nombre TEXT," +
                        "tipoPrecio INTEGER," +
                        "precioAdulto TEXT," +
                        "precioMenor TEXT," +
                        "precioAcomp TEXT," +
                        "precioRango TEXT," +
                        "rango INTEGER)");
            case 4:
                db.execSQL("ALTER TABLE Reservas ADD COLUMN fechaRepVenta TEXT;");
            case 5:
                db.execSQL("ALTER TABLE Excursiones ADD COLUMN idiomaNecesario INTEGER;");
            case 6:
                db.execSQL("ALTER TABLE Reservas ADD COLUMN fechaDevolucion TEXT;");
                db.execSQL("ALTER TABLE Reservas ADD COLUMN importeDevuelto TEXT;");
            case 7:
                db.execSQL("ALTER TABLE Reservas ADD COLUMN fechaOrigEjec TEXT;");
            case 8:
                db.execSQL("ALTER TABLE Reservas ADD COLUMN fechaCanc TEXT;");
                db.execSQL("ALTER TABLE Reservas ADD COLUMN historial TEXT;");
            case 9:
                db.execSQL("ALTER TABLE Reservas ADD COLUMN incluirRepVenta INTEGER DEFAULT 1;");
        }
    }
}
