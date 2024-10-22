package com.example.usuario.aavv.Reservas;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.StorageAccess.VentaTTOOStorageAccess;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;
import com.example.usuario.aavv.Util.RepVAgenciaExcel;
import com.example.usuario.aavv.Util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by usuario on 13/08/2023.
 */

public class FragmentVentaTTOO extends Fragment {

    public static final String TAG = "FragmentVentaTTOO";
    private final String TODAS = "Todas";

    private boolean desdeIsRegular;

    private LinearLayout layoutInfo;
    private AppCompatSpinner spinnerAgencias;
    private TextView tvFechaDesde, tvFechaHasta, tvInfoVenta, tvFiltrandoPor;
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
        layoutInfo = view.findViewById(R.id.layout_info);
        spinnerAgencias = view.findViewById(R.id.sp_agencias);
        tvFechaDesde = view.findViewById(R.id.tv_fecha_desde);
        tvFechaHasta = view.findViewById(R.id.tv_fecha_hasta);
        tvInfoVenta = view.findViewById(R.id.tv_info_venta);
        rvReservas = view.findViewById(R.id.rv_reservas);
        tvFiltrandoPor = view.findViewById(R.id.tv_filtrando_por);
    }

    private void setItUp(){
        myCallBack.udUI(TAG);
        figureOutDesde();
        figureOutHasta();

        Log.d("fechas","Cierre regular: " + MySharedPreferences.isCierreRegular(getContext()));
        Log.d("fechas","Dia cierre: " + MySharedPreferences.getDiaCierre(getContext()));

        layoutInfo.setOnLongClickListener(view -> {
            String texto = "Agencia: "+spinnerAgencias.getSelectedItem().toString()+"\n" +
                    "Desde: "+tvFechaDesde.getText().toString()+"\n" +
                    "Hasta: "+tvFechaHasta.getText().toString()+"\n\n" + tvInfoVenta.getText().toString();
            Util.copyToClipBoard(getContext(),texto);
            return true;
        });
        tvFechaDesde.setOnClickListener(view -> DateHandler.showDatePicker(getContext(), tvFechaDesde, () -> {
            showInfo();
            myCallBack.setLastDesde(tvFechaDesde.getText().toString());
        }));
        tvFechaHasta.setOnClickListener(view -> DateHandler.showDatePicker(getContext(), tvFechaHasta, () -> {
            showInfo();
            myCallBack.setLastHasta(tvFechaHasta.getText().toString());
        }));
        listaReservas = new ArrayList<>();
        listaAgencias = new ArrayList<>();
        rvAdapter = new ReservaRVAdapter(getContext(), Reserva.toObjectList(listaReservas), ReservaRVAdapter.Modo.POR_AGENCIA,
                position -> myCallBack.setUpFragmentReservar(listaReservas.get(position).getNoTE()));
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
        int tipoFecha = MySharedPreferences.getTipoFechaFiltrar(getContext());
        if(tipoFecha == MisConstantes.Filtrar.FECHA_EXCURSION.ordinal()){
            tvFiltrandoPor.setText("Según fecha excursión");
        }else if(tipoFecha == MisConstantes.Filtrar.FECHA_CONFECCION.ordinal()){
            tvFiltrandoPor.setText("Según fecha venta");
        }
        showInfo();
    }

    private void figureOutDesde(){
        String desde;
        if(myCallBack.getLastDesde() != null && !myCallBack.getLastDesde().isEmpty()){
            desde = myCallBack.getLastDesde();
        } else if(MySharedPreferences.isCierreRegular(getContext())){
            desde = "01" + DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR).substring(2, 10);
            desdeIsRegular = true;
        } else {
            int diaCierre = MySharedPreferences.getDiaCierre(getContext());
            /*if(diaCierre > 0 && diaCierre < 31){
                desde = DateHandler.getDesdeLastMonth(diaCierre);
                desdeIsRegular = false;
            } else {
                desde = "01" + DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR).substring(2, 10);
                desdeIsRegular = true;
            }*/
            desde = DateHandler.getDesdeSegunPeriodo(diaCierre);
            desdeIsRegular = false;
        }
        tvFechaDesde.setText(desde);
        myCallBack.setLastDesde(desde);
    }

    private void figureOutHasta(){
        String hasta;
        if(myCallBack.getLastHasta() != null && !myCallBack.getLastHasta().isEmpty()){
            hasta = myCallBack.getLastHasta();
        } else if(desdeIsRegular || MySharedPreferences.isCierreRegular(getContext())){
            hasta = DateHandler.getLastDayOfMonth(MisConstantes.FormatoFecha.MOSTRAR);
        } else {
            int diaCierre = MySharedPreferences.getDiaCierre(getContext());
            if(diaCierre > 0 && diaCierre < 31){
                hasta = DateHandler.getHastaSegunPeriodo(diaCierre);
            } else {
                hasta = DateHandler.getLastDayOfMonth(MisConstantes.FormatoFecha.MOSTRAR);
            }
        }
        tvFechaHasta.setText(hasta);
        myCallBack.setLastHasta(hasta);
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
        if(!listaReservas.isEmpty()){
            if(spinnerAgencias.getSelectedItem()!=null) {
                selected = spinnerAgencias.getSelectedItem().toString();
            }
        }
        udListaAgencias();
        setUpSpinner();
        if(selected.isEmpty() || !listaAgencias.contains(selected)){
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
        listaAgencias = new ArrayList<>();
        if(!reservasDelPeriodo.isEmpty()) {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Collections.sort(reservasDelPeriodo,
                        Comparator.comparing(reserva -> reserva.getAgencia().toLowerCase()));
            }*/
            List<String> nuevasAgencias = new ArrayList<>();
            for (Reserva reserva : reservasDelPeriodo) {
                if (!nuevasAgencias.contains(reserva.getAgencia())) {
                    nuevasAgencias.add(reserva.getAgencia());
                }
            }
            if(!nuevasAgencias.isEmpty()){
                Collections.sort(nuevasAgencias);
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
            rvAdapter.setReservaList(Reserva.toObjectList(listaReservas));
            //udTVInfo();
            return;
        }
        String selected = spinnerAgencias.getSelectedItem().toString();
        listaReservas = getReservasFromDB(selected);
        if(listaReservas.size()>1){
            Collections.sort(listaReservas,Reserva.ordenarPorTE);
        }
        rvAdapter.setReservaList(Reserva.toObjectList(listaReservas));
    }

    private List<Reserva> getReservasFromDB(String agencia){
        String query = "";
        String [] args = new String[]{};
        String desdeDBFormat = DateHandler.formatDateToStoreInDB(tvFechaDesde.getText().toString());
        String hastaDBFormat = DateHandler.formatDateToStoreInDB(tvFechaHasta.getText().toString());
        String cxcStr = String.valueOf(Reserva.ESTADO_CANCELADO);
        if(agencia.equals(TODAS)){
            if(MySharedPreferences.getTipoFechaFiltrar(getContext()) == MisConstantes.Filtrar.FECHA_EXCURSION.ordinal()) {
                query = "SELECT * FROM " + ReservaBDHandler.TABLE_NAME + " WHERE " + ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL + ">=? AND " +
                        ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL + "<=? AND " + ReservaBDHandler.CAMPO_ESTADO + "!=? OR " +
                        ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL + " IS NULL AND " + ReservaBDHandler.CAMPO_FECHA_EJECUCION + ">=? AND " +
                        ReservaBDHandler.CAMPO_FECHA_EJECUCION + "<=? AND " + ReservaBDHandler.CAMPO_ESTADO + "!=?";
                args = new String[]{desdeDBFormat, hastaDBFormat, cxcStr, desdeDBFormat, hastaDBFormat, cxcStr};
            }else if(MySharedPreferences.getTipoFechaFiltrar(getContext()) == MisConstantes.Filtrar.FECHA_CONFECCION.ordinal()){
                query = "SELECT * FROM " + ReservaBDHandler.TABLE_NAME + " WHERE " + ReservaBDHandler.CAMPO_FECHA_CONFECCION + ">=? AND " +
                        ReservaBDHandler.CAMPO_FECHA_CONFECCION + "<=? AND " + ReservaBDHandler.CAMPO_ESTADO + "!=?";
                args = new String[]{desdeDBFormat, hastaDBFormat, cxcStr};
            }
        }else {
            if(MySharedPreferences.getTipoFechaFiltrar(getContext()) == MisConstantes.Filtrar.FECHA_EXCURSION.ordinal()) {
                query = "SELECT * FROM " + ReservaBDHandler.TABLE_NAME + " WHERE " + ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL + ">=? AND " +
                        ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL + "<=? AND " + ReservaBDHandler.CAMPO_AGENCIA + "=? AND " +
                        ReservaBDHandler.CAMPO_ESTADO + "!=? OR " + ReservaBDHandler.CAMPO_FECHA_EJECUCION_ORIGINAL + " IS NULL AND " +
                        ReservaBDHandler.CAMPO_FECHA_EJECUCION + ">=? AND " + ReservaBDHandler.CAMPO_FECHA_EJECUCION + "<=? AND " +
                        ReservaBDHandler.CAMPO_AGENCIA + "=? AND " + ReservaBDHandler.CAMPO_ESTADO + "!=?";
                args = new String[]{desdeDBFormat, hastaDBFormat, agencia, cxcStr, desdeDBFormat, hastaDBFormat, agencia, cxcStr};
            }else if(MySharedPreferences.getTipoFechaFiltrar(getContext()) == MisConstantes.Filtrar.FECHA_CONFECCION.ordinal()){
                query = "SELECT * FROM " + ReservaBDHandler.TABLE_NAME + " WHERE " + ReservaBDHandler.CAMPO_FECHA_CONFECCION + ">=? AND " +
                        ReservaBDHandler.CAMPO_FECHA_CONFECCION + "<=? AND " + ReservaBDHandler.CAMPO_AGENCIA + "=? AND " +
                        ReservaBDHandler.CAMPO_ESTADO + "!=?";
                args = new String[]{desdeDBFormat, hastaDBFormat, agencia, cxcStr};
            }
        }
        return Reserva.getSoloActivas(ReservaBDHandler.getReservasFromDB(getContext(),query,args));
    }

    private void udTVInfo(){
        StringBuilder texto = new StringBuilder();
        String selected = "";
        if(spinnerAgencias.getSelectedItem()!=null){
            selected = spinnerAgencias.getSelectedItem().toString();
        }

        if(listaReservas.isEmpty()){
            texto = new StringBuilder("No hay reservas para mostrar");
        } else if(selected.equals(TODAS)){
            double importeTotal = 0;
            int paxTotal = 0;
            for (String agencia : listaAgencias) {
                if (!agencia.equals(TODAS)) {
                    double importeTotalAgencia = getImporteTotal(agencia);
                    int cantPaxAgencia = getCantPax(agencia);
                    texto.append(agencia).append(": ").append(cantPaxAgencia).append(" pax ").append(importeTotalAgencia).append(" usd\n");
                    importeTotal = importeTotal + importeTotalAgencia;
                    paxTotal = paxTotal + cantPaxAgencia;
                }
            }
            texto.append("TOTAL ").append(paxTotal).append(" pax ").append(importeTotal).append(" usd");
        } else {
            texto = new StringBuilder(getCantPax(selected) + " pax " + getImporteTotal(selected) + " usd");
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
        tvInfoVenta.setText(texto.toString());
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
        if(listaReservas.isEmpty()){return;}
        String agencia = spinnerAgencias.getSelectedItem().toString();
        if(agencia.equals(TODAS)){
            Toast.makeText(getContext(),"Función no disponible para agencias: "+TODAS,Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Util.hasPermissionGranted(getContext())){
            myCallBack.requestCreateSelectAppDir(true);
            return;
        }
        String desde = tvFechaDesde.getText().toString();
        String hasta = tvFechaHasta.getText().toString();

        new Thread(()->{
            try {
                VentaTTOOStorageAccess ventaTTOOStorageAccess = new VentaTTOOStorageAccess(getContext(),desde,hasta,agencia);
                RepVAgenciaExcel repVAgenciaExcel = new RepVAgenciaExcel(listaReservas, ventaTTOOStorageAccess);
                if(repVAgenciaExcel.generarExcel()){
                    myCallBack.showSnackBar("Excel generado correctamente: "+ventaTTOOStorageAccess.getFileName());
                }else {
                    getActivity().runOnUiThread(()->{
                        myCallBack.requestCreateSelectAppDir(true);
                    });
                }
            } catch (Exception e) {
                myCallBack.showSnackBar("Error creando excel.");
                Log.e("excel","Error generando excel",e);
            }
        }).start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu!=null){menu.clear();}
        inflater.inflate(R.menu.menu_frag_venta_ttoo,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_excel_reporte_venta) {
            generarExcelReporteDeVenta();
        }
        return super.onOptionsItemSelected(item);
    }

    public interface MyCallBack{
        void udUI(String tag);
        void setUpFragmentReservar(String id);
        void showSnackBar(String mensaje);
        void setLastDesde(String lastDesde);
        void setLastHasta(String lastHasta);
        String getLastDesde();
        String getLastHasta();
        void requestCreateSelectAppDir(boolean conAlertDialog);
    }
}
