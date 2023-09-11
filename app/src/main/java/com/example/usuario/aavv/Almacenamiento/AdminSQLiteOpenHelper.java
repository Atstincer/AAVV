package com.example.usuario.aavv.Almacenamiento;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by usuario on 29/10/2021.
 */

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String BD_NAME = "MiBD";
    public static final int BD_VERSION = 2;
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
                "adultos INTEGER," +
                "menores INTEGER," +
                "infantes INTEGER," +
                "acompanantes INTEGER," +
                "idioma TEXT," +
                "precio TEXT," +
                "estado INTEGER DEFAULT 0," +
                "observaciones TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:
                db.execSQL("ALTER TABLE Reservas ADD COLUMN acompanantes INTEGER;");
                db.execSQL("ALTER TABLE Reservas ADD COLUMN estado INTEGER DEFAULT 0;");
        }
    }
}
