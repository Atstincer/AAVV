package com.example.usuario.aavv.Reservas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.aavv.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;
import com.example.usuario.aavv.Util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Created by usuario on 3/08/2023.
 */

public class FragmentLiquidacion extends Fragment implements ReservaRVAdapter.MyCallBack {

    public static final String TAG = "FragmentLiquidacion";

    private LinearLayout layoutInfo;
    private TextView tvDesde, tvHasta, tvInfo;
    private RecyclerView rvReservas;
    private ReservaRVAdapter adapter;
    private FloatingActionButton btnAddReserva;

    private List<Reserva> reservaList;

    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_liquidacion,container,false);
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
        tvDesde = view.findViewById(R.id.tv_desde_fliquidacion);
        tvHasta = view.findViewById(R.id.tv_hasta_fliquidacion);
        tvInfo = view.findViewById(R.id.tv_info_venta);
        rvReservas = view.findViewById(R.id.rv_reservas_fliquidacion);
        btnAddReserva = view.findViewById(R.id.btn_add_reserva);
    }

    private void setItUp(){
        myCallBack.udUI(FragmentLiquidacion.TAG);
        if(myCallBack.getDesdeLiq()==null) {
            tvDesde.setText(DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
            myCallBack.setDesdeLiq(tvDesde.getText().toString());
        }else {
            tvDesde.setText(myCallBack.getDesdeLiq());
        }
        if(myCallBack.getHastaLiq()==null) {
            tvHasta.setText(DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
            myCallBack.setHastaLiq(tvHasta.getText().toString());
        }else {
            tvHasta.setText(myCallBack.getHastaLiq());
        }
        udReservaList();
        udTvInfo();
        adapter = new ReservaRVAdapter(getContext(),Reserva.toObjectList(reservaList), ReservaRVAdapter.Modo.LIQUIDACION,this);
        rvReservas.setAdapter(adapter);
        rvReservas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvReservas.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    btnAddReserva.hide();
                }else if(dy<0){
                    btnAddReserva.show();
                }
            }
        });

        tvDesde.setOnClickListener(view -> DateHandler.showDatePicker(getContext(), tvDesde, () -> {
            myCallBack.setDesdeLiq(tvDesde.getText().toString());
            udUI();
        }));

        tvHasta.setOnClickListener(view -> DateHandler.showDatePicker(getContext(), tvHasta, () -> {
            myCallBack.setHastaLiq(tvHasta.getText().toString());
            udUI();
        }));

        layoutInfo.setOnLongClickListener(view -> {
            String info;
            String desde = tvDesde.getText().toString();
            String hasta = tvHasta.getText().toString();
            if(desde.equals(hasta)){
                info = "Venta del "+ desde +"\n"+tvInfo.getText().toString();
            } else {
                info = "Venta\ndesde: "+ desde + "\thasta: " + hasta +"\n\n"+tvInfo.getText().toString();
            }

            Util.copyToClipBoard(getContext(),info);
            return true;
        });

        btnAddReserva.setOnClickListener(view -> addReserva());
    }

    private boolean areDatesValid(){
        String desde = tvDesde.getText().toString();
        String hasta = tvHasta.getText().toString();
        return desde.equals(hasta) || DateHandler.areDatesInOrder(desde, hasta);
    }

    private void udUI(){
        if(areDatesValid()){
            udReservaList();
            udTvInfo();
        }else {
            reservaList = new ArrayList<>();
            tvInfo.setText("Las fechas no son correctas");
        }
        adapter.setReservaList(Reserva.toObjectList(reservaList));
    }

    private void udTvInfo(){
        String text;
        if(!reservaList.isEmpty()) {
            double[] infoTotal = getTotales();
            text = "Totales: " + (int)(infoTotal[0]) + " pax  " + infoTotal[1] + " usd";
            if(MySharedPreferences.getIncluirPrecioCUP(getContext())&&MySharedPreferences.getTasaCUP(getContext())>0){
                text += " ("+infoTotal[1]*MySharedPreferences.getTasaCUP(getContext())+" cup)";
            }
            if(infoTotal[2]>0){
                text += "\nDevoluciones: " + infoTotal[2] + " usd";
                if(MySharedPreferences.getIncluirPrecioCUP(getContext())&&MySharedPreferences.getTasaCUP(getContext())>0){
                    text += " ("+infoTotal[2]*MySharedPreferences.getTasaCUP(getContext())+" cup)";
                }
                text += "\nTotal sin dev: " + (infoTotal[1]-infoTotal[2]) +" usd";
                if(MySharedPreferences.getIncluirPrecioCUP(getContext()) && MySharedPreferences.getTasaCUP(getContext())>0){
                    text += " (" + (infoTotal[1] - infoTotal[2]) * MySharedPreferences.getTasaCUP(getContext()) + " cup)";
                }
            }
        } else {
            text = "No hay información para mostrar";
        }
        tvInfo.setText(text);
    }

    private double[] getTotales(){
        double total = 0;
        double cantPax = 0;
        double devoluciones = 0;
        for(Reserva reserva:reservaList){
            if(reserva.getEstado()==Reserva.ESTADO_ACTIVO) {
                total += reserva.getPrecio();
                cantPax += reserva.getAdultos() + reserva.getMenores() + reserva.getInfantes() + reserva.getAcompanantes();
            }else if(reserva.getEstado()==Reserva.ESTADO_DEVUELTO){
                if(reserva.getCriterioSeleccion() == Reserva.Criterio_Seleccion.FECHA_CONFECCION){
                    total += reserva.getPrecio();
                    cantPax += reserva.getAdultos() + reserva.getMenores() + reserva.getInfantes() + reserva.getAcompanantes();
                } else if(reserva.getCriterioSeleccion() == Reserva.Criterio_Seleccion.FECHA_DEVOLUCION){
                    devoluciones += reserva.getImporteDevuelto();
                }
            }
        }
        return new double[]{cantPax,total, devoluciones};
    }

    private void udReservaList(){
        reservaList = new ArrayList<>();
        List<Reserva> devoluciones = new ArrayList<>();
        String desdeDB = DateHandler.formatDateToStoreInDB(tvDesde.getText().toString());
        String hastaDB = DateHandler.formatDateToStoreInDB(tvHasta.getText().toString());
        reservaList.addAll(ReservaBDHandler.getReservasFromDB(getContext(),
                "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_FECHA_CONFECCION+">=? AND "
                        + ReservaBDHandler.CAMPO_FECHA_CONFECCION+"<=?",
                new String[]{desdeDB, hastaDB}));
        if(MySharedPreferences.getIncluirDevEnLiquidacion(getContext())){
            devoluciones.addAll(ReservaBDHandler.getReservasFromDB(getContext(),
                    "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_FECHA_DEVOLUCION+">=? " +
                            "AND "+ReservaBDHandler.CAMPO_FECHA_DEVOLUCION+"<=? AND "+ReservaBDHandler.CAMPO_ESTADO+"=?",
                    new String[]{desdeDB,hastaDB,String.valueOf(Reserva.ESTADO_DEVUELTO)}));
        }
        if(!reservaList.isEmpty()){
            for(Reserva reserva: reservaList){
                reserva.setCriterioSeleccion(Reserva.Criterio_Seleccion.FECHA_CONFECCION);
            }
            Collections.sort(reservaList,Reserva.ordenarPorTE);
        }
        if(!devoluciones.isEmpty()){
            for(Reserva reserva: devoluciones){
                reserva.setCriterioSeleccion(Reserva.Criterio_Seleccion.FECHA_DEVOLUCION);
            }
            Collections.sort(devoluciones,Reserva.ordenarPorTE);
            reservaList.addAll(devoluciones);
        }
    }


    private void copiarInfo(){
        String desde = tvDesde.getText().toString();
        String hasta = tvHasta.getText().toString();
        StringBuilder sb = new StringBuilder();
        if(desde.equals(hasta)){
            sb.append("Venta del día: ");
            sb.append(desde);
        } else {
            sb.append("Venta\ndesde: ");
            sb.append(desde);
            sb.append("\thasta: ");
            sb.append(hasta);
        }
        sb.append("\n\n");
        sb.append(tvInfo.getText().toString());
        for (Reserva reserva:reservaList){
            if(reserva.getEstado() == Reserva.ESTADO_ACTIVO) {
                sb.append("\n\n");
                sb.append(Reserva.toString(getContext(),reserva, Reserva.INFO_LIQUIDACION));
            }else if(reserva.getEstado() == Reserva.ESTADO_CANCELADO){
                sb.append("\n\nTE: ");
                sb.append(reserva.getNoTE());
                sb.append("    CANCELADO");
            } else if(reserva.getEstado() == Reserva.ESTADO_DEVUELTO){
                if(reserva.getCriterioSeleccion() == Reserva.Criterio_Seleccion.FECHA_CONFECCION){
                    sb.append("\n\n");
                    sb.append(Reserva.toString(getContext(),reserva, Reserva.INFO_LIQUIDACION));
                } else if (reserva.getCriterioSeleccion() == Reserva.Criterio_Seleccion.FECHA_DEVOLUCION){
                    if(reserva.getPrecio() == reserva.getImporteDevuelto()) {
                        sb.append("\n\nTE: ");
                        sb.append(reserva.getNoTE());
                        sb.append("    DEVOLUCION TOTAL");
                    }else if(reserva.getPrecio() > reserva.getImporteDevuelto()) {
                        sb.append("\n\nTE: ");
                        sb.append(reserva.getNoTE());
                        sb.append("    DEVOLUCION PARCIAL");
                        sb.append("\nImporte devuelto: -");
                        sb.append(reserva.getImporteDevuelto());
                    }
                }
            }
        }
        Util.copyToClipBoard(getContext(),sb.toString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu!=null){menu.clear();}
        inflater.inflate(R.menu.menu_frag_liquidacion,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_copiar) {
            copiarInfo();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(int position) {
        myCallBack.setUpFragmentReservar(reservaList.get(position).getNoTE());
    }

    private void addReserva(){
        if(reservaList.isEmpty()){
            String lastTE = getLastTEFromDB();
            myCallBack.setUpFragmentReservar(lastTE,tvDesde.getText().toString());
            return;
        }
        Reserva lastReserva = getLastReservaActiva();
        if(lastReserva.getFechaConfeccion() == null || lastReserva.getFechaConfeccion().isEmpty()){
            myCallBack.setUpFragmentReservar("",tvDesde.getText().toString());
        } else {
            myCallBack.setUpFragmentReservar(lastReserva.getNoTE(),lastReserva.getFechaConfeccion());
        }
    }

    @SuppressLint("Range")
    private String getLastTEFromDB(){
        String lastTE = "";
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(
                getContext(),
                AdminSQLiteOpenHelper.BD_NAME,
                null,
                AdminSQLiteOpenHelper.BD_VERSION
        );
        SQLiteDatabase db = admin.getReadableDatabase();
        String query = "SELECT " + ReservaBDHandler.CAMPO_NUMERO_TE + " FROM " +
                ReservaBDHandler.TABLE_NAME + " ORDER BY " +
                ReservaBDHandler.CAMPO_FECHA_CONFECCION + " DESC, " +
                ReservaBDHandler.CAMPO_NUMERO_TE + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            lastTE = cursor.getString(0);
        }
        cursor.close();
        return lastTE;
    }

    private Reserva getLastReservaActiva(){
        for(int i = reservaList.size() - 1; i >= 0; i--){
            if(reservaList.get(i).getCriterioSeleccion() == Reserva.Criterio_Seleccion.FECHA_CONFECCION){
                return reservaList.get(i);
            }
        }
        return new Reserva();
    }

    public interface MyCallBack{
        void udUI(String tag);
        void setUpFragmentReservar(String id);
        void setUpFragmentReservar(String lastTE, String fechaLiquidacion);
        void setDesdeLiq(String fechaDesdeLiq);
        void setHastaLiq(String fechaHastaLiq);
        String getDesdeLiq();
        String getHastaLiq();
    }
}
