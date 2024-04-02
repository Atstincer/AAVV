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

    public static String TABLE_NAME = "Reservas";
    private static String CAMPO_NUMERO_TE = "TE";
    private static String CAMPO_EXCURSION = "excursion";
    static String CAMPO_AGENCIA = "agencia";
    private static String CAMPO_NUMERO_HAB = "hab";
    private static String CAMPO_CLIENTE = "cliente";
    private static String CAMPO_HOTEL = "hotel";
    static String CAMPO_FECHA_CONFECCION = "fechaConfeccion";
    static String CAMPO_FECHA_EJECUCION = "fechaEjecucion";
    static String CAMPO_FECHA_EJECUCION_ORIGINAL = "fechaOrigEjec";
    static String CAMPO_FECHA_REPORTE_VENTA = "fechaRepVenta";
    private static String CAMPO_ADULTOS = "adultos";
    private static String CAMPO_MENORES = "menores";
    private static String CAMPO_INFANTES = "infantes";
    private static String CAMPO_ACOMPANANTES = "acompanantes";
    private static String CAMPO_IDIOMA = "idioma";
    private static String CAMPO_PRECIO = "precio";
    static String CAMPO_ESTADO = "estado";
    static String CAMPO_FECHA_DEVOLUCION = "fechaDevolucion";
    static String CAMPO_FECHA_CANCELACION = "fechaCanc";
    static String CAMPO_IMPORTE_DEVUELTO = "importeDevuelto";
    static String CAMPO_HISTORIAL = "historial";
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
        values.put(ReservaBDHandler.CAMPO_ACOMPANANTES,reserva.getAcompanantes());
        values.put(ReservaBDHandler.CAMPO_FECHA_EJECUCION,DateHandler.formatDateToStoreInDB(reserva.getFechaEjecucion()));
        if(reserva.getFechaOriginalEjecucion()!=null && !reserva.getFechaOriginalEjecucion().equals("")) {
            values.put(ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL, DateHandler.formatDateToStoreInDB(reserva.getFechaOriginalEjecucion()));
        }
        if(reserva.getFechaReporteVenta()!=null){
            values.put(ReservaBDHandler.CAMPO_FECHA_REPORTE_VENTA,DateHandler.formatDateToStoreInDB(reserva.getFechaReporteVenta()));
        }
        if(reserva.getFechaCancelacion()!=null){
            values.put(ReservaBDHandler.CAMPO_FECHA_CANCELACION,DateHandler.formatDateToStoreInDB(reserva.getFechaCancelacion()));
        }
        values.put(ReservaBDHandler.CAMPO_AGENCIA,reserva.getAgencia());
        values.put(ReservaBDHandler.CAMPO_HOTEL,reserva.getHotel());
        values.put(ReservaBDHandler.CAMPO_NUMERO_HAB,reserva.getNoHab());
        values.put(ReservaBDHandler.CAMPO_PRECIO,reserva.getPrecio());
        values.put(ReservaBDHandler.CAMPO_IDIOMA,reserva.getIdioma());
        values.put(ReservaBDHandler.CAMPO_ESTADO,reserva.getEstado());
        values.put(ReservaBDHandler.CAMPO_OBSERVACIONES,reserva.getObservaciones());
        values.put(CAMPO_HISTORIAL,reserva.getHistorial());
        return values;
    }

    private static Reserva getReserva(Cursor cursor){
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
        if(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL)) != null &&
                !cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL)).equals("")){
            reserva.setFechaOriginalEjecucion(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL))));
        }
        if(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_REPORTE_VENTA))!=null) {
            reserva.setFechaReporteVenta(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_REPORTE_VENTA))));
        }
        reserva.setIdioma(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_IDIOMA)));
        reserva.setObservaciones(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_OBSERVACIONES)));
        reserva.setAdultos(cursor.getInt(cursor.getColumnIndex(ReservaBDHandler.CAMPO_ADULTOS)));
        reserva.setMenores(cursor.getInt(cursor.getColumnIndex(ReservaBDHandler.CAMPO_MENORES)));
        reserva.setInfantes(cursor.getInt(cursor.getColumnIndex(ReservaBDHandler.CAMPO_INFANTES)));
        reserva.setAcompanante(cursor.getInt(cursor.getColumnIndex(ReservaBDHandler.CAMPO_ACOMPANANTES)));
        reserva.setPrecio(Double.parseDouble(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_PRECIO))));
        reserva.setEstado(cursor.getInt(cursor.getColumnIndex(ReservaBDHandler.CAMPO_ESTADO)));
        if(reserva.getEstado()==Reserva.ESTADO_DEVUELTO){
            reserva.setFechaDevolucion(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_DEVOLUCION))));
            if(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_IMPORTE_DEVUELTO))!=null &&
                    !cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_IMPORTE_DEVUELTO)).equals("")) {
                reserva.setImporteDevuelto(Double.parseDouble(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_IMPORTE_DEVUELTO))));
            }
        }
        if(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_CANCELACION))!=null) {
            reserva.setFechaCancelacion(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_CANCELACION))));
        }
        reserva.setHistorial(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_HISTORIAL)));
        return reserva;
    }

    public static List<Reserva> getAllReservasFromDB(Context ctx){
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


/*    static List<Reserva> getReservasFromDB(Context ctx, String desde, String hasta, boolean soloActivas){
        String query = "";
        String [] args;
        if(soloActivas){
            query = "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_FECHA_EJECUCION+">=? AND " +
                    ""+ReservaBDHandler.CAMPO_FECHA_EJECUCION+"<=? AND "+ReservaBDHandler.CAMPO_ESTADO+"!=?";
            args = new String[]{DateHandler.formatDateToStoreInDB(desde),DateHandler.formatDateToStoreInDB(hasta),
                    String.valueOf(Reserva.ESTADO_CANCELADO)};
        }else {
            query = "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_FECHA_EJECUCION+">=? AND " +
                    ""+ReservaBDHandler.CAMPO_FECHA_EJECUCION+"<=?";
            args = new String[]{DateHandler.formatDateToStoreInDB(desde),DateHandler.formatDateToStoreInDB(hasta)};
        }
        return filterResult(getReservasFromDB(ctx,query,args),soloActivas);
    }

    static List<Reserva> getReservasFromDB(Context ctx, String desde, String hasta, String agencia, boolean soloActivas){
        String query = "";
        String [] args;
        if(soloActivas){
            query = "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_FECHA_EJECUCION+">=? AND " +
                    ""+ReservaBDHandler.CAMPO_FECHA_EJECUCION+"<=? AND "+ReservaBDHandler.CAMPO_AGENCIA+"=? AND "+ReservaBDHandler.CAMPO_ESTADO+"!=?";
            args = new String[]{DateHandler.formatDateToStoreInDB(desde),DateHandler.formatDateToStoreInDB(hasta),agencia,
                    String.valueOf(Reserva.ESTADO_CANCELADO)};
        }else {
            query = "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_FECHA_EJECUCION+">=? AND " +
                    ""+ReservaBDHandler.CAMPO_FECHA_EJECUCION+"<=? AND "+ReservaBDHandler.CAMPO_AGENCIA+"=?";
            args = new String[]{DateHandler.formatDateToStoreInDB(desde),DateHandler.formatDateToStoreInDB(hasta),agencia};
        }
        return filterResult(getReservasFromDB(ctx,query,args),soloActivas);
    }

    private static List<Reserva> filterResult(List<Reserva> list, boolean soloActivas){
        if(soloActivas){
            return getSoloActivas(list);
        }
        return list;
    }*/


    static List<Reserva> getReservasFromDB(Context ctx, String query, String[] arg){
        List<Reserva> reservasList = new ArrayList<>();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,arg);
        if(cursor.moveToFirst()){
            do {
                reservasList.add(getReserva(cursor));
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
