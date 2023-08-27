package com.example.usuario.aavv.Reservas;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;

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
        spinnerAgencias = (AppCompatSpinner)view.findViewById(R.id.sp_agencias);
        tvFechaDesde = (TextView)view.findViewById(R.id.tv_fecha_desde);
        tvFechaHasta = (TextView)view.findViewById(R.id.tv_fecha_hasta);
        tvInfoVenta = (TextView)view.findViewById(R.id.tv_info_venta);
        rvReservas = (RecyclerView)view.findViewById(R.id.rv_reservas);
    }

    private void setItUp(){
        myCallBack.udUI(TAG);
        String desde = "01" + DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR).substring(2,10);
        tvFechaDesde.setText(desde);
        tvFechaHasta.setText(DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
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
        rvAdapter = new ReservaRVAdapter(getContext(), listaReservas, ReservaRVAdapter.Modo.LIQUIDACION, new ReservaRVAdapter.MyCallBack() {
            @Override
            public void itemClicked(int position) {
                myCallBack.setUpFragmentReservar(listaReservas.get(position).getId());
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
        if(!DateHandler.areDatesInOrder(tvFechaDesde.getText().toString(),tvFechaHasta.getText().toString())){
            Toast.makeText(getContext(),"Las fechas no son correctas",Toast.LENGTH_SHORT).show();
            //return;
        }
        String selected = "";
        if(listaReservas.size()!=0){
            selected = spinnerAgencias.getSelectedItem().toString();
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
        List<Reserva> reservasDelPeriodo = ReservaBDHandler.getReservasFromDB(getContext(),tvFechaDesde.getText().toString(),tvFechaHasta.getText().toString());
        listaAgencias.clear();
        listaAgencias.add(TODAS);
        if(reservasDelPeriodo.size()!=0) {
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
            listaAgencias.addAll(nuevasAgencias);
        }
    }

    private void setUpSpinner(){
        ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,listaAgencias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgencias.setAdapter(adapter);
    }

    private void udUI(){
        udReservasRV();
        udTVInfo();
    }

    private void udReservasRV(){
        listaReservas.clear();
        String selected = spinnerAgencias.getSelectedItem().toString();
        if(selected.equals(TODAS)){
            listaReservas = ReservaBDHandler.getReservasFromDB(getContext(),tvFechaDesde.getText().toString(),tvFechaHasta.getText().toString());
        }else {
            listaReservas = ReservaBDHandler.getReservasFromDB(getContext(),tvFechaDesde.getText().toString(),tvFechaHasta.getText().toString(),selected);
        }
        if(listaReservas.size()!=0){
            Collections.sort(listaReservas,Reserva.ordenarPorTE);
        }
        rvAdapter.setReservaList(listaReservas);
    }

    private void udTVInfo(){
        String texto = "";
        String selected = spinnerAgencias.getSelectedItem().toString();
        if(selected.equals(TODAS)){
            if(listaAgencias.size()>1) {
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
            }else {texto = "No hay reservas para mostrar";}
        } else {
            texto = getCantPax(selected) + " pax " + getImporteTotal(selected) + " usd";
        }
        tvInfoVenta.setText(texto);
    }

    private int getCantPax(String agencia){
        int totalPax = 0;
        for(Reserva reserva:listaReservas){
            if(reserva.getAgencia().equals(agencia)){
                totalPax = totalPax + reserva.getAdultos() + reserva.getMenores() + reserva.getInfantes();
            }
        }
        return totalPax;
    }

    private double getImporteTotal(String agencia){
        double importeTotal = 0;
        for(Reserva reserva:listaReservas){
            if(reserva.getAgencia().equals(agencia)){
                importeTotal = importeTotal + reserva.getPrecio();
            }
        }
        return importeTotal;
    }

    public interface MyCallBack{
        void udUI(String tag);
        void setUpFragmentReservar(long id);
    }
}
