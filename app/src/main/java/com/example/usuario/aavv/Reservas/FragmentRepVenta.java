package com.example.usuario.aavv.Reservas;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
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
 * Created by user on 4/3/2024.
 */

public class FragmentRepVenta extends Fragment implements ReservaRVAdapter.MyCallBack{

    public static final String TAG = "FragmentRepVenta";

    private final String[] mesesDelAno = new String[]{"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};

    private TextView tvFecha;
    private RecyclerView rvReservas;

    private List<Reserva> reservaList;
    private ReservaRVAdapter adapter;

    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_repventa,container,false);
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
        tvFecha = (TextView)view.findViewById(R.id.tv_fecha_repventa);
        rvReservas = (RecyclerView)view.findViewById(R.id.rv_reservas_repventa);
    }

    private void setItUp(){
        myCallBack.udUI(TAG);
        if(myCallBack.getLastFechaRepVenta()==null || myCallBack.getLastFechaRepVenta().isEmpty()){
            tvFecha.setText(DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR));
            myCallBack.setLastFechaRepVenta(tvFecha.getText().toString());
        }else {
            tvFecha.setText(myCallBack.getLastFechaRepVenta());
        }
        udReservaList();
        adapter = new ReservaRVAdapter(getContext(),Reserva.toObjectList(reservaList), ReservaRVAdapter.Modo.REP_VENTA,this);
        rvReservas.setAdapter(adapter);
        rvReservas.setLayoutManager(new LinearLayoutManager(getContext()));
        tvFecha.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DateHandler.showDatePicker(getContext(), tvFecha, new DateHandler.DatePickerCallBack() {
                    @Override
                    public void dateSelected() {
                        myCallBack.setLastFechaRepVenta(tvFecha.getText().toString());
                        udUI();
                    }
                });
            }
        });
    }

    private void udUI(){
        udReservaList();
        adapter.setReservaList(Reserva.toObjectList(reservaList));
    }

    @Override
    public void itemClicked(int position) {
        myCallBack.setLastFechaRepVenta(tvFecha.getText().toString());
        myCallBack.setUpFragmentReservar(reservaList.get(position).getNoTE());
    }

    private void udReservaList(){
        List<Reserva> resultadoBruto = ReservaBDHandler.getReservasFromDB(getContext(),
                "SELECT * FROM "+ReservaBDHandler.TABLE_NAME+" WHERE "+ReservaBDHandler.CAMPO_INCLUIR_REP_VENTA+"=? AND "+
                        ReservaBDHandler.CAMPO_FECHA_REPORTE_VENTA+"=? AND "+ReservaBDHandler.CAMPO_ESTADO+"!=?",
                new String[]{
                        String.valueOf(1),
                        DateHandler.formatDateToStoreInDB(tvFecha.getText().toString()),
                        String.valueOf(Reserva.ESTADO_CANCELADO)});
        if(reservaList==null){reservaList = new ArrayList<>();}
        if(!reservaList.isEmpty()){reservaList.clear();}
        for (Reserva reserva: resultadoBruto){
            if(reserva.getEstado()==Reserva.ESTADO_ACTIVO ||
                    reserva.getEstado()==Reserva.ESTADO_DEVUELTO && reserva.isDevParcial()){
                reservaList.add(reserva);
            }
        }
        Collections.sort(reservaList,Reserva.ordenarPorTE);
    }

    private void enviarMailVentaDelDia(){
        if(reservaList.isEmpty()){
            Toast.makeText(getContext(),"No existen reservas para reportar",Toast.LENGTH_SHORT).show();
            return;
        }
        MyEmail.setUpEmail(getContext(),new MyEmail(new String[]{},"Venta del día "+ tvFecha.getText().toString(),getCuerpoMail()));
    }

    private String getCuerpoMail(){
        StringBuilder cuerpo = new StringBuilder();
        cuerpo.append(getInfoVendedor());

        if(cuerpo.length() > 0) {
            cuerpo.append("\n\n");
        }
        cuerpo.append("Venta del día: ").append(tvFecha.getText().toString());
        for (Reserva reserva:reservaList){
            cuerpo.append("\n\n").append(Reserva.toString(getContext(), reserva, Reserva.INFO_REPORTE_VENTA));
        }
        return cuerpo.toString();
    }

    private String getInfoVendedor(){
        String texto = "";
        if(!MySharedPreferences.getAgenciaVendedor(getContext()).isEmpty()){
            texto += "Agencia: "+MySharedPreferences.getAgenciaVendedor(getContext());
        }
        if(!MySharedPreferences.getNombreVendedor(getContext()).isEmpty()){
            if(!texto.isEmpty()) {
                texto += "\n";
            }
            texto += "Vendedor: " + MySharedPreferences.getNombreVendedor(getContext());
        }
        if(!MySharedPreferences.getTelefonoVendedor(getContext()).isEmpty()){
            if(!texto.isEmpty()) {
                texto += "\n";
            }
            texto += "Contacto: "+MySharedPreferences.getTelefonoVendedor(getContext());
        }
        return texto;
    }

    private void generarExcelReporteDeVenta(){
        if(reservaList.isEmpty()){
            Toast.makeText(getContext(),"No existen reservas para reportar",Toast.LENGTH_SHORT).show();
            return;
        }
        String ruta = getRuta();
        if(ruta==null){
            Toast.makeText(getContext(),"No se pudo crear el reporte",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            File file = new File(ruta, tvFecha.getText().toString().replace("/","") + ".xls");
            if(MyExcel.generarExcelReporteVenta(getContext(),file,reservaList, tvFecha.getText().toString())){
                myCallBack.showSnackBar("Excel generado correctamente: "+file);
            }
        } catch (Exception e) {
            Log.e("excel","Error creando excel",e);
        }
    }

    private String getRuta(){
        try {
            File rutaSD = new File(Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name));
            if(!rutaSD.exists()){
                if(rutaSD.mkdir()){
                    Log.d("excel","Directorio creado");
                }else {
                    Log.d("excel","mkdir() no crea el directorio");
                    return null;
                }
            }
            rutaSD = new File(rutaSD.getAbsolutePath()+"/Reportes de venta");
            if(!rutaSD.exists()){rutaSD.mkdir();}
            rutaSD = new File(rutaSD.getAbsolutePath()+"/"+ tvFecha.getText().toString().substring(6));
            if(!rutaSD.exists()){rutaSD.mkdir();}
            rutaSD = new File(rutaSD.getAbsolutePath()+"/"+mesesDelAno[Integer.parseInt(tvFecha.getText().toString().substring(3,5))-1]);
            if(!rutaSD.exists()){rutaSD.mkdir();}
            return rutaSD.getAbsolutePath();
        }catch (Exception e){
            Log.e("excel","Error creando ruta",e);
            return null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu!=null){menu.clear();}
        inflater.inflate(R.menu.menu_frag_repventa,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_item_enviar_mail_venta_del_dia) {
            enviarMailVentaDelDia();
        } else if (itemId == R.id.menu_item_excel_reporte_venta) {
            //checkForPermissions();
            if(Util.isPermissionGranted(getContext())){
                generarExcelReporteDeVenta();
                Log.d("permisos","Permision is granted");
            }else {
                Log.d("permisos","Permision is not granted");
                myCallBack.requestPermisionAccessExternalStorage();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public interface MyCallBack{
        void udUI(String tag);
        void setUpFragmentReservar(String id);
        void showSnackBar(String mensaje);
        void setLastFechaRepVenta(String fecha);
        String getLastFechaRepVenta();
        void requestPermisionAccessExternalStorage();
    }
}
