package com.example.usuario.aavv.Reservas;

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
import android.widget.TextView;

import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;

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

    private RecyclerView rvReservas;
    private TextView tvFechaEjecucion;

    private List<Reserva> reservasList;
    private ReservaRVAdapter adapter;

    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_reservas,container,false);
        bindComponents(v);
        myCallBack.udUI(FragmentReservasSaliendoElDia.TAG);
        setItUp();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        myCallBack = (MyCallBack)context;
        super.onAttach(context);
    }

    private void bindComponents(View v){
        rvReservas = (RecyclerView)v.findViewById(R.id.rv_reservas);
        tvFechaEjecucion = (TextView)v.findViewById(R.id.tv_fecha_ejecucion_freservas);
    }

    private void setItUp(){
        tvFechaEjecucion.setText(DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
        tvFechaEjecucion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DateHandler.showDatePicker(getContext(), tvFechaEjecucion, new DateHandler.DatePickerCallBack() {
                    @Override
                    public void dateSelected() {
                        udReservasList();
                        adapter.setReservaList(reservasList);
                    }
                });
            }
        });

        //reservasList = ReservaBDHandler.getAllReservasFromDB(getContext());
        udReservasList();
        adapter = new ReservaRVAdapter(getContext(),reservasList, ReservaRVAdapter.Modo.GENERAL,this);
        rvReservas.setAdapter(adapter);
        rvReservas.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    private void udReservasList(){
        reservasList = ReservaBDHandler.getReservaFromDB(getContext(),
                "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_FECHA_EJECUCION+"=?",
                new String[]{DateHandler.formatDateToStoreInDB(tvFechaEjecucion.getText().toString())});
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

    private void enviarMail(){

    }


    @Override
    public void itemClicked(int position) {
        //Toast.makeText(getContext(),"Item clicked: "+position,Toast.LENGTH_SHORT).show();
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
