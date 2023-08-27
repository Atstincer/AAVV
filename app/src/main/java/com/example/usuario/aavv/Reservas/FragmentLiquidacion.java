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
import android.widget.Toast;

import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;
import com.example.usuario.aavv.Util.MyEmail;

import java.util.Collections;
import java.util.List;


/**
 * Created by usuario on 3/08/2023.
 */

public class FragmentLiquidacion extends Fragment implements ReservaRVAdapter.MyCallBack {

    public static final String TAG = "FragmentLiquidacion";

    private TextView tvFechaConfeccion,tvTotalUSD;
    private RecyclerView rvReservas;
    private ReservaRVAdapter adapter;

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
        tvFechaConfeccion = (TextView) view.findViewById(R.id.tv_fecha_confeccion_fliquidacion);
        tvTotalUSD = (TextView) view.findViewById(R.id.tv_total_usd_fliquidacion);
        rvReservas = (RecyclerView) view.findViewById(R.id.rv_reservas_fliquidacion);
    }

    private void setItUp(){
        myCallBack.udUI(FragmentLiquidacion.TAG);
        tvFechaConfeccion.setText(DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
        udReservaList();
        getTotalUSD();
        adapter = new ReservaRVAdapter(getContext(),reservaList, ReservaRVAdapter.Modo.LIQUIDACION,this);
        rvReservas.setAdapter(adapter);
        rvReservas.setLayoutManager(new LinearLayoutManager(getContext()));

        tvFechaConfeccion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DateHandler.showDatePicker(getContext(), tvFechaConfeccion, new DateHandler.DatePickerCallBack() {
                    @Override
                    public void dateSelected() {
                        udUI();
                    }
                });
            }
        });
    }

    private void udUI(){
        udReservaList();
        getTotalUSD();
        adapter.setReservaList(reservaList);
    }

    private void getTotalUSD(){
        double total = 0;
        for(Reserva reserva:reservaList){
            total = total + reserva.getPrecio();
        }
        tvTotalUSD.setText(String.valueOf(total));
    }

    private void udReservaList(){
        reservaList = ReservaBDHandler.getReservaFromDB(getContext(),
                "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_FECHA_CONFECCION+"=?",
                new String[]{DateHandler.formatDateToStoreInDB(tvFechaConfeccion.getText().toString())});
        Collections.sort(reservaList,Reserva.ordenarPorTE);
    }

    private String getCuerpoMail(){
        String cuerpo = "";
        if(!MySharedPreferences.getAgenciaVendedor(getContext()).equals("")){
            cuerpo += "Agencia: "+MySharedPreferences.getAgenciaVendedor(getContext())+"\n";
        }
        if(!MySharedPreferences.getNombreVendedor(getContext()).equals("")){
            cuerpo += "Vendedor: "+MySharedPreferences.getNombreVendedor(getContext())+"\n";
        }
        if(!MySharedPreferences.getTelefonoVendedor(getContext()).equals("")){
            cuerpo += "Contacto: "+MySharedPreferences.getTelefonoVendedor(getContext())+"\n";
        }

        if(!MySharedPreferences.getNombreVendedor(getContext()).equals("")||!MySharedPreferences.getAgenciaVendedor(getContext()).equals("")||
                !MySharedPreferences.getTelefonoVendedor(getContext()).equals("")) {
            cuerpo += "\n";
        }
        cuerpo += "Venta del día: "+tvFechaConfeccion.getText().toString()+"\n\n";


        for (Reserva reserva:reservaList){
            cuerpo += "TE: " + reserva.getNoTE() + "\n" +
                    "Excursion: " + reserva.getExcursion() + "\n" +
                    "Fecha: " + reserva.getFechaEjecucion() + "\n" +
                    "Cantidad de pax: " + reserva.getCantPaxs(true) + "\n" +
                    "Hotel: " + reserva.getHotel() + "\n" +
                    "Habitación: " + reserva.getNoHab() + "\n";
            if(!reserva.getIdioma().equals("")){
                cuerpo += "Idioma: " + reserva.getIdioma() + "\n";
            }
            if(!reserva.getObservaciones().equals("")){
                cuerpo += "Observaciones: " + reserva.getObservaciones() + "\n";
            }
            cuerpo += "\n";
        }
        return cuerpo;
    }

    private void enviarMailVentaDelDia(){
        MyEmail.setUpEmail(getContext(),new MyEmail(new String[]{},"Venta del día "+tvFechaConfeccion.getText().toString(),getCuerpoMail()));
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
            case R.id.menu_item_enviar_mail_venta_del_dia:
                if(!reservaList.isEmpty()) {
                    enviarMailVentaDelDia();
                }else {
                    Toast.makeText(getContext(), "No hay reservas para enviar", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(int position) {
        //do nothing for now
    }

    public interface MyCallBack{
        void udUI(String tag);
    }
}
