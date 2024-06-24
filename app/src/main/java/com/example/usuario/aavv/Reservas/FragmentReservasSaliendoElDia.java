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

import com.example.usuario.aavv.Excursiones.Excursion;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;
import com.example.usuario.aavv.Util.MyEmail;
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

    //contiene Excursion y Reservas
    private List<Object> reservasList;
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
        reservasList = new ArrayList<>();
        if(myCallBack.getLastFechaEjec()==null) {
            tvFechaEjecucion.setText(DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
            myCallBack.setLastFechaEjec(tvFechaEjecucion.getText().toString());
        }else {
            tvFechaEjecucion.setText(myCallBack.getLastFechaEjec());
        }
        tvFechaEjecucion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DateHandler.showDatePicker(getContext(), tvFechaEjecucion, new DateHandler.DatePickerCallBack() {
                    @Override
                    public void dateSelected() {
                        udFragment();
                        myCallBack.setLastFechaEjec(tvFechaEjecucion.getText().toString());
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
        if(reservasList!=null) {
            reservasList.clear();
        }
        List<Reserva> nuevasReservas;
        nuevasReservas = ReservaBDHandler.getReservasFromDB(getContext(),
                "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_FECHA_EJECUCION+"=? AND "+ReservaBDHandler.CAMPO_ESTADO+"=?",
                new String[]{DateHandler.formatDateToStoreInDB(tvFechaEjecucion.getText().toString()),String.valueOf(Reserva.ESTADO_ACTIVO)});
        if(nuevasReservas.isEmpty()){
            return;
        }
        Collections.sort(nuevasReservas,Reserva.ordenarPorTE);
        Map<String,List<Reserva>> mapReservas = new HashMap<>();
        for(Reserva reserva:nuevasReservas){
            if(mapReservas.containsKey(reserva.getExcursion())){
                mapReservas.get(reserva.getExcursion()).add(reserva);
            }else {
                mapReservas.put(reserva.getExcursion(),new ArrayList<Reserva>());
                mapReservas.get(reserva.getExcursion()).add(reserva);
            }
        }
        for(String excursion:mapReservas.keySet()){
            reservasList.add(new Excursion(excursion));
            Collections.sort(mapReservas.get(excursion),Reserva.ordenarPorHotel);
            reservasList.addAll(mapReservas.get(excursion));
        }
    }

    private void udTvInfo(){
        if(reservasList==null || reservasList.isEmpty()){
            tvInfo.setText("No hay resultados para mostrar");
            return;
        }
        String texto = "";
        String nombreExc = "";

        //creando mensaje
        for(Object object:reservasList) {
            if (object instanceof Excursion) {
                if (!nombreExc.equals("")) {
                    texto += "\n\n";
                }
                nombreExc = ((Excursion) object).getNombre();
                texto += nombreExc + " (" + getTotalPaxExc(nombreExc) + ")";
            } else if (object instanceof Reserva) {
                Reserva reserva = (Reserva) object;
                int cantPax = reserva.getAdultos() + reserva.getMenores() + reserva.getInfantes() + reserva.getAcompanantes();
                texto += "\n" + reserva.getNoTE() + " " + reserva.getHotel() + " " + reserva.getNoHab();
                if (cantPax != 0) {
                    texto += " " + cantPax + " pax";
                }
            }
        }
        tvInfo.setText(texto);
    }


    private int getTotalPaxExc(String nombreExc){
        int totalPax = 0;
        for (Object object:reservasList){
            if(object instanceof Reserva){
                Reserva reserva = (Reserva)object;
                if(reserva.getExcursion().equals(nombreExc)){
                    totalPax += reserva.getAdultos()+reserva.getMenores()+reserva.getInfantes();
                }
            }
        }
        return totalPax;
    }

    private void enviarMail(){
        if(reservasList.isEmpty()){
            Toast.makeText(getContext(), "No hay reservas que enviar por mail", Toast.LENGTH_SHORT).show();
            return;
        }
        String asunto = "Reservas saliendo el día " + tvFechaEjecucion.getText().toString();
        String texto = asunto + "\n\n" + tvInfo.getText().toString();
        MyEmail.setUpEmail(getContext(),new MyEmail(new String[]{},asunto,texto));
    }


    @Override
    public void itemClicked(int position) {
        if(reservasList.get(position) instanceof Reserva){
            Reserva reserva = (Reserva)reservasList.get(position);
            myCallBack.setUpFragmentReservar(reserva.getId());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu != null){menu.clear();}
        inflater.inflate(R.menu.menu_frag_reservas_saliendo_el_dia,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_enviar_mail_reservas_saliendo_el_dia) {
            enviarMail();
        }
        return super.onOptionsItemSelected(item);
    }

    public interface MyCallBack{
        void udUI(String tag);
        void setUpFragmentReservar(long id);
        void setLastFechaEjec(String lastFechaEjec);
        String getLastFechaEjec();
    }
}
