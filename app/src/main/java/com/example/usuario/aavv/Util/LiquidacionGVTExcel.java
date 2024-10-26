package com.example.usuario.aavv.Util;

import android.util.Log;

import com.example.usuario.aavv.AbstractClasses.MyExcel;
import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Reservas.Reserva;
import com.example.usuario.aavv.StorageAccess.LiquidacionGVTStorageAccess;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.util.IOUtils;

import java.io.InputStream;
import java.util.List;

public class LiquidacionGVTExcel extends MyExcel {

    private final String fecha;

    public LiquidacionGVTExcel(List<Reserva> reservaList, LiquidacionGVTStorageAccess liquidacionStorageAccess) {
        super(reservaList, liquidacionStorageAccess);
        fecha = liquidacionStorageAccess.getFecha();
    }

    @Override
    protected String getSheetName() {
        return fecha.replace("/","");
    }

    @Override
    protected void configureColumnWidth() {
        sheet.setColumnWidth(0, (18 * 256));//TE
        sheet.setColumnWidth(1, (11 * 256));//Efectivo CUP
        sheet.setColumnWidth(2, (11 * 256));//Cheque CUP
        sheet.setColumnWidth(3, (11 * 256));//Pos CUP
        sheet.setColumnWidth(4, (11 * 256));//Pos USD
        sheet.setColumnWidth(5, (11 * 256));//Transfermovil CUP
        sheet.setColumnWidth(6, (11 * 256));//Transfermovil USD
        sheet.setColumnWidth(7, (11 * 256));//Enzona CUP
        sheet.setColumnWidth(8, (11 * 256));//Enzona USD
        sheet.setColumnWidth(9, (11 * 256));//Total
    }

    @Override
    protected void showGeneralInfo() {
        //addLogo();
        addRegionsWithStyle(new CellRangeAddress(
                0,
                3,
                0,
                1
        ),CellStyle.BORDER_NONE);
        Row row = sheet.createRow(ROW_INDEX++);
        cell = row.createCell(2);
        cell.setCellValue("       Anexo Liquidación de Ventas de Opcionales\n" +
                "       Fecha: "+fecha+"\n" +
                "       U.E.B GAVIOTA TOUR CENTRO\n" +
                "       CLIENTE (Agencia): "+MySharedPreferences.getAgenciaVendedor(ctx));
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
        cell.setCellStyle(cellStyle);
        addRegionsWithStyle(new CellRangeAddress(
                0,
                3,
                2,
                getNumberOfColumns()-1
        ), CellStyle.BORDER_NONE);
        ROW_INDEX += 3;
    }

    private void addLogo(){
        /*try{
            //InputStream is = new FileInputStream("src/main/res/images/gvt_logo.jpg");
            InputStream is = ctx.getResources().openRawResource(R.raw.gvt_logo_invertido);
            byte[] bytes = IOUtils.toByteArray(is);
            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
            is.close();

            CreationHelper helper = workbook.getCreationHelper();

            Drawing drawing = sheet.createDrawingPatriarch();

            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(1);
            anchor.setRow1(1);
            Picture picture = drawing.createPicture(anchor, pictureIdx);
            //picture.resize(2);
        } catch (Exception e){
            Log.e("Excel","Error añadiendo picture.",e);
        }*/
    }

    @Override
    protected void setHeaderRow() {
        Row row1 = sheet.createRow(ROW_INDEX++);
        Row row2 = sheet.createRow(ROW_INDEX++);
        Row row3 = sheet.createRow(ROW_INDEX++);

        cell = row1.createCell(0);
        cell.setCellValue("Ticket");
        cell.setCellStyle(headerCellStyle);

        cell = row1.createCell(1);
        cell.setCellValue("Efectivo\nCUP");
        cell.setCellStyle(headerCellStyle);

        cell = row1.createCell(2);
        cell.setCellValue("Cheque\nCUP");
        cell.setCellStyle(headerCellStyle);

        cell = row1.createCell(3);
        cell.setCellValue("Tarjeta de crédito (importe siempre en CUP)");
        cell.setCellStyle(headerCellStyle);

        cell = row2.createCell(3);
        cell.setCellValue("Pos");
        cell.setCellStyle(headerCellStyle);

        cell = row2.createCell(5);
        cell.setCellValue("Transfermovil");
        cell.setCellStyle(headerCellStyle);

        cell = row2.createCell(7);
        cell.setCellValue("Enzona");
        cell.setCellStyle(headerCellStyle);

        cell = row2.createCell(9);
        cell.setCellValue("Total");
        cell.setCellStyle(headerCellStyle);

        for(int i = 3; i < 9; i++){
            cell = row3.createCell(i);
            if(i % 2 == 0){
                cell.setCellValue("USD");
            } else {
                cell.setCellValue("CUP");
            }
            cell.setCellStyle(headerCellStyle);
        }
        addHeadersRegionsWithStyle();
    }

    private void addHeadersRegionsWithStyle(){
        ROW_INDEX -= 3;
        short border = CellStyle.BORDER_THIN;
        for(int i = 0; i < 3; i++){
            addRegionsWithStyle(new CellRangeAddress(
                    ROW_INDEX,
                    ROW_INDEX+2,
                    i,
                    i
            ), border);
        }
        addRegionsWithStyle(new CellRangeAddress(
                ROW_INDEX,
                ROW_INDEX,
                3,
                9
        ), border);
        ROW_INDEX++;
        for (int i = 3; i < 8; i+=2){
            addRegionsWithStyle(new CellRangeAddress(
                    ROW_INDEX,
                    ROW_INDEX,
                    i,
                    i+1
            ), border);
        }
        addRegionsWithStyle(new CellRangeAddress(
                ROW_INDEX,
                ROW_INDEX+1,
                9,
                9
        ), border);
        ROW_INDEX += 2;
    }

    private void addRegionsWithStyle(CellRangeAddress region, short border){
        sheet.addMergedRegion(region);
        setRegionBorder(region, border);
    }


    private void setRegionBorder(CellRangeAddress region, short border){
        RegionUtil.setBorderTop(border, region,sheet,workbook);
        RegionUtil.setBorderBottom(border, region,sheet,workbook);
        RegionUtil.setBorderLeft(border, region,sheet,workbook);
        RegionUtil.setBorderRight(border, region,sheet,workbook);
    }

    @Override
    protected void setHeaderCellStyle() {
        headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headerCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        headerCellStyle.setWrapText(true);
        setBorderStyle(headerCellStyle,CellStyle.BORDER_THIN);
    }

    @Override
    protected void setTotalCellStyle() {
        totalStyle = workbook.createCellStyle();
        totalStyle.setAlignment(CellStyle.ALIGN_CENTER);
        totalStyle.setDataFormat(workbook.createDataFormat().getFormat("#.00"));
        setBorderStyle(totalStyle,CellStyle.BORDER_THIN);
    }

    @Override
    protected void fillDataIntoExcel() {
        double tasaCUP = MySharedPreferences.getTasaCUP(ctx);
        if(tasaCUP == 0){ tasaCUP = 120; }
        for (int i = 0; i < reservaList.size(); i++) {
            Row rowData = sheet.createRow(ROW_INDEX++);

            double precioCUP = 0;
            if(reservaList.get(i).getCriterioSeleccion() == Reserva.Criterio_Seleccion.FECHA_DEVOLUCION){
                precioCUP = reservaList.get(i).getImporteDevuelto()*tasaCUP*-1;
            } else if(reservaList.get(i).getCriterioSeleccion() == Reserva.Criterio_Seleccion.FECHA_CONFECCION){
                if(reservaList.get(i).getEstado() != Reserva.ESTADO_CANCELADO){
                    precioCUP = reservaList.get(i).getPrecio()*tasaCUP;
                }
            }
            TOTAL += precioCUP;

            cell = rowData.createCell(0);//TE
            cell.setCellValue(reservaList.get(i).getNoTE());
            cell.setCellStyle(centerStyle);

            cell = rowData.createCell(1);//Efectivo CUP
            cell.setCellStyle(centerStyle);

            cell = rowData.createCell(2);//Cheque CUP
            cell.setCellStyle(centerStyle);

            cell = rowData.createCell(3);//Pos CUP
            cell.setCellStyle(centerStyle);

            cell = rowData.createCell(4);//Pos USD
            cell.setCellValue(precioCUP);
            cell.setCellStyle(precioStyle);

            cell = rowData.createCell(5);//Transfermovil CUP
            cell.setCellStyle(centerStyle);

            cell = rowData.createCell(6);//Transfermovil USD
            cell.setCellStyle(centerStyle);

            cell = rowData.createCell(7);//Enzona CUP
            cell.setCellStyle(centerStyle);

            cell = rowData.createCell(8);//Enzona USD
            cell.setCellStyle(centerStyle);

            cell = rowData.createCell(9);//Total
            cell.setCellValue(precioCUP);
            cell.setCellStyle(precioStyle);
        }
    }

    @Override
    protected boolean hasRowTotal() {
        return true;
    }

    @Override
    protected void addRowTotal() {
        Row rowData = sheet.createRow(ROW_INDEX++);
        for(int i=0;i<getNumberOfColumns()-1;i++){
            cell = rowData.createCell(i);
            if(i == 0){
                cell.setCellValue("Totales (sumar)");
                cell.setCellStyle(headerCellStyle);
            }
            if(i == 4){
                cell.setCellValue(TOTAL);
                cell.setCellStyle(totalStyle);
            } else {
                cell.setCellStyle(headerCellStyle);
            }
        }
        cell = rowData.createCell(getNumberOfColumns()-1);
        cell.setCellValue(TOTAL);
        cell.setCellStyle(totalStyle);
    }

    @Override
    protected int getNumberOfColumns() {
        return 10;
    }

    @Override
    protected void addFootInfo() {
        Row row;
        row = sheet.createRow(ROW_INDEX);
        cell = row.createCell(0);
        cell.setCellValue("Observaciones:");

        addRegionsWithStyle(new CellRangeAddress(
                ROW_INDEX,
                ROW_INDEX+1,
                0,
                getNumberOfColumns()-1
        ), CellStyle.BORDER_THIN);
        ROW_INDEX++;
        for (int i = 0; i < 2; i++){
            ROW_INDEX++;
            addRegionsWithStyle(new CellRangeAddress(
                    ROW_INDEX,
                    ROW_INDEX,
                    0,
                    getNumberOfColumns()-1
            ), CellStyle.BORDER_THIN);
        }
        ROW_INDEX++;
        row = sheet.createRow(ROW_INDEX);
        cell = row.createCell(0);
        String value = "Vendedor:__________________________________________      Firma:____________________\n" +
                "Cajero:_____________________________________________      Firma:____________________\n" +
                "Liquidado:_________________________________________\n" +
                "Impuesto:__________________________________________\n" +
                "Comisión TTOO:_____________________________________";
        cell.setCellValue(value);
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setWrapText(true);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        cell.setCellStyle(style);
        addRegionsWithStyle(new CellRangeAddress(
                ROW_INDEX,
                ROW_INDEX+5,
                0,
                getNumberOfColumns()-1
        ), CellStyle.BORDER_THIN);
    }
}

/*
Vendedor:__________________________________________      Firma:____________________
Cajero:_____________________________________________      Firma:____________________
Liquidado:_________________________________________
Impuesto:__________________________________________
Comisión TTOO:_____________________________________
*/