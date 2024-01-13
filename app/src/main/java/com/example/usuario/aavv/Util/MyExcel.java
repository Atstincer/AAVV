package com.example.usuario.aavv.Util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
import com.example.usuario.aavv.Reservas.Reserva;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * Created by usuario on 7/09/2023.
 */

public class MyExcel {

    private static String TAG = "MyExcel";

    private static int ROW_INDEX;

    private static Workbook workbook = null;
    private static Sheet sheet = null;
    private static Cell cell = null;
    private static CellStyle headerCellStyle;
    private static CellStyle centerStyle;
    private static CellStyle wrappedStyle;
    private static CellStyle borderThickStyle;
    private static CellStyle precioStyle;
    private static CellStyle totalStyle;


    public static boolean generarExcelReporteVentaPorAgencia(File file, List<Reserva> listaReservas, String agencia, String desde, String hasta, double total) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Almacenamiento no disponible o de solo lectura");
            return false;
        }

        Modelo modelo = Modelo.REPORTE_VENTA_AGENCIA;
        if(agencia.toLowerCase().trim().contains("meeting point")){
            modelo = Modelo.REPORTE_VENTA_MEETING_POINT;
        }

        ROW_INDEX = 0;

        // Creating a New HSSF Workbook (.xls format)
        workbook = new HSSFWorkbook();

        setHeaderCellStyle();
        setBodyCellStyle();

        String sheetName = desde.replace("/","") + " - " + hasta.replace("/","");

        createNewSheet(sheetName);
        configureColumnWidth(modelo);

        showGeneralInfo(agencia,desde,hasta);
        setHeaderRow(modelo);
        fillDataIntoExcel(listaReservas,modelo);

        int columns = 6;
        if(modelo == Modelo.REPORTE_VENTA_MEETING_POINT){
            columns = 16;
        }
        addBlankRows(columns, 2, borderThickStyle);
        addRowTotal(columns,total,modelo);

        return storeExcelInStorage(file);
    }

    public static boolean generarExcelReporteVenta(Context ctx, File file, List<Reserva> listaReservas, String fecha) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Almacenamiento no disponible o de solo lectura");
            return false;
        }

        ROW_INDEX = 0;

        // Creating a New HSSF Workbook (.xls format)
        workbook = new HSSFWorkbook();

        setHeaderCellStyle();
        setBodyCellStyle();

        String sheetName = "Venta " + listaReservas.get(0).getFechaConfeccion().substring(0,5).replace("/",".");

        createNewSheet(sheetName);
        configureColumnWidth(Modelo.REPORTE_VENTA);

        showGeneralInfo(ctx,fecha);
        setHeaderRow(Modelo.REPORTE_VENTA);
        fillDataIntoExcel(listaReservas,Modelo.REPORTE_VENTA);

        return storeExcelInStorage(file);
    }

    private static boolean isExternalStorageReadOnly() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState);
    }

    private static boolean isExternalStorageAvailable() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(externalStorageState);
    }

    private static void setHeaderCellStyle() {
        headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        setBorderStyle(headerCellStyle,CellStyle.BORDER_THICK);
    }

    private static void setBodyCellStyle(){
        centerStyle = workbook.createCellStyle();
        centerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        setBorderStyle(centerStyle,CellStyle.BORDER_THICK);

        wrappedStyle = workbook.createCellStyle();
        wrappedStyle.setWrapText(true);
        setBorderStyle(wrappedStyle,CellStyle.BORDER_THICK);

        borderThickStyle = workbook.createCellStyle();
        setBorderStyle(borderThickStyle,CellStyle.BORDER_THICK);

        precioStyle = workbook.createCellStyle();
        precioStyle.setAlignment(CellStyle.ALIGN_CENTER);
        precioStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
        setBorderStyle(precioStyle,CellStyle.BORDER_THICK);

        totalStyle = workbook.createCellStyle();
        totalStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        totalStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        totalStyle.setAlignment(CellStyle.ALIGN_CENTER);
        totalStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
        setBorderStyle(totalStyle,CellStyle.BORDER_THICK);
    }

    private static void setBorderStyle(CellStyle cellStyle, short borderStyle){
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setBorderRight(borderStyle);
        cellStyle.setBorderTop(borderStyle);
    }

    private static void showGeneralInfo(Context ctx, String fechaVenta){
        fillRowGeneralInfo("Venta:",fechaVenta);

        if(!MySharedPreferences.getNombreVendedor(ctx).equals("")){
            fillRowGeneralInfo("Vendedor:",MySharedPreferences.getNombreVendedor(ctx));
        }
        if(!MySharedPreferences.getTelefonoVendedor(ctx).equals("")){
            fillRowGeneralInfo("Contacto:",MySharedPreferences.getTelefonoVendedor(ctx));
        }
        if(!MySharedPreferences.getAgenciaVendedor(ctx).equals("")){
            fillRowGeneralInfo("Agencia:",MySharedPreferences.getAgenciaVendedor(ctx));
        }
        ROW_INDEX ++;
    }

    private static void showGeneralInfo(String agencia, String desde, String hasta){
        fillRowGeneralInfo("Agencia:",agencia);
        fillRowGeneralInfo("Desde:",desde);
        fillRowGeneralInfo("Hasta",hasta);
        ROW_INDEX ++;
    }

    private static void fillRowGeneralInfo(String name, String value){
        Row row = sheet.createRow(ROW_INDEX);
        cell = row.createCell(0);
        cell.setCellStyle(borderThickStyle);
        cell.setCellValue(name);

        cell = row.createCell(1);
        cell.setCellStyle(borderThickStyle);
        cell.setCellValue(value);

        ROW_INDEX ++;
    }

    private static void setHeaderRow(Modelo modelo) {
        Row headerRow = sheet.createRow(ROW_INDEX);

        switch (modelo){
            case REPORTE_VENTA:
                cell = headerRow.createCell(0);
                cell.setCellValue("TICKET");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(1);
                cell.setCellValue("OPCIONALES");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(2);
                cell.setCellValue("FECHA");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(3);
                cell.setCellValue("ADULTOS");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(4);
                cell.setCellValue("MENORES");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(5);
                cell.setCellValue("IDIOMA");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(6);
                cell.setCellValue("HOTEL");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(7);
                cell.setCellValue("HAB");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(8);
                cell.setCellValue("OBSERVACIONES");
                cell.setCellStyle(headerCellStyle);
                break;
            case REPORTE_VENTA_AGENCIA:
                cell = headerRow.createCell(0);
                cell.setCellValue("TICKET");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(1);
                cell.setCellValue("EMISION");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(2);
                cell.setCellValue("EJECUCION");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(3);
                cell.setCellValue("PAX");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(4);
                cell.setCellValue("EXCURSION");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(5);
                cell.setCellValue("IMPORTE(USD)");
                cell.setCellStyle(headerCellStyle);
                break;
            case REPORTE_VENTA_MEETING_POINT:
                cell = headerRow.createCell(0);
                cell.setCellValue("No Tikect");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(1);
                cell.setCellValue("Referencia");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(2);
                cell.setCellValue("Excursión");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(3);
                cell.setCellValue("Prestatario");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(4);
                cell.setCellValue("Idioma");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(5);
                cell.setCellValue("Fecha Excursion");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(6);
                cell.setCellValue("Fecha Venta");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(7);
                cell.setCellValue("Turoperador");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(8);
                cell.setCellValue("Hotel");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(9);
                cell.setCellValue("Habitacion");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(10);
                cell.setCellValue("Adultos");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(11);
                cell.setCellValue("Niños");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(12);
                cell.setCellValue("Nombre Cliente");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(13);
                cell.setCellValue("Importe USD");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(14);
                cell.setCellValue("Estado");
                cell.setCellStyle(headerCellStyle);

                cell = headerRow.createCell(15);
                cell.setCellValue("Imp Devolucion");
                cell.setCellStyle(headerCellStyle);
                break;
        }


        ROW_INDEX ++;
    }

    private static void createNewSheet(String sheetName){
        sheet = workbook.createSheet(sheetName);
    }

    private static void configureColumnWidth(Modelo modelo){
        switch (modelo){
            case REPORTE_VENTA:
                sheet.setColumnWidth(0, (10 * 256));//TE
                sheet.setColumnWidth(1, (30 * 256));//Opcionales
                sheet.setColumnWidth(2, (15 * 256));//Fecha
                sheet.setColumnWidth(3, (9 * 256));//Adultos
                sheet.setColumnWidth(4, (9 * 256));//Menores
                sheet.setColumnWidth(5, (10 * 256));//Idioma
                sheet.setColumnWidth(6, (15 * 256));//Hotel
                sheet.setColumnWidth(7, (6 * 256));//Hab
                sheet.setColumnWidth(8, (30 * 256));//Observaciones
                break;
            case REPORTE_VENTA_AGENCIA:
                sheet.setColumnWidth(0, (10 * 256));//TE
                sheet.setColumnWidth(1, (15 * 256));//Emision
                sheet.setColumnWidth(2, (15 * 256));//Ejecucion
                sheet.setColumnWidth(3, (8 * 256));//Pax
                sheet.setColumnWidth(4, (40 * 256));//Excursion
                sheet.setColumnWidth(5, (15 * 256));//Importe(usd)
                break;
            case REPORTE_VENTA_MEETING_POINT:
                sheet.setColumnWidth(0, (11 * 256));//TE
                sheet.setColumnWidth(1, (11 * 256));//Referencia
                sheet.setColumnWidth(2, (30 * 256));//Excursion
                sheet.setColumnWidth(3, (15 * 256));//Prestatario
                sheet.setColumnWidth(4, (15 * 256));//Idioma
                sheet.setColumnWidth(5, (15 * 256));//Ejecucion
                sheet.setColumnWidth(6, (15 * 256));//Emision
                sheet.setColumnWidth(7, (15 * 256));//TTOO
                sheet.setColumnWidth(8, (20 * 256));//Hotel
                sheet.setColumnWidth(9, (13 * 256));//Hab
                sheet.setColumnWidth(10, (13 * 256));//Adultos
                sheet.setColumnWidth(11, (10 * 256));//Niños
                sheet.setColumnWidth(12, (20 * 256));//Nombre
                sheet.setColumnWidth(13, (15 * 256));//Importe
                sheet.setColumnWidth(14, (15 * 256));//Estado
                sheet.setColumnWidth(15, (15 * 256));//Importe dev
                break;
        }
    }

    private static void fillDataIntoExcel(List<Reserva> listaReservas, Modelo modelo) {
        switch (modelo){
            case REPORTE_VENTA:
                for (int i = 0; i < listaReservas.size(); i++) {
                    // Create a New Row for every new entry in list
                    Row rowData = sheet.createRow(ROW_INDEX);

                    // Create Cells for each row
                    cell = rowData.createCell(0);//TE
                    cell.setCellValue(listaReservas.get(i).getNoTE());
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(1);//Opcionales
                    cell.setCellValue(listaReservas.get(i).getExcursion());
                    cell.setCellStyle(wrappedStyle);

                    cell = rowData.createCell(2);//Fecha
                    cell.setCellValue(listaReservas.get(i).getFechaEjecucion());
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(3);//Adultos
                    int value = listaReservas.get(i).getAdultos();
                    if(value == 0 && listaReservas.get(i).getAcompanantes() != 0){
                        value = listaReservas.get(i).getAcompanantes();
                    }
                    cell.setCellValue(value);
                    cell.setCellStyle(centerStyle);

                    int menores = listaReservas.get(i).getMenores() + listaReservas.get(i).getInfantes();
                    cell = rowData.createCell(4);//Menores
                    cell.setCellValue(menores);
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(5);//Idioma
                    cell.setCellValue(listaReservas.get(i).getIdioma());
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(6);//Hotel
                    cell.setCellValue(listaReservas.get(i).getHotel());
                    cell.setCellStyle(wrappedStyle);
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(7);//Hab
                    cell.setCellValue(listaReservas.get(i).getNoHab());
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(8);//Observaciones
                    cell.setCellValue(listaReservas.get(i).getObservaciones());
                    cell.setCellStyle(wrappedStyle);

                    ROW_INDEX ++;
                }
                break;
            case REPORTE_VENTA_AGENCIA:
                for (int i = 0; i < listaReservas.size(); i++) {
                    // Create a New Row for every new entry in list
                    Row rowData = sheet.createRow(ROW_INDEX);

                    // Create Cells for each row
                    cell = rowData.createCell(0);//TE
                    cell.setCellValue(listaReservas.get(i).getNoTE());
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(1);//Emision
                    cell.setCellValue(listaReservas.get(i).getFechaConfeccion());
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(2);//Ejecucion
                    cell.setCellValue(listaReservas.get(i).getFechaEjecucion());
                    cell.setCellStyle(centerStyle);

                    String pax = "";
                    int adultos = listaReservas.get(i).getAdultos();
                    int menores = listaReservas.get(i).getMenores()+listaReservas.get(i).getInfantes();
                    int acompanantes = listaReservas.get(i).getAcompanantes();
                    if(adultos>0){
                        pax += String.valueOf(adultos);
                    }
                    if(menores>0){
                        if(adultos>0){
                            pax += "+";
                        }
                        pax += String.valueOf(menores);
                    }
                    if(acompanantes>0){
                        if(adultos>0 || menores>0){
                            pax += "+";
                        }
                        pax += String.valueOf(acompanantes);
                    }
                    cell = rowData.createCell(3);//Pax
                    cell.setCellValue(pax);
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(4);//Excursion
                    cell.setCellValue(listaReservas.get(i).getExcursion());
                    cell.setCellStyle(wrappedStyle);

                    cell = rowData.createCell(5);//Importe
                    cell.setCellValue(listaReservas.get(i).getPrecio());
                    cell.setCellStyle(precioStyle);

                    ROW_INDEX ++;
                }
                break;
            case REPORTE_VENTA_MEETING_POINT:
                for (int i = 0; i < listaReservas.size(); i++) {
                    // Create a New Row for every new entry in list
                    Row rowData = sheet.createRow(ROW_INDEX);

                    // Create Cells for each row
                    cell = rowData.createCell(0);//TE
                    cell.setCellValue(listaReservas.get(i).getNoTE());
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(1);//Referencia
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(2);//Excursion
                    cell.setCellValue(listaReservas.get(i).getExcursion());
                    cell.setCellStyle(wrappedStyle);

                    cell = rowData.createCell(3);//Prestatario
                    cell.setCellValue("Gaviota");
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(4);//Idioma
                    cell.setCellValue("Aleman");
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(5);//Ejecucion
                    cell.setCellValue(listaReservas.get(i).getFechaEjecucion());
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(6);//Emision
                    cell.setCellValue(listaReservas.get(i).getFechaConfeccion());
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(7);//TTOO
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(8);//Hotel
                    cell.setCellValue(listaReservas.get(i).getHotel());
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(9);//Hab
                    cell.setCellValue(listaReservas.get(i).getNoHab());
                    cell.setCellStyle(centerStyle);

                    int adultos = listaReservas.get(i).getAdultos();
                    if(adultos<1 && listaReservas.get(i).getAcompanantes()>0){
                        adultos = listaReservas.get(i).getAcompanantes();
                    }
                    cell = rowData.createCell(10);//Adultos
                    cell.setCellValue(adultos);
                    cell.setCellStyle(centerStyle);

                    int menores = listaReservas.get(i).getMenores()+listaReservas.get(i).getInfantes();
                    cell = rowData.createCell(11);//Menores
                    cell.setCellValue(menores);
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(12);//Nombre
                    cell.setCellValue(listaReservas.get(i).getCliente());
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(13);//Importe
                    cell.setCellValue(listaReservas.get(i).getPrecio());
                    cell.setCellStyle(precioStyle);

                    String estado = "";
                    if(listaReservas.get(i).getEstado() == Reserva.ESTADO_ACTIVO){
                        estado = "Vendido";
                    } else if(listaReservas.get(i).getEstado() == Reserva.ESTADO_DEVUELTO){
                        estado = "Devolucion";
                    } else if(listaReservas.get(i).getEstado() == Reserva.ESTADO_CANCELADO){
                        estado = "Cancelado";
                    }
                    cell = rowData.createCell(14);//Estado
                    cell.setCellValue(estado);
                    cell.setCellStyle(centerStyle);

                    cell = rowData.createCell(15);//Impo Devolucion
                    cell.setCellStyle(centerStyle);

                    ROW_INDEX ++;
                }
                break;
        }
    }

    private static void addBlankRows(int columns, int rows, CellStyle cellStile){
        for(int i=0;i<rows;i++){
            Row rowData = sheet.createRow(ROW_INDEX);
            for(int y=0;y<columns;y++){
                cell = rowData.createCell(y);
                cell.setCellStyle(cellStile);
            }
            ROW_INDEX ++;
        }
    }

    private static void addRowTotal(int columns,double total,Modelo modelo){
        Row rowData = sheet.createRow(ROW_INDEX);
        for(int i=0;i<columns-1;i++){
            cell = rowData.createCell(i);
            if(modelo == Modelo.REPORTE_VENTA_MEETING_POINT && i == (columns-3)){
                cell.setCellValue(total);
                cell.setCellStyle(totalStyle);
            } else {
                cell.setCellStyle(headerCellStyle);
            }
        }
        cell = rowData.createCell(columns-1);
        if(modelo == Modelo.REPORTE_VENTA_MEETING_POINT){
            cell.setCellStyle(headerCellStyle);
        }else {
            cell.setCellValue(total);
            cell.setCellStyle(totalStyle);
        }
    }

    private static boolean storeExcelInStorage(File file) {
        boolean isSuccess;
        //File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            Log.e(TAG, "Writing file" + file);
            isSuccess = true;
        } catch (IOException e) {
            Log.e(TAG, "Error writing Exception: ", e);
            isSuccess = false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to save file due to Exception: ", e);
            isSuccess = false;
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return isSuccess;
    }

    private enum Modelo{
        REPORTE_VENTA,
        REPORTE_VENTA_AGENCIA,
        REPORTE_VENTA_MEETING_POINT
    }

}
