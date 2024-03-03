package com.example.usuario.aavv.Reservas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.aavv.MainActivity;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;
import com.example.usuario.aavv.Util.MyExcel;
import com.example.usuario.aavv.Util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by usuario on 13/08/2023.
 */

public class FragmentVentaTTOO extends Fragment {

    public static final String TAG = "FragmentVentaTTOO";
    private final String TODAS = "Todas";

    private LinearLayout layoutInfo;
    private AppCompatSpinner spinnerAgencias;
    private TextView tvFechaDesde, tvFechaHasta, tvInfoVenta;
    private RecyclerView rvReservas;

    private ReservaRVAdapter rvAdapter;

    private MyCallBack myCallBack;

    private List<Reserva> listaReservas;
    private List<String> listaAgencias;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_venta_ttoo,container,false);
        bindComponents(view);
        setItUp();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        myCallBack = (MyCallBack)context;
        super.onAttach(context);
    }

    private void bindComponents(View view){
        layoutInfo = (LinearLayout)view.findViewById(R.id.layout_info);
        spinnerAgencias = (AppCompatSpinner)view.findViewById(R.id.sp_agencias);
        tvFechaDesde = (TextView)view.findViewById(R.id.tv_fecha_desde);
        tvFechaHasta = (TextView)view.findViewById(R.id.tv_fecha_hasta);
        tvInfoVenta = (TextView)view.findViewById(R.id.tv_info_venta);
        rvReservas = (RecyclerView)view.findViewById(R.id.rv_reservas);
    }

    private void setItUp(){
        myCallBack.udUI(TAG);
        if(tvFechaDesde.getText().toString().equals("fecha")) {
            String desde = "01" + DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR).substring(2, 10);
            tvFechaDesde.setText(desde);
        }
        if(tvFechaHasta.getText().toString().equals("fecha")) {
            //tvFechaHasta.setText(DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
            tvFechaHasta.setText(DateHandler.getLastDayOfMonth(MisConstantes.FormatoFecha.MOSTRAR));
        }
        layoutInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String texto = "Agencia: "+spinnerAgencias.getSelectedItem().toString()+"\n" +
                        "Desde: "+tvFechaDesde.getText().toString()+"\n" +
                        "Hasta: "+tvFechaHasta.getText().toString()+"\n\n" + tvInfoVenta.getText().toString();
                Util.copyToClipBoard(getContext(),texto);
                return true;
            }
        });
        tvFechaDesde.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DateHandler.showDatePicker(getContext(), tvFechaDesde, new DateHandler.DatePickerCallBack() {
                    @Override
                    public void dateSelected() {
                        showInfo();
                    }
                });
            }
        });
        tvFechaHasta.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DateHandler.showDatePicker(getContext(), tvFechaHasta, new DateHandler.DatePickerCallBack() {
                    @Override
                    public void dateSelected() {
                        showInfo();
                    }
                });
            }
        });
        listaReservas = new ArrayList<>();
        listaAgencias = new ArrayList<>();
        rvAdapter = new ReservaRVAdapter(getContext(), listaReservas, ReservaRVAdapter.Modo.POR_AGENCIA, new ReservaRVAdapter.MyCallBack() {
            @Override
            public void itemClicked(int position) {
                myCallBack.setUpFragmentReservar(listaReservas.get(position).getId());
            }

            @Override
            public String getFechaLiquidacion() {
                return null;
            }
        });
        rvReservas.setAdapter(rvAdapter);
        rvReservas.setLayoutManager(new LinearLayoutManager(getContext()));
        spinnerAgencias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                udUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        showInfo();
    }

    private void showInfo(){
        if(!tvFechaDesde.getText().toString().equals(tvFechaHasta.getText().toString())){
            if(!DateHandler.areDatesInOrder(tvFechaDesde.getText().toString(),tvFechaHasta.getText().toString())){
                listaAgencias.clear();
                listaReservas.clear();
                udTVInfo();
                setUpSpinner();
                Toast.makeText(getContext(),"Las fechas no son correctas",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String selected = "";
        if(listaReservas.size()>0){
            if(spinnerAgencias.getSelectedItem()!=null) {
                selected = spinnerAgencias.getSelectedItem().toString();
            }
        }
        udListaAgencias();
        setUpSpinner();
        if(selected.equals("") || !listaAgencias.contains(selected)){
            spinnerAgencias.setSelection(0);
        }else {
            if(listaAgencias.contains(selected)){
                spinnerAgencias.setSelection(listaAgencias.indexOf(selected));
            }
        }
        udUI();
    }

    private void udListaAgencias(){
        List<Reserva> reservasDelPeriodo = getReservasFromDB(TODAS);
        listaAgencias.clear();
        if(reservasDelPeriodo.size()>0) {
            Collections.sort(reservasDelPeriodo, new Comparator<Reserva>() {
                @Override
                public int compare(Reserva reserva1, Reserva reserva2) {
                    return reserva1.getAgencia().toLowerCase().compareTo(reserva2.getAgencia().toLowerCase());
                }
            });
            List<String> nuevasAgencias = new ArrayList<>();
            for (Reserva reserva : reservasDelPeriodo) {
                if (!nuevasAgencias.contains(reserva.getAgencia())) {
                    nuevasAgencias.add(reserva.getAgencia());
                }
            }
            if(nuevasAgencias.size()>1){listaAgencias.add(TODAS);}
            listaAgencias.addAll(nuevasAgencias);
        }
    }

    private void setUpSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,listaAgencias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgencias.setAdapter(adapter);
    }

    private void udUI(){
        udReservasRV();
        udTVInfo();
    }

    private void udReservasRV(){
        listaReservas.clear();
        if(listaAgencias.isEmpty()){
            rvAdapter.setReservaList(listaReservas);
            //udTVInfo();
            return;
        }
        String selected = spinnerAgencias.getSelectedItem().toString();
        listaReservas = getReservasFromDB(selected);
        if(listaReservas.size()>1){
            Collections.sort(listaReservas,Reserva.ordenarPorTE);
        }
        rvAdapter.setReservaList(listaReservas);
    }

    private List<Reserva> getReservasFromDB(String agencia){
        String query;
        String [] args;
        String desdeDBFormat = DateHandler.formatDateToStoreInDB(tvFechaDesde.getText().toString());
        String hastaDBFormat = DateHandler.formatDateToStoreInDB(tvFechaHasta.getText().toString());
        String cxcStr = String.valueOf(Reserva.ESTADO_CANCELADO);
        if(agencia.equals(TODAS)){
            query = "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL+">=? AND " +
                    ""+ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL+"<=? AND "+ReservaBDHandler.CAMPO_ESTADO+"!=? OR " +
                    ""+ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL+" IS NULL AND "+ReservaBDHandler.CAMPO_FECHA_EJECUCION+">=? AND " +
                    ""+ReservaBDHandler.CAMPO_FECHA_EJECUCION+"<=? AND "+ReservaBDHandler.CAMPO_ESTADO+"!=?";
            args = new String[]{desdeDBFormat,hastaDBFormat,cxcStr,desdeDBFormat,hastaDBFormat,cxcStr};
        }else {
            query = "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL+">=? AND " +
                    ""+ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL+"<=? AND "+ReservaBDHandler.CAMPO_AGENCIA+"=? AND " +
                    ""+ReservaBDHandler.CAMPO_ESTADO+"!=? OR "+ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL+" IS NULL AND " +
                    ""+ReservaBDHandler.CAMPO_FECHA_EJECUCION+">=? AND "+ReservaBDHandler.CAMPO_FECHA_EJECUCION+"<=? AND " +
                    ""+ReservaBDHandler.CAMPO_AGENCIA+"=? AND "+ReservaBDHandler.CAMPO_ESTADO+"!=?";
            args = new String[]{desdeDBFormat,hastaDBFormat,agencia,cxcStr,desdeDBFormat,hastaDBFormat,agencia,cxcStr};
        }
        return Reserva.getSoloActivas(ReservaBDHandler.getReservasFromDB(getContext(),query,args));
    }

    private void udTVInfo(){
        String texto = "";
        String selected = "";
        if(spinnerAgencias.getSelectedItem()!=null){
            selected = spinnerAgencias.getSelectedItem().toString();
        }

        if(listaReservas.size()==0){
            texto = "No hay reservas para mostrar";
        } else if(selected.equals(TODAS)){
            double importeTotal = 0;
            int paxTotal = 0;
            for (String agencia : listaAgencias) {
                if (!agencia.equals(TODAS)) {
                    double importeTotalAgencia = getImporteTotal(agencia);
                    int cantPaxAgencia = getCantPax(agencia);
                    texto += agencia + ": " + cantPaxAgencia + " pax " + importeTotalAgencia + " usd\n";
                    importeTotal = importeTotal + importeTotalAgencia;
                    paxTotal = paxTotal + cantPaxAgencia;
                }
            }
            texto += "TOTAL " + paxTotal + " pax " + importeTotal + " usd";
        } else {
            texto = getCantPax(selected) + " pax " + getImporteTotal(selected) + " usd";
        }
        /*double cancelaciones = getImpCancelaciones();
        double devoluciones = getImpDevoluciones();
        if(cancelaciones!=0 || devoluciones!=0){texto += "\n";}
        if(cancelaciones!=0){
            texto += "\nCancelaciones: " + cancelaciones + " usd";
        }
        if(devoluciones!=0){
            texto += "\nDevoluciones: " + devoluciones + " usd";
        }*/
        tvInfoVenta.setText(texto);
    }

    /*private double getImpCancelaciones(){
        double cancelaciones = 0;
        for (Reserva reserva:listaReservas){
            if(reserva.getEstado()==Reserva.ESTADO_CANCELADO){
                cancelaciones = cancelaciones + reserva.getPrecio();
            }
        }
        return cancelaciones;
    }

    private double getImpDevoluciones(){
        double devoluciones = 0;
        for (Reserva reserva:listaReservas){
            if(reserva.getEstado()==Reserva.ESTADO_DEVUELTO){
                devoluciones = devoluciones + reserva.getPrecio();
            }
        }
        return devoluciones;
    }*/

    private int getCantPax(String agencia){
        int totalPax = 0;
        for(Reserva reserva:listaReservas){
            if(reserva.getAgencia().equals(agencia) && reserva.getEstado()==Reserva.ESTADO_ACTIVO ||
                    reserva.getAgencia().equals(agencia) && reserva.getEstado()==Reserva.ESTADO_DEVUELTO && reserva.isDevParcial()){
                totalPax += reserva.getAdultos() + reserva.getMenores() + reserva.getInfantes();
            }
        }
        return totalPax;
    }

    private double getImporteTotal(String agencia){
        double importeTotal = 0;
        for(Reserva reserva:listaReservas){
            if(reserva.getAgencia().equals(agencia) && reserva.getEstado()==Reserva.ESTADO_ACTIVO){
                importeTotal += reserva.getPrecio();
            }else if(reserva.getAgencia().equals(agencia) && reserva.getEstado()==Reserva.ESTADO_DEVUELTO && reserva.isDevParcial()){
                importeTotal += reserva.getPrecio()-reserva.getImporteDevuelto();
            }
        }
        return importeTotal;
    }

    private void generarExcelReporteDeVenta(){
        if(listaReservas.size()<1){return;}
        String agencia = spinnerAgencias.getSelectedItem().toString();
        if(agencia.equals(TODAS)){
            Toast.makeText(getContext(),"Función no disponible para agencias: "+TODAS,Toast.LENGTH_SHORT).show();
            return;
        }
        String desde = tvFechaDesde.getText().toString();
        String hasta = tvFechaHasta.getText().toString();

        try {
            //File rutaSD = Environment.getExternalStorageDirectory();
            File rutaSD = new File(Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name));
            if(!rutaSD.exists()){rutaSD.mkdir();}
            rutaSD = new File(rutaSD.getAbsolutePath()+"/Venta por agencias-período");
            if(!rutaSD.exists()){rutaSD.mkdir();}
            rutaSD = new File(rutaSD.getAbsolutePath()+"/"+agencia);
            if(!rutaSD.exists()){rutaSD.mkdir();}
            rutaSD = new File(rutaSD.getAbsolutePath()+"/"+desde.substring(6));
            if(!rutaSD.exists()){rutaSD.mkdir();}

            //File rutaSD = Environment.getExternalFilesDir(null);
            String fileName = desde.replace("/","") + "-" + hasta.replace("/","") + " " + agencia + ".xls";
            File file = new File(rutaSD.getAbsolutePath(), fileName);
            if(MyExcel.generarExcelReporteVentaPorAgencia(file,listaReservas,agencia,desde,hasta,getImporteTotal(agencia))){
                Toast.makeText(getContext(),"Excel generado correctamente: "+file,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //System.out.println("Mensaje error: " + e.getMessage());
            Toast.makeText(getContext(), "Mensaje error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void checkForPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_EXTORAGE);
        } else {
            // Permission is already granted, call the function that does what you need
            generarExcelReporteDeVenta();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MainActivity.REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_EXTORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!....call the function that does what you need
                    generarExcelReporteDeVenta();
                } else {
                    Log.e(TAG, "Write permissions has to be granted to ATMS, otherwise it cannot operate properly.\n Exiting the program...\n");
                }
                break;
            }
            // other 'case' lines to check for other permissions this app might request.
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu!=null){menu.clear();}
        inflater.inflate(R.menu.menu_frag_venta_ttoo,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_excel_reporte_venta:
                checkForPermissions();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface MyCallBack{
        void udUI(String tag);
        void setUpFragmentReservar(long id);
    }
}
