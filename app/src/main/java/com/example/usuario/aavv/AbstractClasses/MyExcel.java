package com.example.usuario.aavv.AbstractClasses;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.example.usuario.aavv.Reservas.Reserva;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public abstract class MyExcel {

    protected int ROW_INDEX;

    protected Workbook workbook;
    protected Sheet sheet;
    protected Cell cell;
    protected CellStyle headerCellStyle, centerStyle, wrappedStyle, borderThinStyle, precioStyle,
            totalStyle, numberStyle;

    protected List<Reserva> reservaList;
    protected DocumentFile file;
    protected Context ctx;

    protected double TOTAL;

    protected StorageAccessAbstractClass storageAccess;


    protected MyExcel(List<Reserva> reservaList, StorageAccessAbstractClass storageAccess) {
        this.reservaList = reservaList;
        this.storageAccess = storageAccess;
        file = storageAccess.getFile();
        ctx = storageAccess.getContext();
        TOTAL = 0;
        ROW_INDEX = 0;
    }

    protected abstract String getSheetName();
    protected abstract void configureColumnWidth();
    protected abstract void showGeneralInfo();
    protected abstract void setHeaderRow();
    protected abstract void fillDataIntoExcel();
    protected abstract int getNumberOfColumns();
    protected abstract boolean hasRowTotal();

    /**
     * Override if you need to add info at the end of the excel
     */
    protected void addFootInfo(){

    }

    protected void addRowTotal() {
        Row rowData = sheet.createRow(ROW_INDEX++);
        for(int i=0;i<getNumberOfColumns()-1;i++){
            cell = rowData.createCell(i);
            cell.setCellStyle(headerCellStyle);
        }
        cell = rowData.createCell(getNumberOfColumns()-1);
        cell.setCellValue(TOTAL);
        cell.setCellStyle(totalStyle);
    }

    private void setBodyCellStyle(){
        centerStyle = workbook.createCellStyle();
        centerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        setBorderStyle(centerStyle,CellStyle.BORDER_THIN);

        wrappedStyle = workbook.createCellStyle();
        wrappedStyle.setWrapText(true);
        setBorderStyle(wrappedStyle,CellStyle.BORDER_THIN);

        borderThinStyle = workbook.createCellStyle();
        setBorderStyle(borderThinStyle,CellStyle.BORDER_THIN);

        precioStyle = workbook.createCellStyle();
        precioStyle.setAlignment(CellStyle.ALIGN_CENTER);
        precioStyle.setDataFormat(workbook.createDataFormat().getFormat("#.00"));
        setBorderStyle(precioStyle,CellStyle.BORDER_THIN);

        numberStyle = workbook.createCellStyle();
        numberStyle.setAlignment(CellStyle.ALIGN_CENTER);
        numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#"));
        setBorderStyle(numberStyle, CellStyle.BORDER_THIN);

        setTotalCellStyle();
    }

    protected void setTotalCellStyle(){
        totalStyle = workbook.createCellStyle();
        totalStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        totalStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        totalStyle.setAlignment(CellStyle.ALIGN_CENTER);
        totalStyle.setDataFormat(workbook.createDataFormat().getFormat("#.00"));
        setBorderStyle(totalStyle,CellStyle.BORDER_MEDIUM);
    }

    protected void setHeaderCellStyle() {
        headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        setBorderStyle(headerCellStyle,CellStyle.BORDER_MEDIUM);
    }

    protected void setBorderStyle(CellStyle cellStyle, short borderStyle){
        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setBorderRight(borderStyle);
        cellStyle.setBorderTop(borderStyle);
    }

    private void addBlankRows(){
        for(int i=0;i<2;i++){
            Row rowData = sheet.createRow(ROW_INDEX++);
            for(int y=0;y<getNumberOfColumns();y++){
                cell = rowData.createCell(y);
                cell.setCellStyle(borderThinStyle);
            }
        }
    }

    protected void fillRowGeneralInfo(String name, String value){
        Row row = sheet.createRow(ROW_INDEX++);

        cell = row.createCell(0);
        cell.setCellStyle(borderThinStyle);
        cell.setCellValue(name);

        cell = row.createCell(1);
        cell.setCellStyle(borderThinStyle);
        cell.setCellValue(value);
    }

    private void createNewSheet(){
        sheet = workbook.createSheet(getSheetName());
    }

    private void configureSheetToFitOnePage(){
        PrintSetup printSetup = sheet.getPrintSetup();
        sheet.setAutobreaks(true);
        printSetup.setFitHeight((short) 1);
        printSetup.setFitWidth((short) 1);
    }

    private void setPrintArea(){
        workbook.setPrintArea(
                0, //sheet index
                0, //start column
                getNumberOfColumns(), //end column
                0, //start row
                ROW_INDEX // end row
        );
    }

    public boolean generarExcel(){
        if(file==null || !file.exists()){
            return false;
        }
        workbook = new HSSFWorkbook();
        setHeaderCellStyle();
        setBodyCellStyle();

        createNewSheet();
        configureColumnWidth();

        showGeneralInfo();
        setHeaderRow();
        fillDataIntoExcel();
        addBlankRows();
        if(hasRowTotal()) {
            addRowTotal();
        }

        configureSheetToFitOnePage();
        setPrintArea();
        addFootInfo();
        return storeExcelInStorage();
    }

    protected boolean storeExcelInStorage() {
        String TAG = "MyExcel";
        boolean isSuccess;
        FileOutputStream fileOutputStream = null;
        ParcelFileDescriptor excel = null;

        try {
            excel = ctx.getContentResolver().
                    openFileDescriptor(file.getUri(), "w");
            fileOutputStream =
                    new FileOutputStream(excel.getFileDescriptor());
            workbook.write(fileOutputStream);
            Log.d(TAG, "Writing file" + file);
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
                if(excel != null){
                    excel.close();
                }
            } catch (Exception ex) {
                Log.e(TAG,ex.getMessage(),ex);
                //ex.printStackTrace();
            }
        }
        return isSuccess;
    }

}
