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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import java.util.Collections;
import java.util.List;


/**
 * Created by usuario on 3/08/2023.
 */

public class FragmentLiquidacion extends Fragment implements ReservaRVAdapter.MyCallBack {

    public static final String TAG = "FragmentLiquidacion";

    private final String[] mesesDelAno = new String[]{"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};

    private LinearLayout layoutInfo;
    private TextView tvFechaConfeccion, tvInfo;
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
        layoutInfo = (LinearLayout)view.findViewById(R.id.layout_info);
        tvFechaConfeccion = (TextView) view.findViewById(R.id.tv_fecha_confeccion_fliquidacion);
        tvInfo = (TextView) view.findViewById(R.id.tv_info_venta);
        rvReservas = (RecyclerView) view.findViewById(R.id.rv_reservas_fliquidacion);
    }

    private void setItUp(){
        myCallBack.udUI(FragmentLiquidacion.TAG);
        tvFechaConfeccion.setText(DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
        udReservaList();
        udTvInfo();
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

        layoutInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Util.copyToClipBoard(getContext(),"Venta del "+tvFechaConfeccion.getText().toString()+"\n"+tvInfo.getText().toString());
                return true;
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
            text = "Totales: " + getTotales();
        } else {
            text = "No hay información para mostrar";
        }
        tvInfo.setText(text);
    }

    private String getTotales(){
        double total = 0;
        int cantPax = 0;
        for(Reserva reserva:reservaList){
            total = total + reserva.getPrecio();
            cantPax = cantPax + reserva.getAdultos() + reserva.getMenores() + reserva.getInfantes() + reserva.getAcompanantes();
        }
        return String.valueOf(cantPax) + " pax  " + String.valueOf(total) + " usd";
    }

    private void udReservaList(){
        reservaList = ReservaBDHandler.getReservasFromDB(getContext(),
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
        cuerpo += "Venta del día: "+tvFechaConfeccion.getText().toString();

        for (Reserva reserva:reservaList){
            cuerpo += "\n\n" + Reserva.toString(reserva);
        }
        return cuerpo;
    }

    private void enviarMailVentaDelDia(){
        MyEmail.setUpEmail(getContext(),new MyEmail(new String[]{},"Venta del día "+tvFechaConfeccion.getText().toString(),getCuerpoMail()));
    }

    private void generarExcelReporteDeVenta(){
        if(reservaList.size()<1){return;}
        try {
            //File rutaSD = Environment.getExternalStorageDirectory();
            File rutaSD = new File(Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name));
            if(!rutaSD.exists()){rutaSD.mkdir();}
            rutaSD = new File(rutaSD.getAbsolutePath()+"/Reportes de venta");
            if(!rutaSD.exists()){rutaSD.mkdir();}
            rutaSD = new File(rutaSD.getAbsolutePath()+"/"+tvFechaConfeccion.getText().toString().substring(6));
            if(!rutaSD.exists()){rutaSD.mkdir();}
            rutaSD = new File(rutaSD.getAbsolutePath()+"/"+mesesDelAno[Integer.parseInt(tvFechaConfeccion.getText().toString().substring(3,5))-1]);
            if(!rutaSD.exists()){rutaSD.mkdir();}

            //File rutaSD = Environment.getExternalFilesDir(null);
            File file = new File(rutaSD.getAbsolutePath(), tvFechaConfeccion.getText().toString().replace("/","") + ".xls");
            if(MyExcel.generarExcelReporteVenta(getContext(),file,reservaList)){
                //Toast.makeText(getContext(),"Excel generado correctamente: "+file,Toast.LENGTH_SHORT).show();
                myCallBack.showSnackBar("Excel generado correctamente: "+file);
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
            case R.id.menu_item_excel_reporte_venta:
                checkForPermissions();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void itemClicked(int position) {
        myCallBack.setUpFragmentReservar(reservaList.get(position).getId());
    }

    public interface MyCallBack{
        void udUI(String tag);
        void setUpFragmentReservar(long id);
        void showSnackBar(String mensaje);
    }
}
