package com.example.usuario.aavv.Reservas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.usuario.aavv.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.aavv.Util.DateHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 30/07/2023.
 */

public class ReservaBDHandler {

    static String TABLE_NAME = "Reservas";
    private static String CAMPO_NUMERO_TE = "TE";
    private static String CAMPO_EXCURSION = "excursion";
    private static String CAMPO_AGENCIA = "agencia";
    private static String CAMPO_NUMERO_HAB = "hab";
    private static String CAMPO_CLIENTE = "cliente";
    private static String CAMPO_HOTEL = "hotel";
    private static String CAMPO_FECHA_CONFECCION = "fechaConfeccion";
    private static String CAMPO_FECHA_EJECUCION = "fechaEjecucion";
    private static String CAMPO_ADULTOS = "adultos";
    private static String CAMPO_MENORES = "menores";
    private static String CAMPO_INFANTES = "infantes";
    private static String CAMPO_IDIOMA = "idioma";
    private static String CAMPO_PRECIO = "precio";
    private static String CAMPO_OBSERVACIONES = "observaciones";


    static ContentValues getContentValues(Reserva reserva){
        ContentValues values = new ContentValues();
        values.put(ReservaBDHandler.CAMPO_FECHA_CONFECCION, DateHandler.formatDateToStoreInDB(reserva.getFechaConfeccion()));
        values.put(ReservaBDHandler.CAMPO_NUMERO_TE,reserva.getNoTE());
        values.put(ReservaBDHandler.CAMPO_EXCURSION,reserva.getExcursion());
        values.put(ReservaBDHandler.CAMPO_CLIENTE,reserva.getCliente());
        values.put(ReservaBDHandler.CAMPO_ADULTOS,reserva.getAdultos());
        values.put(ReservaBDHandler.CAMPO_MENORES,reserva.getMenores());
        values.put(ReservaBDHandler.CAMPO_INFANTES,reserva.getInfantes());
        values.put(ReservaBDHandler.CAMPO_FECHA_EJECUCION,DateHandler.formatDateToStoreInDB(reserva.getFechaEjecucion()));
        values.put(ReservaBDHandler.CAMPO_AGENCIA,reserva.getAgencia());
        values.put(ReservaBDHandler.CAMPO_HOTEL,reserva.getHotel());
        values.put(ReservaBDHandler.CAMPO_NUMERO_HAB,reserva.getNoHab());
        values.put(ReservaBDHandler.CAMPO_PRECIO,reserva.getPrecio());
        values.put(ReservaBDHandler.CAMPO_IDIOMA,reserva.getIdioma());
        values.put(ReservaBDHandler.CAMPO_OBSERVACIONES,reserva.getObservaciones());
        return values;
    }

    static Reserva getReserva(Cursor cursor){
        Reserva reserva = new Reserva();
        reserva.setId(cursor.getLong(0));
        reserva.setNoTE(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_NUMERO_TE)));
        reserva.setExcursion(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_EXCURSION)));
        reserva.setAgencia(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_AGENCIA)));
        reserva.setNoHab(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_NUMERO_HAB)));
        reserva.setCliente(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_CLIENTE)));
        reserva.setHotel(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_HOTEL)));
        reserva.setFechaConfeccion(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_CONFECCION))));
        reserva.setFechaEjecucion(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_EJECUCION))));
        reserva.setIdioma(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_IDIOMA)));
        reserva.setObservaciones(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_OBSERVACIONES)));
        reserva.setAdultos(cursor.getInt(cursor.getColumnIndex(ReservaBDHandler.CAMPO_ADULTOS)));
        reserva.setMenores(cursor.getInt(cursor.getColumnIndex(ReservaBDHandler.CAMPO_MENORES)));
        reserva.setInfantes(cursor.getInt(cursor.getColumnIndex(ReservaBDHandler.CAMPO_INFANTES)));
        reserva.setPrecio(Double.parseDouble(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_PRECIO))));
        return reserva;
    }

    static List<Reserva> getAllReservasFromDB(Context ctx){
        List<Reserva> reservasList = new ArrayList<>();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ReservaBDHandler.TABLE_NAME,null);
        if(cursor.moveToFirst()){
            do {
                reservasList.add(ReservaBDHandler.getReserva(cursor));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return reservasList;
    }

    static Reserva getReservaFromDB(Context ctx, long id){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE id=?",new String[]{String.valueOf(id)});
        if(cursor.moveToFirst()){
            Reserva reserva = getReserva(cursor);
            cursor.close();
            return reserva;
        }
        cursor.close();
        return new Reserva();
    }

}
