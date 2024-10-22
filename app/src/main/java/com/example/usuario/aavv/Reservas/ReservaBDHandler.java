package com.example.usuario.aavv.Reservas;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.usuario.aavv.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.aavv.Util.DateHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 30/07/2023.
 */

public class ReservaBDHandler {

    public final static String TABLE_NAME = "Reservas";
    public final static String CAMPO_NUMERO_TE = "TE";
    private final static String CAMPO_EXCURSION = "excursion";
    final static String CAMPO_AGENCIA = "agencia";
    private final static String CAMPO_NUMERO_HAB = "hab";
    private final static String CAMPO_CLIENTE = "cliente";
    private final static String CAMPO_HOTEL = "hotel";
    final static String CAMPO_FECHA_CONFECCION = "fechaConfeccion";
    final static String CAMPO_FECHA_EJECUCION = "fechaEjecucion";
    final static String CAMPO_FECHA_EJECUCION_ORIGINAL = "fechaOrigEjec";
    final static String CAMPO_FECHA_REPORTE_VENTA = "fechaRepVenta";
    private final static String CAMPO_ADULTOS = "adultos";
    private final static String CAMPO_MENORES = "menores";
    private final static String CAMPO_INFANTES = "infantes";
    private final static String CAMPO_ACOMPANANTES = "acompanantes";
    private final static String CAMPO_IDIOMA = "idioma";
    private final static String CAMPO_PRECIO = "precio";
    final static String CAMPO_ESTADO = "estado";
    final static String CAMPO_FECHA_DEVOLUCION = "fechaDevolucion";
    final static String CAMPO_FECHA_CANCELACION = "fechaCanc";
    final static String CAMPO_IMPORTE_DEVUELTO = "importeDevuelto";
    final static String CAMPO_HISTORIAL = "historial";
    final static String CAMPO_INCLUIR_REP_VENTA = "incluirRepVenta";
    private final static String CAMPO_OBSERVACIONES = "observaciones";
    //static String CAMPO_OBS_DEVOLUCION = "obsDevolucion";


    public static ContentValues getContentValues(Reserva reserva){
        ContentValues values = new ContentValues();
        if(reserva.getFechaConfeccion()!=null && !reserva.getFechaConfeccion().isEmpty()) {
            values.put(ReservaBDHandler.CAMPO_FECHA_CONFECCION, DateHandler.formatDateToStoreInDB(reserva.getFechaConfeccion()));
        }
        values.put(ReservaBDHandler.CAMPO_NUMERO_TE,reserva.getNoTE());
        values.put(ReservaBDHandler.CAMPO_EXCURSION,reserva.getExcursion());
        values.put(ReservaBDHandler.CAMPO_CLIENTE,reserva.getCliente());
        values.put(ReservaBDHandler.CAMPO_ADULTOS,reserva.getAdultos());
        values.put(ReservaBDHandler.CAMPO_MENORES,reserva.getMenores());
        values.put(ReservaBDHandler.CAMPO_INFANTES,reserva.getInfantes());
        values.put(ReservaBDHandler.CAMPO_ACOMPANANTES,reserva.getAcompanantes());
        if(reserva.getFechaEjecucion()!=null && !reserva.getFechaEjecucion().isEmpty()) {
            values.put(ReservaBDHandler.CAMPO_FECHA_EJECUCION, DateHandler.formatDateToStoreInDB(reserva.getFechaEjecucion()));
        }
        if(reserva.getFechaOriginalEjecucion()!=null && !reserva.getFechaOriginalEjecucion().isEmpty()) {
            values.put(ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL, DateHandler.formatDateToStoreInDB(reserva.getFechaOriginalEjecucion()));
        }
        if(reserva.getFechaReporteVenta()!=null && !reserva.getFechaReporteVenta().isEmpty()){
            Log.d("fecha","Fecha reporte de venta: "+reserva.getFechaReporteVenta());
            values.put(ReservaBDHandler.CAMPO_FECHA_REPORTE_VENTA,DateHandler.formatDateToStoreInDB(reserva.getFechaReporteVenta()));
        }
        if(reserva.getFechaCancelacion()!=null && !reserva.getFechaCancelacion().isEmpty()){
            values.put(ReservaBDHandler.CAMPO_FECHA_CANCELACION,DateHandler.formatDateToStoreInDB(reserva.getFechaCancelacion()));
        }
        if(reserva.getFechaDevolucion()!=null && !reserva.getFechaDevolucion().isEmpty()){
            values.put(ReservaBDHandler.CAMPO_FECHA_DEVOLUCION,DateHandler.formatDateToStoreInDB(reserva.getFechaDevolucion()));
        }
        values.put(ReservaBDHandler.CAMPO_AGENCIA,reserva.getAgencia());
        values.put(ReservaBDHandler.CAMPO_HOTEL,reserva.getHotel());
        values.put(ReservaBDHandler.CAMPO_NUMERO_HAB,reserva.getNoHab());
        values.put(ReservaBDHandler.CAMPO_PRECIO,reserva.getPrecio());
        values.put(ReservaBDHandler.CAMPO_IMPORTE_DEVUELTO,reserva.getImporteDevuelto());
        values.put(ReservaBDHandler.CAMPO_IDIOMA,reserva.getIdioma());
        values.put(ReservaBDHandler.CAMPO_ESTADO,reserva.getEstado());
        values.put(ReservaBDHandler.CAMPO_OBSERVACIONES,reserva.getObservaciones());
        values.put(CAMPO_HISTORIAL,reserva.getHistorial());
        values.put(CAMPO_INCLUIR_REP_VENTA,reserva.incluirEnRepVenta());
        /*if(reserva.getObsDevolucion() != null && !reserva.getObsDevolucion().isEmpty()){
            values.put(CAMPO_OBS_DEVOLUCION, reserva.getObsDevolucion());
        }*/
        return values;
    }

    @SuppressLint("Range")
    private static Reserva getReserva(Cursor cursor){
        Reserva reserva = new Reserva();
        reserva.setNoTE(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_NUMERO_TE)));
        reserva.setExcursion(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_EXCURSION)));
        reserva.setAgencia(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_AGENCIA)));
        reserva.setNoHab(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_NUMERO_HAB)));
        reserva.setCliente(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_CLIENTE)));
        reserva.setHotel(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_HOTEL)));
        reserva.setFechaConfeccion(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_CONFECCION))));
        reserva.setFechaEjecucion(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_EJECUCION))));
        if(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL)) != null &&
                !cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL)).isEmpty()){
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
                    !cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_IMPORTE_DEVUELTO)).isEmpty()) {
                reserva.setImporteDevuelto(Double.parseDouble(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_IMPORTE_DEVUELTO))));
            }
        }
        if(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_CANCELACION))!=null) {
            reserva.setFechaCancelacion(DateHandler.formatDateToShow(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_FECHA_CANCELACION))));
        }
        reserva.setHistorial(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_HISTORIAL)));
        if(cursor.getInt(cursor.getColumnIndex(ReservaBDHandler.CAMPO_INCLUIR_REP_VENTA))==1){
            reserva.setIncluirEnRepVenta(true);
        }else if(cursor.getInt(cursor.getColumnIndex(ReservaBDHandler.CAMPO_INCLUIR_REP_VENTA))==0){
            reserva.setIncluirEnRepVenta(false);
        }
        //reserva.setObsDevolucion(cursor.getString(cursor.getColumnIndex(ReservaBDHandler.CAMPO_OBS_DEVOLUCION)));
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

    public static Reserva getReservaFromDB(Context ctx, String numeroTE){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(ctx,AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_NUMERO_TE+"=?",
                new String[]{numeroTE});
        if(cursor.moveToFirst()){
            Reserva reserva = getReserva(cursor);
            cursor.close();
            return reserva;
        }
        cursor.close();
        return new Reserva();
    }
}
