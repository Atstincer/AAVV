package com.example.usuario.aavv.Hoteles;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.usuario.aavv.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.aavv.TTOO.TTOO;
import com.example.usuario.aavv.TTOO.TTOOBDHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 23/07/2023.
 */

public class HotelBDHandler {

    public final static String TABLE_NAME = "Hoteles";
    static String CAMPO_NOMBRE = "nombre";

    @SuppressLint("Range")
    static Hotel getHotel(Cursor cursor){
        Hotel hotel = new Hotel();
        hotel.setId(cursor.getLong(0));
        hotel.setNombre(cursor.getString(cursor.getColumnIndex(HotelBDHandler.CAMPO_NOMBRE)));
        return hotel;
    }

    public static ContentValues getContentValues(Hotel hotel){
        ContentValues values = new ContentValues();
        values.put(HotelBDHandler.CAMPO_NOMBRE,hotel.getNombre());
        return values;
    }

    public static List<Hotel> getAllHotelesfromDB(Context ctx){
        List<Hotel> hotelesList = new ArrayList<>();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursor = bd.rawQuery("Select * from "+ HotelBDHandler.TABLE_NAME,null);
        if(cursor.moveToFirst()){
            do{
                hotelesList.add(getHotel(cursor));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return hotelesList;
    }

    public static Hotel getHotelfromDB(Context ctx,long id){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursor = bd.rawQuery("Select * from "+ HotelBDHandler.TABLE_NAME+" where id=?",new String[]{String.valueOf(id)});
        if(cursor.moveToFirst()){
            return getHotel(cursor);
        }
        cursor.close();
        return new Hotel();
    }

    public static Hotel getHotelfromDB(Context ctx,String name){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursor = bd.rawQuery("Select * from "+ HotelBDHandler.TABLE_NAME+" where "+HotelBDHandler.CAMPO_NOMBRE+"=?",
                new String[]{name});
        if(cursor.moveToFirst()){
            return getHotel(cursor);
        }
        cursor.close();
        return new Hotel();
    }
}
