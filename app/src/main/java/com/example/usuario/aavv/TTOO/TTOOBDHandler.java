package com.example.usuario.aavv.TTOO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.usuario.aavv.Almacenamiento.AdminSQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 23/07/2023.
 */

public class TTOOBDHandler {

    static String TABLE_NAME = "Touroperadores";
    static String CAMPO_NOMBRE = "nombre";

    static TTOO getTTOO(Cursor cursor){
        TTOO ttoo = new TTOO();
        ttoo.setId(cursor.getLong(0));
        ttoo.setNombre(cursor.getString(cursor.getColumnIndex(TTOOBDHandler.CAMPO_NOMBRE)));
        return ttoo;
    }

    public static List<TTOO> getAllTTOOfromDB(Context ctx){
        List<TTOO> ttooList = new ArrayList<>();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor cursor = bd.rawQuery("Select * from "+TTOOBDHandler.TABLE_NAME,null);
        if(cursor.moveToFirst()){
            do{
                ttooList.add(getTTOO(cursor));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return ttooList;
    }

    static TTOO getTTOOfromDB(Context ctx,long id){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Cursor cursor = bd.rawQuery("Select * from "+TTOOBDHandler.TABLE_NAME+" where id=?",new String[]{String.valueOf(id)});
        if(cursor.moveToFirst()){
            return getTTOO(cursor);
        }
        cursor.close();
        return new TTOO();
    }
}
