package com.example.usuario.aavv.Reservas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
import com.example.usuario.aavv.MainActivity;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;
import com.example.usuario.aavv.Util.MyEmail;
import com.example.usuario.aavv.Util.MyExcel;
import com.example.usuario.aavv.Util.Util;

import java.io.File;
import java.util.ArrayList;
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
        String text = "";
        if(!reservaList.isEmpty()) {
            double[] infoTotal = getTotales();
            text = "Totales: " + String.valueOf((int)(infoTotal[0])) + " pax  " + String.valueOf(infoTotal[1]) + " usd";
            if(MySharedPreferences.getIncluirPrecioCUP(getContext())&&MySharedPreferences.getTasaCUP(getContext())>0){
                text += " ("+infoTotal[1]*MySharedPreferences.getTasaCUP(getContext())+" cup)";
            }
        } else {
            text = "No hay información para mostrar";
        }
        tvInfo.setText(text);
    }

    private double[] getTotales(){
        double total = 0;
        double cantPax = 0;
        for(Reserva reserva:reservaList){
            if(reserva.getEstado()==Reserva.ESTADO_ACTIVO ||
                    reserva.getEstado()==Reserva.ESTADO_CANCELADO && reserva.getFechaCancelacion()!=null &&
                            !reserva.getFechaCancelacion().equals(tvFechaLiquidacion.getText().toString())) {
                total = total + reserva.getPrecio();
                cantPax = cantPax + reserva.getAdultos() + reserva.getMenores() + reserva.getInfantes() + reserva.getAcompanantes();
            }else if(reserva.getEstado()==Reserva.ESTADO_DEVUELTO){
                if(reserva.getFechaConfeccion().equals(reserva.getFechaDevolucion())) {
                    cantPax = cantPax + reserva.getAdultos() + reserva.getMenores() + reserva.getInfantes() + reserva.getAcompanantes();
                    total = total + reserva.getPrecio() - reserva.getImporteDevuelto();
                } else if(reserva.getFechaConfeccion().equals(tvFechaLiquidacion.getText().toString())){
                    total = total + reserva.getPrecio();
                    cantPax = cantPax + reserva.getAdultos() + reserva.getMenores() + reserva.getInfantes() + reserva.getAcompanantes();
                } else if(reserva.getFechaDevolucion().equals(tvFechaLiquidacion.getText().toString())) {
                    total = total - reserva.getImporteDevuelto();
                }
            }
        }
        //return String.valueOf(cantPax) + " pax  " + String.valueOf(total) + " usd";
        return new double[]{cantPax,total};
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

/*    private String getInfoVendedor(){
        String texto = "";
        if(!MySharedPreferences.getAgenciaVendedor(getContext()).equals("")){
            texto += "Agencia: "+MySharedPreferences.getAgenciaVendedor(getContext());
        }
        if(!MySharedPreferences.getNombreVendedor(getContext()).equals("")){
            if(!texto.equals("")) {
                texto += "\n";
            }
            texto += "Vendedor: " + MySharedPreferences.getNombreVendedor(getContext());
        }
        if(!MySharedPreferences.getTelefonoVendedor(getContext()).equals("")){
            if(!texto.equals("")) {
                texto += "\n";
            }
            texto += "Contacto: "+MySharedPreferences.getTelefonoVendedor(getContext());
        }
        return texto;
    }*/

/*    private String getCuerpoMail(){
        String cuerpo = "";
        cuerpo += getInfoVendedor();

        if(!cuerpo.equals("")) {
            cuerpo += "\n\n";
        }
        cuerpo += "Venta del día: "+ tvFechaLiquidacion.getText().toString();
        for (Reserva reserva:getReservasReporteVenta()){
            cuerpo += "\n\n" + Reserva.toString(getContext(),reserva,Reserva.INFO_REPORTE_VENTA);
        }
        return cuerpo;
    }*/

/*    private void enviarMailVentaDelDia(){
        MyEmail.setUpEmail(getContext(),new MyEmail(new String[]{},"Venta del día "+ tvFechaLiquidacion.getText().toString(),getCuerpoMail()));
    }*/

/*    private void generarExcelReporteDeVenta(){
        //if(reservaList.size()<1){return;}
        try {
            //File rutaSD = Environment.getExternalStorageDirectory();
            File rutaSD = new File(Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name));
            if(!rutaSD.exists()){rutaSD.mkdir();}
            rutaSD = new File(rutaSD.getAbsolutePath()+"/Reportes de venta");
            if(!rutaSD.exists()){rutaSD.mkdir();}
            rutaSD = new File(rutaSD.getAbsolutePath()+"/"+ tvFechaLiquidacion.getText().toString().substring(6));
            if(!rutaSD.exists()){rutaSD.mkdir();}
            rutaSD = new File(rutaSD.getAbsolutePath()+"/"+mesesDelAno[Integer.parseInt(tvFechaLiquidacion.getText().toString().substring(3,5))-1]);
            if(!rutaSD.exists()){rutaSD.mkdir();}

            //File rutaSD = Environment.getExternalFilesDir(null);
            File file = new File(rutaSD.getAbsolutePath(), tvFechaLiquidacion.getText().toString().replace("/","") + ".xls");
            List<Reserva> reservasReportar = getReservasReporteVenta();
            if(reservasReportar.isEmpty()){
                Toast.makeText(getContext(),"No existen reservas para reportar",Toast.LENGTH_SHORT).show();
                return;
            }
            if(MyExcel.generarExcelReporteVenta(getContext(),file,reservasReportar, tvFechaLiquidacion.getText().toString())){
                myCallBack.showSnackBar("Excel generado correctamente: "+file);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Mensaje error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }*/

/*    private List<Reserva> getReservasReporteVenta(){
        List<Reserva> resultadoBruto = ReservaBDHandler.getReservasFromDB(getContext(),
                "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_FECHA_REPORTE_VENTA+"=? AND "+
                        ReservaBDHandler.CAMPO_ESTADO+"!=?",
                new String[]{
                        DateHandler.formatDateToStoreInDB(tvFechaLiquidacion.getText().toString()),
                        String.valueOf(Reserva.ESTADO_CANCELADO)});
        List<Reserva> reservasRepVenta = new ArrayList<>();
        for (Reserva reserva: resultadoBruto){
            if(reserva.getEstado()==Reserva.ESTADO_ACTIVO ||
                    reserva.getEstado()==Reserva.ESTADO_DEVUELTO && reserva.isDevParcial()){
                reservasRepVenta.add(reserva);
            }
        }
        Collections.sort(reservasRepVenta,Reserva.ordenarPorTE);
        return reservasRepVenta;
    }*/

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

/*    private void checkForPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_EXTORAGE);
        } else {
            // Permission is already granted, call the function that does what you need
            generarExcelReporteDeVenta();
        }
    }*/

/*    @Override
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
    }*/

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
        myCallBack.setLastFechaLiq(tvFechaLiquidacion.getText().toString());
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
