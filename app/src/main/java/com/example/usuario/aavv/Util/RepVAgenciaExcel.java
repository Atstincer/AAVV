package com.example.usuario.aavv.Util;

import com.example.usuario.aavv.AbstractClasses.MyExcel;
import com.example.usuario.aavv.Reservas.Reserva;
import com.example.usuario.aavv.StorageAccess.VentaTTOOStorageAccess;

import org.apache.poi.ss.usermodel.Row;

import java.util.List;

public class RepVAgenciaExcel extends MyExcel {

    private final String desde, hasta, agencia;

    public RepVAgenciaExcel(List<Reserva> reservaList, VentaTTOOStorageAccess ventaTTOOStorageAccess) {
        super(reservaList, ventaTTOOStorageAccess);
        agencia = ventaTTOOStorageAccess.getAgencia();
        desde = ventaTTOOStorageAccess.getFecha();
        hasta = ventaTTOOStorageAccess.getHasta();
    }

    @Override
    protected int getNumberOfColumns() {
        return 6;
    }

    @Override
    protected String getSheetName() {
        return desde.replace("/","") + " - " + hasta.replace("/","");
    }

    @Override
    protected void configureColumnWidth() {
        sheet.setColumnWidth(0, (10 * 256));//TE
        sheet.setColumnWidth(1, (15 * 256));//Emision
        sheet.setColumnWidth(2, (15 * 256));//Ejecucion
        sheet.setColumnWidth(3, (8 * 256));//Pax
        sheet.setColumnWidth(4, (40 * 256));//Excursion
        sheet.setColumnWidth(5, (15 * 256));//Importe(usd)
    }

    @Override
    protected boolean hasRowTotal() {
        return true;
    }

    @Override
    protected void showGeneralInfo() {
        fillRowGeneralInfo("Agencia:",agencia);
        fillRowGeneralInfo("Desde:",desde);
        fillRowGeneralInfo("Hasta",hasta);
        ROW_INDEX ++;
    }

    @Override
    protected void setHeaderRow() {
        Row headerRow = sheet.createRow(ROW_INDEX++);

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
    }

    @Override
    protected void fillDataIntoExcel() {
        for (int i = 0; i < reservaList.size(); i++) {
            // Create a New Row for every new entry in list
            Row rowData = sheet.createRow(ROW_INDEX++);

            // Create Cells for each row
            cell = rowData.createCell(0);//TE
            cell.setCellValue(reservaList.get(i).getNoTE());
            cell.setCellStyle(centerStyle);

            cell = rowData.createCell(1);//Emision
            cell.setCellValue(reservaList.get(i).getFechaConfeccion());
            cell.setCellStyle(centerStyle);


            cell = rowData.createCell(2);//Ejecucion
            if(reservaList.get(i).getFechaOriginalEjecucion()==null ||
                    reservaList.get(i).getFechaOriginalEjecucion().isEmpty()) {
                cell.setCellValue(reservaList.get(i).getFechaEjecucion());
            }else {
                cell.setCellValue(reservaList.get(i).getFechaOriginalEjecucion());
            }
            cell.setCellStyle(centerStyle);

            String pax = "";
            int adultos = reservaList.get(i).getAdultos();
            int menores = reservaList.get(i).getMenores()+reservaList.get(i).getInfantes();
            int acompanantes = reservaList.get(i).getAcompanantes();
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
            cell.setCellValue(reservaList.get(i).getExcursion());
            cell.setCellStyle(wrappedStyle);

            cell = rowData.createCell(5);//Importe
            cell.setCellValue(reservaList.get(i).getSaldoFinal());
            cell.setCellStyle(precioStyle);

            TOTAL += reservaList.get(i).getSaldoFinal();
        }
    }

}
