package com.example.usuario.aavv.Excursiones;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.usuario.aavv.Almacenamiento.AdminSQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 1/10/2023.
 */

public class ExcursionBDHandler {

    static final private String TAG;

    public static String TABLE_NAME;
    private final static String CAMPO_NOMBRE;
    private final static String CAMPO_TIPO_PRECIO;
    private final static String CAMPO_PRECIO_ADULTO;
    private final static String CAMPO_PRECIO_MENOR;
    private final static String CAMPO_PRECIO_ACOMPANANTE;
    private final static String CAMPO_PRECIO_RANGO;
    private final static String CAMPO_RANGO;
    private final static String CAMPO_IDIOMA_NECESARIO;

    static {
        TAG = "ExcursionBDHandler";
        TABLE_NAME = "Excursiones";
        CAMPO_NOMBRE = "nombre";
        CAMPO_TIPO_PRECIO = "tipoPrecio";
        CAMPO_PRECIO_ADULTO = "precioAdulto";
        CAMPO_PRECIO_MENOR = "precioMenor";
        CAMPO_PRECIO_ACOMPANANTE = "precioAcomp";
        CAMPO_PRECIO_RANGO = "precioRango";
        CAMPO_RANGO = "rango";
        CAMPO_IDIOMA_NECESARIO = "idiomaNecesario";
    }


    public static ContentValues getContentValues(Excursion excursion){
        ContentValues values = new ContentValues();
        values.put(ExcursionBDHandler.CAMPO_NOMBRE,excursion.getNombre());
        values.put(ExcursionBDHandler.CAMPO_TIPO_PRECIO,excursion.getTipoPrecio());
        values.put(ExcursionBDHandler.CAMPO_PRECIO_ADULTO,excursion.getPrecioAd());
        values.put(ExcursionBDHandler.CAMPO_PRECIO_MENOR,excursion.getPrecioMenor());
        values.put(ExcursionBDHandler.CAMPO_PRECIO_ACOMPANANTE,excursion.getPrecioAcomp());
        values.put(ExcursionBDHandler.CAMPO_PRECIO_RANGO,excursion.getPrecioRango());
        values.put(ExcursionBDHandler.CAMPO_RANGO,excursion.getRangoHasta());
        values.put(ExcursionBDHandler.CAMPO_IDIOMA_NECESARIO,excursion.getIdiomaNecesario());
        return values;
    }

    @SuppressLint("Range")
    private static Excursion getExcursion(Cursor cursor){
        Excursion excursion = new Excursion();
        excursion.setId(cursor.getLong(0));
        excursion.setNombre(cursor.getString(cursor.getColumnIndex(ExcursionBDHandler.CAMPO_NOMBRE)));
        excursion.setTipoPrecio(cursor.getInt(cursor.getColumnIndex(ExcursionBDHandler.CAMPO_TIPO_PRECIO)));
        try{
            excursion.setPrecioAd(Float.parseFloat(cursor.getString(cursor.getColumnIndex(ExcursionBDHandler.CAMPO_PRECIO_ADULTO))));
            excursion.setPrecioMenor(Float.parseFloat(cursor.getString(cursor.getColumnIndex(ExcursionBDHandler.CAMPO_PRECIO_MENOR))));
            excursion.setPrecioAcomp(Float.parseFloat(cursor.getString(cursor.getColumnIndex(ExcursionBDHandler.CAMPO_PRECIO_ACOMPANANTE))));
            excursion.setPrecioRango(Float.parseFloat(cursor.getString(cursor.getColumnIndex(ExcursionBDHandler.CAMPO_PRECIO_RANGO))));
        }catch (Exception e){
            Log.e(TAG, "getExcursion: ", e);
        }
        excursion.setRangoHasta(cursor.getInt(cursor.getColumnIndex(ExcursionBDHandler.CAMPO_RANGO)));
        excursion.setIdiomaNecesario(cursor.getInt(cursor.getColumnIndex(ExcursionBDHandler.CAMPO_IDIOMA_NECESARIO)));
        return excursion;
    }

    public static List<Excursion> getAllExcursionesfromDB(Context ctx){
        List<Excursion> excursionesList = new ArrayList<>();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursor = bd.rawQuery("Select * from "+ExcursionBDHandler.TABLE_NAME,null);
        if(cursor.moveToFirst()){
            do{
                excursionesList.add(getExcursion(cursor));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return excursionesList;
    }

    public static Excursion getExcursionfromDB(Context ctx,long id){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursor = bd.rawQuery("Select * from "+ExcursionBDHandler.TABLE_NAME+" where id=?",new String[]{String.valueOf(id)});
        if(cursor.moveToFirst()){
            return getExcursion(cursor);
        }
        cursor.close();
        return new Excursion();
    }

    public static Excursion getExcursionfromDB(Context ctx,String name){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursor = bd.rawQuery("Select * from "+ExcursionBDHandler.TABLE_NAME+" where "+ExcursionBDHandler.CAMPO_NOMBRE+"=?",
                new String[]{name});
        if(cursor.moveToFirst()){
            return getExcursion(cursor);
        }
        cursor.close();
        return new Excursion();
    }

}
