package com.example.usuario.aavv.Almacenamiento;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.usuario.aavv.Excursiones.Excursion;
import com.example.usuario.aavv.Excursiones.ExcursionBDHandler;
import com.example.usuario.aavv.Hoteles.Hotel;
import com.example.usuario.aavv.Hoteles.HotelBDHandler;
import com.example.usuario.aavv.Reservas.Reserva;
import com.example.usuario.aavv.Reservas.ReservaBDHandler;
import com.example.usuario.aavv.TTOO.TTOO;
import com.example.usuario.aavv.TTOO.TTOOBDHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BDImporter {

    private final Context context;
    private final FragmentActivity activity;
    private final CallFromImporter callBack;

    public BDImporter(Context context, FragmentActivity activity, CallFromImporter callBack) {
        this.context = context;
        this.activity = activity;
        this.callBack = callBack;
    }

    public void importar(Uri uri){
        callBack.starLoading();
        new Thread(()->{
            TipoDato tipoDato = TipoDato.RESERVAS;
            List<Reserva> reservaList = new ArrayList<>();
            List<Excursion> excursionList = new ArrayList<>();
            List<TTOO> ttooList = new ArrayList<>();
            List<Hotel> hotelList = new ArrayList<>();
            try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if(line.equals(ReservaBDHandler.TABLE_NAME)){tipoDato= TipoDato.RESERVAS;continue;}//Reservas
                    if(line.equals(ExcursionBDHandler.TABLE_NAME)){tipoDato= TipoDato.EXCURSIONES;continue;}//Excursiones
                    if(line.equals(TTOOBDHandler.TABLE_NAME)){tipoDato= TipoDato.AGENCIAS;continue;}//Agencias
                    if(line.equals(HotelBDHandler.TABLE_NAME)){tipoDato= TipoDato.HOTELES;continue;}//Hoteles
                    if(line.equals("Config")){tipoDato= TipoDato.CONFIGURACION;continue;}//Configuracion

                    switch (tipoDato){
                        case RESERVAS:
                            Reserva reserva = getReserva(line);
                            if (reserva != null) {
                                reservaList.add(reserva);
                            }
                            break;
                        case EXCURSIONES:
                            Excursion excursion = getExcursion(line);
                            if (excursion != null) {
                                excursionList.add(excursion);
                            }
                            break;
                        case AGENCIAS:
                            TTOO agencia = getAgencia(line);
                            if (agencia != null) {
                                ttooList.add(agencia);
                            }
                            break;
                        case HOTELES:
                            Hotel hotel = getHotel(line);
                            if (hotel != null) {
                                hotelList.add(hotel);
                            }
                            break;
                        case CONFIGURACION:
                            saveConfig(line);
                            break;
                    }
                }

                if(!reservaList.isEmpty()){
                    storeReservasEnBD(reservaList);
                }
                if(!excursionList.isEmpty()){
                    storeExcursionesEnBD(excursionList);
                }
                if(!ttooList.isEmpty()){
                    storeAgenciasEnBD(ttooList);
                }
                if(!hotelList.isEmpty()){
                    storeHotelesEnBD(hotelList);
                }
                activity.runOnUiThread(()->{
                    callBack.endLoading();
                    callBack.refreshUI();
                    Toast.makeText(context,"Archivo importado correctamente",Toast.LENGTH_SHORT).show();
                });
                Log.d("Importando","Importado correctamente: "+reservaList.size()+" reservas en la lista.");
            }catch (Exception e){
                callBack.endLoading();
                Log.e("Importando","Error en importCSVInfo",e);
            }
        }).start();
    }

    private Reserva getReserva(@NonNull String line){
        String[] str = line.split("[|]");
        Reserva reserva = new Reserva();
        try{
            if(hasValue(str[0])){
                reserva.setNoTE(str[0]);
            }
            if(hasValue(str[1])){
                reserva.setExcursion(formatStringToImport(str[1]));
            }
            if(hasValue(str[2])){
                reserva.setAgencia(str[2]);
            }
            if(hasValue(str[3])){
                reserva.setNoHab(str[3]);
            }
            if(hasValue(str[4])){
                reserva.setCliente(str[4]);
            }
            if(hasValue(str[5])){
                reserva.setHotel(str[5]);
            }
            if(hasValue(str[6])){
                reserva.setFechaConfeccion(str[6]);
            }
            if(hasValue(str[7])){
                reserva.setFechaEjecucion(str[7]);
            }
            if(hasValue(str[8])){
                reserva.setFechaOriginalEjecucion(str[8]);
            }
            if(hasValue(str[9])){
                if(str[9].equals("true")){
                    reserva.setIncluirEnRepVenta(true);
                }else if(str[9].equals("false")){
                    reserva.setIncluirEnRepVenta(false);
                }
            }
            if(hasValue(str[10])){
                reserva.setFechaReporteVenta(str[10]);
            }
            if(hasValue(str[11])){
                reserva.setAdultos(Integer.parseInt(str[11]));
            }
            if(hasValue(str[12])){
                reserva.setMenores(Integer.parseInt(str[12]));
            }
            if(hasValue(str[13])){
                reserva.setInfantes(Integer.parseInt(str[13]));
            }
            if(hasValue(str[14])){
                reserva.setAcompanante(Integer.parseInt(str[14]));
            }
            if(hasValue(str[15])){
                reserva.setIdioma(str[15]);
            }
            if(hasValue(str[16])){
                reserva.setPrecio(Double.parseDouble(str[16]));
            }
            if(hasValue(str[17])){
                reserva.setEstado(Integer.parseInt(str[17]));
            }
            if(hasValue(str[18])){
                reserva.setFechaDevolucion(str[18]);
            }
            if(hasValue(str[19])){
                reserva.setFechaCancelacion(str[19]);
            }
            if(hasValue(str[20])){
                reserva.setImporteDevuelto(Double.parseDouble(str[20]));
            }
            if(hasValue(str[21])){
                reserva.setHistorial(formatStringToImport(str[21]));
            }
            if(hasValue(str[22])){
                reserva.setObsDevolucion(str[22]);
            }
            if(hasValue(str[23])){
                reserva.setObservaciones(formatStringToImport(str[23]));
            }
            return reserva;
        }catch (Exception e){
            Log.e("importando","Error getting reserva.",e);
            return null;
        }
    }

    private Excursion getExcursion(@NonNull String line){
        String[] str = line.split("[|]");
        Excursion excursion = new Excursion();
        try{
            if(hasValue(str[0])){
                excursion.setId(Long.parseLong(str[0]));
            }
            if(hasValue(str[1])){
                excursion.setNombre(str[1]);
            }
            if(hasValue(str[2])){
                excursion.setTipoPrecio(Integer.parseInt(str[2]));
            }
            if(hasValue(str[3])){
                excursion.setPrecioAd(Float.parseFloat(str[3]));
            }
            if(hasValue(str[4])){
                excursion.setPrecioMenor(Float.parseFloat(str[4]));
            }
            if(hasValue(str[5])){
                excursion.setPrecioAcomp(Float.parseFloat(str[5]));
            }
            if(hasValue(str[6])){
                excursion.setPrecioRango(Float.parseFloat(str[6]));
            }
            if(hasValue(str[7])){
                excursion.setIdiomaNecesario(Integer.parseInt(str[7]));
            }
            if(hasValue(str[8])){
                excursion.setRangoHasta(Integer.parseInt(str[8]));
            }
            return excursion;
        }catch (Exception e){
            Log.e("importando","Error getting excursion.",e);
            return null;
        }
    }

    private TTOO getAgencia(@NonNull String line){
        String[] str = line.split("[|]");
        TTOO agencia = new TTOO();
        try{
            if(hasValue(str[0])){
                agencia.setId(Long.parseLong(str[0]));
            }
            if(hasValue(str[1])){
                agencia.setNombre(str[1]);
            }
            return agencia;
        }catch (Exception e){
            Log.e("importando","Error getting agencia.",e);
            return null;
        }
    }

    private Hotel getHotel(@NonNull String line){
        String[] str = line.split("[|]");
        Hotel hotel = new Hotel();
        try{
            if(hasValue(str[0])){
                hotel.setId(Long.parseLong(str[0]));
            }
            if(hasValue(str[1])){
                hotel.setNombre(str[1]);
            }
            return hotel;
        }catch (Exception e){
            Log.e("importando","Error getting hotel.",e);
            return null;
        }
    }

    private void saveConfig(String line){
        try{
            String[] str = line.split("[|]");
            if(hasValue(str[0])) {//nombre vendedor
                MySharedPreferences.storeNombreVendedor(context,str[0]);
            }
            if(hasValue(str[1])) {//telefono vendedor
                MySharedPreferences.storeTelefonoVendedor(context,str[1]);
            }
            if(hasValue(str[2])) {//agencia
                MySharedPreferences.storeAgenciaVendedor(context,str[2]);
            }
            if(hasValue(str[3])) {//inicio
                MySharedPreferences.storeFragmentInicio(context,Integer.parseInt(str[3]));
            }
            if(hasValue(str[4])) {//predecir precio
                if(str[4].equals("true")) {
                    MySharedPreferences.storePredecirPrecio(context, true);
                }else if(str[4].equals("false")){
                    MySharedPreferences.storePredecirPrecio(context, false);
                }
            }
            if(hasValue(str[5])) {//incluir dev en liquidaciones
                if(str[5].equals("true")) {
                    MySharedPreferences.storeIncluirDevEnLiquidacion(context, true);
                }else if(str[5].equals("false")){
                    MySharedPreferences.storeIncluirDevEnLiquidacion(context, false);
                }
            }
            if(hasValue(str[6])) {//incluir precio en cup
                if(str[6].equals("true")) {
                    MySharedPreferences.storeIncluirPrecioCUP(context, true);
                }else if(str[6].equals("false")){
                    MySharedPreferences.storeIncluirPrecioCUP(context, false);
                }
            }
            if(hasValue(str[7])) {//tasa cup
                MySharedPreferences.storeTasaCUP(context,Float.parseFloat(str[7]));
            }

            if(hasValue(str[8])) {//mail repVenta
                MySharedPreferences.addMailsIfDoesntExits(context,str[8]);
            }
            if(hasValue(str[9])){//fecha a filtrar
                MySharedPreferences.storeTipoFechaFiltrar(context,Integer.parseInt(str[9]));
            }
            if(hasValue(str[10])){
                if(str[10].equals("true")){
                    MySharedPreferences.storeCierreRegular(context,true);
                } else if(str[10].equals("false")){
                    MySharedPreferences.storeCierreRegular(context,false);
                }
            }
            if(hasValue(str[11])){
                MySharedPreferences.storeDiaCierre(context,Integer.parseInt(str[11]));
            }
        }catch (Exception e){
            Log.e("Importando","Error salvando la configuracion",e);
        }
    }

    private void storeHotelesEnBD(List<Hotel> hoteles){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(context, AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        for(Hotel hotel:hoteles){
            Hotel hotelBD = HotelBDHandler.getHotelfromDB(context,hotel.getNombre());
            if(hotelBD.getNombre()!=null && hotelBD.getNombre().equals(hotel.getNombre())){continue;}
            ContentValues values = HotelBDHandler.getContentValues(hotel);
            db.insert(HotelBDHandler.TABLE_NAME, null, values);
        }
    }

    private void storeAgenciasEnBD(List<TTOO> agencias){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(context, AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        for(TTOO agencia:agencias){
            TTOO agenciaBD = TTOOBDHandler.getTTOOfromDB(context,agencia.getNombre());
            if(agenciaBD.getNombre()!=null && agenciaBD.getNombre().equals(agencia.getNombre())){continue;}
            ContentValues values = TTOOBDHandler.getContentValues(agencia);
            db.insert(TTOOBDHandler.TABLE_NAME, null, values);
        }
    }

    private void storeExcursionesEnBD(List<Excursion> excursiones){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(context, AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        for(Excursion excursion:excursiones){
            Excursion excBD = ExcursionBDHandler.getExcursionfromDB(context,excursion.getNombre());
            if(excBD.getNombre()!=null && excBD.getNombre().equals(excursion.getNombre())){continue;}
            ContentValues values = ExcursionBDHandler.getContentValues(excursion);
            db.insert(ExcursionBDHandler.TABLE_NAME, null, values);
        }
    }
    private void storeReservasEnBD(List<Reserva> reservaList){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(context, AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        for(Reserva reserva:reservaList){
            Reserva reservaBD = ReservaBDHandler.getReservaFromDB(context,reserva.getNoTE());
            if(reservaBD.getNoTE()!=null && reservaBD.getNoTE().equals(reserva.getNoTE())){continue;}
            ContentValues values = ReservaBDHandler.getContentValues(reserva);
            db.insert(ReservaBDHandler.TABLE_NAME, null, values);
        }
    }


    private String formatStringToImport(String texto){
        if(texto!=null) {
            return texto.replace("^", "\n");
        }
        return "";
    }

    private boolean hasValue(@NonNull String value){
        return !value.equals("null") && !value.isEmpty();
    }

    private enum TipoDato{
        RESERVAS, EXCURSIONES, AGENCIAS, HOTELES, CONFIGURACION
    }

    public interface CallFromImporter{
        void refreshUI();
        void starLoading();
        void endLoading();
    }

}
