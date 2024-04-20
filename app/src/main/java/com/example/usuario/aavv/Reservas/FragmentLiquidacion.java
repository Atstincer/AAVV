package com.example.usuario.aavv.Reservas;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;
import com.example.usuario.aavv.Util.Util;

import java.util.Collections;
import java.util.List;


/**
 * Created by usuario on 3/08/2023.
 */

public class FragmentLiquidacion extends Fragment implements ReservaRVAdapter.MyCallBack {

    public static final String TAG = "FragmentLiquidacion";

//    private final String[] mesesDelAno = new String[]{"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};

    private LinearLayout layoutInfo;
    private TextView tvFechaLiquidacion, tvInfo;
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
        layoutInfo = (LinearLayout)view.findViewById(R.id.layout_info);
        tvFechaLiquidacion = (TextView) view.findViewById(R.id.tv_fecha_confeccion_fliquidacion);
        tvInfo = (TextView) view.findViewById(R.id.tv_info_venta);
        rvReservas = (RecyclerView) view.findViewById(R.id.rv_reservas_fliquidacion);
        btnAddReserva = (FloatingActionButton)view.findViewById(R.id.btn_add_reserva);
    }

    private void setItUp(){
        myCallBack.udUI(FragmentLiquidacion.TAG);
        if(myCallBack.getLastFechaLiq()==null) {
            tvFechaLiquidacion.setText(DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
            myCallBack.setLastFechaLiq(tvFechaLiquidacion.getText().toString());
        }else {
            tvFechaLiquidacion.setText(myCallBack.getLastFechaLiq());
        }
        udReservaList();
        udTvInfo();
        adapter = new ReservaRVAdapter(getContext(),reservaList, ReservaRVAdapter.Modo.LIQUIDACION,this);
        rvReservas.setAdapter(adapter);
        rvReservas.setLayoutManager(new LinearLayoutManager(getContext()));

        tvFechaLiquidacion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DateHandler.showDatePicker(getContext(), tvFechaLiquidacion, new DateHandler.DatePickerCallBack() {
                    @Override
                    public void dateSelected() {
                        myCallBack.setLastFechaLiq(tvFechaLiquidacion.getText().toString());
                        udUI();
                    }
                });
            }
        });

        layoutInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Util.copyToClipBoard(getContext(),"Venta del "+ tvFechaLiquidacion.getText().toString()+"\n"+tvInfo.getText().toString());
                return true;
            }
        });

        btnAddReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReserva();
            }
        });
    }

    private void udUI(){
        udReservaList();
        udTvInfo();
        adapter.setReservaList(reservaList);
    }

    private void udTvInfo(){
        String text;
        if(!reservaList.isEmpty()) {
            double[] infoTotal = getTotales();
            text = "Totales: " + String.valueOf((int)(infoTotal[0])) + " pax  " + String.valueOf(infoTotal[1]) + " usd";
            if(MySharedPreferences.getIncluirPrecioCUP(getContext())&&MySharedPreferences.getTasaCUP(getContext())>0){
                text += " ("+infoTotal[1]*MySharedPreferences.getTasaCUP(getContext())+" cup)";
            }
            if(infoTotal[2]>0){
                text += "\nDevoluciones: "+String.valueOf(infoTotal[2])+" usd";
                if(MySharedPreferences.getIncluirPrecioCUP(getContext())&&MySharedPreferences.getTasaCUP(getContext())>0){
                    text += " ("+infoTotal[2]*MySharedPreferences.getTasaCUP(getContext())+" cup)";
                }
                text += "\nTotal sin dev: "+String.valueOf(infoTotal[1]-infoTotal[2])+" usd";
                if(MySharedPreferences.getIncluirPrecioCUP(getContext())&&MySharedPreferences.getTasaCUP(getContext())>0){
                    text += " ("+(infoTotal[1]-infoTotal[2])*MySharedPreferences.getTasaCUP(getContext())+" cup)";
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
            if(reserva.getEstado()==Reserva.ESTADO_ACTIVO ||
                    reserva.getEstado()==Reserva.ESTADO_CANCELADO && reserva.getFechaCancelacion()!=null &&
                            !reserva.getFechaCancelacion().equals(tvFechaLiquidacion.getText().toString())) {
                total += reserva.getPrecio();
                cantPax += + reserva.getAdultos() + reserva.getMenores() + reserva.getInfantes() + reserva.getAcompanantes();
            }else if(reserva.getEstado()==Reserva.ESTADO_DEVUELTO){
                if(reserva.getFechaConfeccion().equals(reserva.getFechaDevolucion())) {
                    cantPax += + reserva.getAdultos() + reserva.getMenores() + reserva.getInfantes() + reserva.getAcompanantes();
                    total += reserva.getPrecio();
                    devoluciones += reserva.getImporteDevuelto();
                } else if(reserva.getFechaConfeccion().equals(tvFechaLiquidacion.getText().toString())){
                    total += reserva.getPrecio();
                    cantPax = cantPax + reserva.getAdultos() + reserva.getMenores() + reserva.getInfantes() + reserva.getAcompanantes();
                } else if(reserva.getFechaDevolucion().equals(tvFechaLiquidacion.getText().toString())) {
                    devoluciones += reserva.getImporteDevuelto();
                }
            }
        }
        //return String.valueOf(cantPax) + " pax  " + String.valueOf(total) + " usd";
        return new double[]{cantPax,total, devoluciones};
    }

    private void udReservaList(){
        String fechaLiqBD = DateHandler.formatDateToStoreInDB(tvFechaLiquidacion.getText().toString());
        if(MySharedPreferences.getIncluirDevEnLiquidacion(getContext())){
            reservaList = ReservaBDHandler.getReservasFromDB(getContext(),
                    "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_FECHA_CONFECCION+"=? OR " +
                    ReservaBDHandler.CAMPO_FECHA_DEVOLUCION+"=?",
                    new String[]{fechaLiqBD,fechaLiqBD});
        }else {
            reservaList = ReservaBDHandler.getReservasFromDB(getContext(),
                    "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_FECHA_CONFECCION+"=?",
                    new String[]{fechaLiqBD});
        }
        Collections.sort(reservaList,Reserva.ordenarPorTE);
    }


    private void copiarInfo(){
        String texto = "Venta del día: "+ tvFechaLiquidacion.getText().toString();
        texto += "\n\n" + tvInfo.getText().toString();
        for (Reserva reserva:reservaList){
            if(reserva.getEstado() == Reserva.ESTADO_ACTIVO ||
                    reserva.getEstado() == Reserva.ESTADO_DEVUELTO &&
                            !tvFechaLiquidacion.getText().toString().equals(reserva.getFechaDevolucion())) {
                texto += "\n\n" + Reserva.toString(getContext(),reserva, Reserva.INFO_LIQUIDACION);
            }else if(reserva.getEstado() == Reserva.ESTADO_CANCELADO){
                texto += "\n\nTE: " + reserva.getNoTE() + "    CANCELADO";
            } else if(reserva.getEstado() == Reserva.ESTADO_DEVUELTO && tvFechaLiquidacion.getText().toString().equals(reserva.getFechaDevolucion())){
                if(reserva.getPrecio()==reserva.getImporteDevuelto()) {
                    texto += "\n\nTE: " + reserva.getNoTE() + "    DEVOLUCION TOTAL";
                }else if(reserva.getPrecio() > reserva.getImporteDevuelto()) {
                    texto += "\n\nTE: " + reserva.getNoTE() + "    DEVOLUCION PARCIAL";
                }
                texto += "\nImporte devuelto: -" + reserva.getImporteDevuelto();
            }
        }
        Util.copyToClipBoard(getContext(),texto);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu!=null){menu.clear();}
        inflater.inflate(R.menu.menu_frag_liquidacion,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_copiar:
                copiarInfo();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(int position) {
//        myCallBack.setLastFechaLiq(tvFechaLiquidacion.getText().toString());
        myCallBack.setUpFragmentReservar(reservaList.get(position).getId());
    }

    private void addReserva(){
        if(reservaList.isEmpty()){
            myCallBack.setUpFragmentReservar("",tvFechaLiquidacion.getText().toString());
            return;
        }
        Reserva lastReserva = reservaList.get(reservaList.size()-1);
        if(lastReserva.getEstado()==Reserva.ESTADO_DEVUELTO && lastReserva.getFechaDevolucion().equals(tvFechaLiquidacion.getText().toString())
                && !lastReserva.getFechaConfeccion().equals(tvFechaLiquidacion.getText().toString())){
            myCallBack.setUpFragmentReservar("",tvFechaLiquidacion.getText().toString());
        }else {
            myCallBack.setUpFragmentReservar(lastReserva.getNoTE(),tvFechaLiquidacion.getText().toString());
        }
    }

    public interface MyCallBack{
        void udUI(String tag);
        void setUpFragmentReservar(long id);
        void setUpFragmentReservar(String lastTE, String fechaLiquidacion);
        void setLastFechaLiq(String lastFechaLiq);
        String getLastFechaLiq();
    }
}
