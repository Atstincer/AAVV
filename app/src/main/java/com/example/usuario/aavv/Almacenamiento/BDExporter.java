package com.example.usuario.aavv.Almacenamiento;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.widget.Toast;

import com.example.usuario.aavv.Ajustes.FragmentAjustes.MyCallBack;
import com.example.usuario.aavv.StorageAccess.SalvaBDStorageAccess;
import com.example.usuario.aavv.Excursiones.Excursion;
import com.example.usuario.aavv.Excursiones.ExcursionBDHandler;
import com.example.usuario.aavv.Hoteles.Hotel;
import com.example.usuario.aavv.Hoteles.HotelBDHandler;
import com.example.usuario.aavv.Reservas.Reserva;
import com.example.usuario.aavv.Reservas.ReservaBDHandler;
import com.example.usuario.aavv.TTOO.TTOO;
import com.example.usuario.aavv.TTOO.TTOOBDHandler;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;

import java.io.FileOutputStream;
import java.util.List;

public class BDExporter {

    private final MyCallBack myCallBack;
    private final Context context;
    private final FragmentActivity activity;

    public BDExporter(Context context,FragmentActivity activity,MyCallBack myCallBack) {
        this.myCallBack = myCallBack;
        this.context = context;
        this.activity = activity;
    }

    public void exportar(){
        new Thread(() -> {
            List<Reserva> reservasList = ReservaBDHandler.getAllReservasFromDB(context);
            List<Excursion> excursionList = ExcursionBDHandler.getAllExcursionesfromDB(context);
            List<TTOO> agenciasList = TTOOBDHandler.getAllTTOOfromDB(context);
            List<Hotel> hotelList = HotelBDHandler.getAllHotelesfromDB(context);
            if(reservasList.isEmpty() && excursionList.isEmpty() && agenciasList.isEmpty() && hotelList.isEmpty()){
                activity.runOnUiThread(() -> Toast.makeText(context,"No hay registros para exportar.",Toast.LENGTH_SHORT).show());
                return;
            }
            FileOutputStream fileOutputStream;
            ParcelFileDescriptor csvFile;
            try {
                SalvaBDStorageAccess salvaBDStorageAccess = new SalvaBDStorageAccess(context, DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
                DocumentFile file = salvaBDStorageAccess.getFile();
                if(file==null || !file.exists()){
                    activity.runOnUiThread(() -> {
                        myCallBack.requestCreateSelectAppDir(true);
                    });
                    return;
                }

                csvFile = context.getContentResolver().openFileDescriptor(file.getUri(), "w");
                fileOutputStream = new FileOutputStream(csvFile.getFileDescriptor());

                addConfig(fileOutputStream);
                if(!reservasList.isEmpty()) {
                    addResservasToExport(fileOutputStream,reservasList);
                }
                if(!excursionList.isEmpty()){
                    addExcursionesToExport(fileOutputStream,excursionList);
                }
                if(!agenciasList.isEmpty()){
                    addAgenciasToExport(fileOutputStream,agenciasList);
                }
                if(!hotelList.isEmpty()){
                    addHotelesToExport(fileOutputStream,hotelList);
                }
                fileOutputStream.close();
                csvFile.close();
                myCallBack.showSnackBar("Salva creada correctamente: "+salvaBDStorageAccess.getFileName());
            } catch (Exception e) {
                Log.d("Exportando","Error creando salva: "+e.getMessage());
            }
        }).start();
    }

    private void addConfig(FileOutputStream fileWriter){
        try{
            fileWriter.write(("Config\n").getBytes());
            fileWriter.write((MySharedPreferences.getNombreVendedor(context)).getBytes());//nombre
            fileWriter.write(("|").getBytes());
            fileWriter.write((MySharedPreferences.getTelefonoVendedor(context)).getBytes());//telefono
            fileWriter.write(("|").getBytes());
            fileWriter.write((MySharedPreferences.getAgenciaVendedor(context)).getBytes());//agencia
            fileWriter.write(("|").getBytes());
            fileWriter.write((String.valueOf(MySharedPreferences.getFragmentInicio(context))).getBytes());//frag inicio
            fileWriter.write(("|").getBytes());
            fileWriter.write((String.valueOf(MySharedPreferences.getPredecirPrecio(context))).getBytes());//predecir precio
            fileWriter.write(("|").getBytes());
            fileWriter.write((String.valueOf(MySharedPreferences.getIncluirDevEnLiquidacion(context))).getBytes());//incluir dev en liquidacion
            fileWriter.write(("|").getBytes());
            fileWriter.write((String.valueOf(MySharedPreferences.getIncluirPrecioCUP(context))).getBytes());//incluir precio en cup
            fileWriter.write(("|").getBytes());
            fileWriter.write((String.valueOf(MySharedPreferences.getTasaCUP(context))).getBytes());//tasa de cambio
            fileWriter.write(("|").getBytes());
            String mails = MySharedPreferences.getMails(context);
            if(!mails.isEmpty()) {
                fileWriter.write(mails.getBytes());//mail repVenta
            }else {
                fileWriter.write("null".getBytes());
            }
            fileWriter.write(("|").getBytes());
            fileWriter.write((String.valueOf(MySharedPreferences.getTipoFechaFiltrar(context))).getBytes());//fecha a filtrar
            fileWriter.write(("\n").getBytes());
        }catch (Exception e){
            Log.e("exportando","Error exportando configuracion.",e);
        }
    }

    private void addHotelesToExport(FileOutputStream fileWriter, List<Hotel> hoteles){
        try{
            String firstRow = HotelBDHandler.TABLE_NAME+"\n";
            fileWriter.write((firstRow).getBytes());
            for(Hotel hotel:hoteles){
                fileWriter.write((String.valueOf(hotel.getId())).getBytes());//0
                fileWriter.write(("|").getBytes());
                fileWriter.write((hotel.getNombre()).getBytes());//1
                fileWriter.write(("\n").getBytes());
            }
        }catch (Exception e){
            Log.e("exportando","Error exportando hoteles.",e);
        }
    }

    private void addAgenciasToExport(FileOutputStream fileWriter, List<TTOO> agencias){
        try{
            String firstRow = TTOOBDHandler.TABLE_NAME+"\n";
            fileWriter.write((firstRow).getBytes());
            for(TTOO ttoo:agencias){
                fileWriter.write((String.valueOf(ttoo.getId())).getBytes());//0
                fileWriter.write(("|").getBytes());
                fileWriter.write((ttoo.getNombre()).getBytes());//1
                fileWriter.write(("\n").getBytes());
            }
        }catch (Exception e){
            Log.e("exportando","Error exportando agencias.",e);
        }
    }

    private void addExcursionesToExport(FileOutputStream fileWriter, List<Excursion> excursiones){
        try{
            String firstRow = ExcursionBDHandler.TABLE_NAME+"\n";
            fileWriter.write((firstRow).getBytes());
            for(Excursion excursion:excursiones){
                fileWriter.write((String.valueOf(excursion.getId())).getBytes());//0
                fileWriter.write(("|").getBytes());
                fileWriter.write((excursion.getNombre()).getBytes());//1
                fileWriter.write(("|").getBytes());
                fileWriter.write((String.valueOf(excursion.getTipoPrecio())).getBytes());//2
                fileWriter.write(("|").getBytes());
                fileWriter.write((String.valueOf(excursion.getPrecioAd())).getBytes());//3
                fileWriter.write(("|").getBytes());
                fileWriter.write((String.valueOf(excursion.getPrecioMenor())).getBytes());//4
                fileWriter.write(("|").getBytes());
                fileWriter.write((String.valueOf(excursion.getPrecioAcomp())).getBytes());//5
                fileWriter.write(("|").getBytes());
                fileWriter.write((String.valueOf(excursion.getPrecioRango())).getBytes());//6
                fileWriter.write(("|").getBytes());
                fileWriter.write((String.valueOf(excursion.getIdiomaNecesario())).getBytes());//7
                fileWriter.write(("|").getBytes());
                fileWriter.write((String.valueOf(excursion.getRangoHasta())).getBytes());//8
                fileWriter.write(("\n").getBytes());
            }
        }catch (Exception e){
            Log.e("exportando","Error exportando excursiones.",e);
        }
    }

    private void addResservasToExport(FileOutputStream fileWriter, List<Reserva> reservas){
        try{
            String firstRow = ReservaBDHandler.TABLE_NAME+"\n";
            fileWriter.write((firstRow).getBytes());
            for(Reserva reserva:reservas){
                writeInFile(fileWriter,reserva.getNoTE());
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,formatStringToExport(reserva.getExcursion()));
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,reserva.getAgencia());
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,reserva.getNoHab());
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,reserva.getCliente());
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,reserva.getHotel());
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,reserva.getFechaConfeccion());
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,reserva.getFechaEjecucion());
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,reserva.getFechaOriginalEjecucion());
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,String.valueOf(reserva.incluirEnRepVenta()));
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,reserva.getFechaReporteVenta());
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,String.valueOf(reserva.getAdultos()));
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,String.valueOf(reserva.getMenores()));
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,String.valueOf(reserva.getInfantes()));
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,String.valueOf(reserva.getAcompanantes()));
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,reserva.getIdioma());
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,String.valueOf(reserva.getPrecio()));
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,String.valueOf(reserva.getEstado()));
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,reserva.getFechaDevolucion());
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,reserva.getFechaCancelacion());
                fileWriter.write(("|").getBytes());
                writeInFile(fileWriter,String.valueOf(reserva.getImporteDevuelto()));
                fileWriter.write(("|").getBytes());
                if(reserva.getHistorial()!=null) {//21
                    fileWriter.write((formatStringToExport(reserva.getHistorial())).getBytes());
                }else {
                    fileWriter.write(("").getBytes());
                }
                fileWriter.write(("|").getBytes());
                if(reserva.getObservaciones()==null || reserva.getObservaciones().isEmpty()){//22
                    fileWriter.write(("null").getBytes());
                }else {
                    String observaciones = formatStringToExport(reserva.getObservaciones());
                    fileWriter.write((observaciones).getBytes());
                }
                fileWriter.write(("\n").getBytes());
            }
        }catch (Exception e){
            Log.e("exportando","Error exportando reservas.",e);
        }
    }

    private void writeInFile(FileOutputStream fileOutputStream,String info){
        try {
            if(info==null){
                fileOutputStream.write(("null").getBytes());
            }else {
                fileOutputStream.write(info.getBytes());
            }
        }catch (Exception e){
            Log.e("Exportando","Error exportando BD: "+e.getMessage(),e);
        }
    }

    private String formatStringToExport(String texto){
        if(texto!=null) {
            return texto.replace("\n", "^");
        }
        return "";
    }

}
