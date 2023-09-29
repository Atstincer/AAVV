package com.example.usuario.aavv.Reservas;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;
import com.example.usuario.aavv.Util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by usuario on 30/07/2023.
 */

public class FragmentReservasSaliendoElDia extends Fragment implements ReservaRVAdapter.MyCallBack {

    public static final String TAG = "FragmentReservasSaliendoElDia";

    private LinearLayout layoutInfo;
    private RecyclerView rvReservas;
    private TextView tvFechaEjecucion, tvInfo;

    private List<Reserva> reservasList;
    private ReservaRVAdapter adapter;

    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_reservas_saliendo_dia,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindComponents(view);
        myCallBack.udUI(FragmentReservasSaliendoElDia.TAG);
        setItUp();
    }

    @Override
    public void onAttach(Context context) {
        myCallBack = (MyCallBack)context;
        super.onAttach(context);
    }

    private void bindComponents(View v){
        layoutInfo = (LinearLayout)v.findViewById(R.id.layout_info);
        rvReservas = (RecyclerView)v.findViewById(R.id.rv_reservas);
        tvFechaEjecucion = (TextView)v.findViewById(R.id.tv_fecha_ejecucion);
        tvInfo = (TextView)v.findViewById(R.id.tv_info);
    }

    private void setItUp(){
        if(tvFechaEjecucion.getText().toString().equals("fecha")) {
            tvFechaEjecucion.setText(DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
        }
        tvFechaEjecucion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DateHandler.showDatePicker(getContext(), tvFechaEjecucion, new DateHandler.DatePickerCallBack() {
                    @Override
                    public void dateSelected() {
                        udFragment();
                    }
                });
            }
        });

        layoutInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String texto = "Reservas saliendo el día " + tvFechaEjecucion.getText().toString() + "\n\n" + tvInfo.getText().toString();
                Util.copyToClipBoard(getContext(),texto);
                return false;
            }
        });

        //reservasList = ReservaBDHandler.getAllReservasFromDB(getContext());
        udReservasList();
        udTvInfo();
        adapter = new ReservaRVAdapter(getContext(),reservasList, ReservaRVAdapter.Modo.EXC_SALIENDO_EL_DIA,this);
        rvReservas.setAdapter(adapter);
        rvReservas.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void udFragment(){
        udReservasList();
        udTvInfo();
        adapter.setReservaList(reservasList);
    }

    private void udReservasList(){
        reservasList = ReservaBDHandler.getReservasFromDB(getContext(),
                "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_FECHA_EJECUCION+"=? AND "+ReservaBDHandler.CAMPO_ESTADO+"=?",
                new String[]{DateHandler.formatDateToStoreInDB(tvFechaEjecucion.getText().toString()),String.valueOf(Reserva.ESTADO_ACTIVO)});
        if(reservasList.isEmpty()){
            return;
        }
        Collections.sort(reservasList,Reserva.ordenarPorTE);
        //Collections.sort(reservasList,Reserva.ordenarPorExc);
        Map<String,List<Reserva>> mapReservas = new HashMap<String, List<Reserva>>();
        for(Reserva reserva:reservasList){
            if(mapReservas.containsKey(reserva.getExcursion())){
                mapReservas.get(reserva.getExcursion()).add(reserva);
            }else {
                mapReservas.put(reserva.getExcursion(),new ArrayList<Reserva>());
                mapReservas.get(reserva.getExcursion()).add(reserva);
            }
        }
        reservasList.clear();
        for(String excursion:mapReservas.keySet()){
            Collections.sort(mapReservas.get(excursion),Reserva.ordenarPorHotel);
            reservasList.addAll(mapReservas.get(excursion));
        }
    }

    private void udTvInfo(){
        if(reservasList.isEmpty()){
            tvInfo.setText("No hay resultados para mostrar");
            return;
        }
        String texto = "";
        List<String> excursiones = new ArrayList<>();

        //llenando lista excursiones
        for(Reserva reserva:reservasList){
            if(!excursiones.contains(reserva.getExcursion())){
                excursiones.add(reserva.getExcursion());
            }
        }

        boolean primeraExc = true;

        //creando mensaje
        for(String excursion:excursiones){
            int totalPaxExc = 0;
            String infoExc = "";
            for(Reserva reserva:reservasList){
                if(reserva.getExcursion().equals(excursion)){
                    int cantPax = reserva.getAdultos()+reserva.getMenores()+reserva.getInfantes();
                    totalPaxExc += cantPax;
                    infoExc += "\n" + reserva.getNoTE() + " " + reserva.getHotel() + " " + reserva.getNoHab();
                    if(cantPax!=0){
                        infoExc += " " + cantPax + " pax";
                    }
                    if(reserva.getAcompanantes()!=0){
                        infoExc += " " + reserva.getAcompanantes() + " acompañante";
                    }
                }
            }
            if(primeraExc){
                texto += excursion + " (" + totalPaxExc + ")" + infoExc;
                primeraExc = false;
            }else {
                texto += "\n\n" + excursion + " (" + totalPaxExc + ")" + infoExc;
            }
        }
        tvInfo.setText(texto);
    }

    private void enviarMail(){
        // TODO: 14/09/2023 funcion enviar x mail excursiones saliendo el dia...
    }


    @Override
    public void itemClicked(int position) {
        myCallBack.setUpFragmentReservar(reservasList.get(position).getId());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu != null){menu.clear();}
        inflater.inflate(R.menu.menu_frag_reservas_saliendo_el_dia,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_enviar_mail_reservas_saliendo_el_dia:
                enviarMail();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface MyCallBack{
        void udUI(String tag);
        void setUpFragmentReservar(long id);
    }
}
