package com.example.usuario.aavv.Util;

import com.example.usuario.aavv.AbstractClasses.MyExcel;
import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
import com.example.usuario.aavv.Reservas.Reserva;
import com.example.usuario.aavv.StorageAccess.RepVentaStorageAccess;

import org.apache.poi.ss.usermodel.Row;

import java.util.List;

public class RepVDiarioExcel extends MyExcel {

    private final String fecha;

    public RepVDiarioExcel(List<Reserva> reservaList, RepVentaStorageAccess repVentaStorageAccess) {
        super(reservaList, repVentaStorageAccess);
        fecha = repVentaStorageAccess.getFecha();
    }

    @Override
    protected boolean hasRowTotal() {
        return false;
    }

    @Override
    protected String getSheetName() {
        return "Venta " + fecha.substring(0,5).replace("/",".");
    }

    @Override
    protected void configureColumnWidth() {
        sheet.setColumnWidth(0, (10 * 256));//TE
        sheet.setColumnWidth(1, (30 * 256));//Opcionales
        sheet.setColumnWidth(2, (15 * 256));//Fecha
        sheet.setColumnWidth(3, (11 * 256));//Adultos
        sheet.setColumnWidth(4, (11 * 256));//Menores
        sheet.setColumnWidth(5, (10 * 256));//Idioma
        sheet.setColumnWidth(6, (18 * 256));//Hotel
        sheet.setColumnWidth(7, (6 * 256));//Hab
        sheet.setColumnWidth(8, (30 * 256));//Observaciones
    }



    @Override
    protected void showGeneralInfo() {
        fillRowGeneralInfo("Venta:",fecha);

        if(!MySharedPreferences.getNombreVendedor(ctx).isEmpty()){
            fillRowGeneralInfo("Vendedor:",MySharedPreferences.getNombreVendedor(ctx));
        }
        if(!MySharedPreferences.getTelefonoVendedor(ctx).isEmpty()){
            fillRowGeneralInfo("Contacto:",MySharedPreferences.getTelefonoVendedor(ctx));
        }
        if(!MySharedPreferences.getAgenciaVendedor(ctx).isEmpty()){
            fillRowGeneralInfo("Agencia:",MySharedPreferences.getAgenciaVendedor(ctx));
        }
        ROW_INDEX ++;
    }

    @Override
    protected void setHeaderRow() {
        Row headerRow = sheet.createRow(ROW_INDEX++);

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
    }

    @Override
    protected void fillDataIntoExcel() {
        for (int i = 0; i < reservaList.size(); i++) {
            Reserva reserva = reservaList.get(i);

            Row rowData = sheet.createRow(ROW_INDEX++);

            cell = rowData.createCell(0);//TE
            cell.setCellValue(reserva.getNoTE());
            cell.setCellStyle(centerStyle);

            if(reserva.getCriterioSeleccion() == Reserva.Criterio_Seleccion.FECHA_REP_VENTA){
                cell = rowData.createCell(1);//Opcionales
                cell.setCellValue(reserva.getExcursion());
                cell.setCellStyle(wrappedStyle);

                cell = rowData.createCell(2);//Fecha
                cell.setCellValue(reserva.getFechaEjecucion());
                cell.setCellStyle(centerStyle);

                cell = rowData.createCell(3);//Adultos
                int value = reserva.getAdultos();
                if(value == 0 && reserva.getAcompanantes() > 0){
                    value = reserva.getAcompanantes();
                }
                cell.setCellValue(value);
                cell.setCellStyle(centerStyle);

                int menores = reserva.getMenores() + reserva.getInfantes();
                cell = rowData.createCell(4);//Menores
                cell.setCellValue(menores);
                cell.setCellStyle(centerStyle);

                cell = rowData.createCell(5);//Idioma
                cell.setCellValue(reserva.getIdioma());
                cell.setCellStyle(centerStyle);

                cell = rowData.createCell(6);//Hotel
                cell.setCellValue(reserva.getHotel());
                cell.setCellStyle(wrappedStyle);
                cell.setCellStyle(centerStyle);

                cell = rowData.createCell(7);//Hab
                cell.setCellValue(reserva.getNoHab());
                cell.setCellStyle(centerStyle);

                cell = rowData.createCell(8);//Observaciones
                cell.setCellValue(reserva.getObservaciones());
                cell.setCellStyle(wrappedStyle);
            } else if (reserva.getCriterioSeleccion() == Reserva.Criterio_Seleccion.FECHA_DEVOLUCION) {
                cell = rowData.createCell(1);//Opcionales
                cell.setCellValue("Devolución");
                cell.setCellStyle(wrappedStyle);

                cell = rowData.createCell(2);//Fecha
                cell.setCellValue(reserva.getFechaDevolucion());
                cell.setCellStyle(centerStyle);

                cell = rowData.createCell(3);//Adultos
                cell.setCellStyle(centerStyle);

                cell = rowData.createCell(4);//Menores
                cell.setCellStyle(centerStyle);

                cell = rowData.createCell(5);//Idioma
                cell.setCellStyle(centerStyle);

                cell = rowData.createCell(6);//Hotel
                cell.setCellStyle(wrappedStyle);
                cell.setCellStyle(centerStyle);

                cell = rowData.createCell(7);//Hab
                cell.setCellStyle(centerStyle);

                cell = rowData.createCell(8);//Observaciones
                String obs = reserva.getObsDevolucion();
                if(obs != null && !obs.isEmpty()) {
                    cell.setCellValue(obs);
                }
                cell.setCellStyle(wrappedStyle);
            }
        }
    }

    @Override
    protected int getNumberOfColumns() {
        return 9;
    }
}
