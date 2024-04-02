package com.example.usuario.aavv.Ajustes;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Reservas.Reserva;
import com.example.usuario.aavv.Reservas.ReservaBDHandler;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by usuario on 9/08/2023.
 */

public class FragmentAjustes extends Fragment {

    public static final String TAG = "FragmentAjustes";

    private LinearLayout layoutEditarNombreVendedor,layoutEditarTelefonoVendedor,layoutEditarAgenciaVendedor;
    private RelativeLayout layoutTasaCUP;
    private EditText etNombreVendedor, etTelefonoVendedor, etAgenciaVendedor, etTasaCUP;
    private TextView tvNombreVendedor, tvTelefonoVendedor, tvAgenciaVendedor;
    private Button btnNombreVendedor, btnTelefonoVendedor,btnAgenciaVendedor;
    private RadioGroup radioGroup;
    private RadioButton rbHomePage, rbLiquidacion, rbExcDelDia;
    private CheckBox cbIncluirDevEnLiq, cbPredecirPrecio, cbIncluirPrecioCUP;

    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ajustes,container,false);
        setHasOptionsMenu(true);
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
        layoutEditarNombreVendedor = (LinearLayout)view.findViewById(R.id.layout_editar_nombre_vendedor);
        layoutEditarTelefonoVendedor = (LinearLayout)view.findViewById(R.id.layout_editar_telefono_vendedor);
        layoutEditarAgenciaVendedor = (LinearLayout)view.findViewById(R.id.layout_editar_agencia_vendedor);
        etNombreVendedor = (EditText)view.findViewById(R.id.et_nombre_vendedor);
        etTelefonoVendedor = (EditText)view.findViewById(R.id.et_telefono_vendedor);
        etAgenciaVendedor = (EditText)view.findViewById(R.id.et_agencia_vendedor);
        tvNombreVendedor = (TextView)view.findViewById(R.id.tv_nombre_vendedor);
        tvTelefonoVendedor = (TextView)view.findViewById(R.id.tv_telefono_vendedor);
        tvAgenciaVendedor = (TextView)view.findViewById(R.id.tv_agencia_vendedor);
        btnNombreVendedor = (Button)view.findViewById(R.id.btn_nombre_vendedor);
        btnTelefonoVendedor = (Button)view.findViewById(R.id.btn_telefono_vendedor);
        btnAgenciaVendedor = (Button)view.findViewById(R.id.btn_agencia_vendedor);
        radioGroup = (RadioGroup)view.findViewById(R.id.rg_fragment_inicio);
        rbHomePage = (RadioButton)view.findViewById(R.id.rb_iniciar_home_page);
        rbLiquidacion = (RadioButton)view.findViewById(R.id.rb_iniciar_liquidacion);
        rbExcDelDia = (RadioButton)view.findViewById(R.id.rb_iniciar_reservas);
        cbIncluirDevEnLiq = (CheckBox)view.findViewById(R.id.cb_incluir_devolucion);
        cbPredecirPrecio = (CheckBox)view.findViewById(R.id.cb_predecir_precio);
        cbIncluirPrecioCUP = (CheckBox)view.findViewById(R.id.cb_incluir_precio_cup);
        etTasaCUP = (EditText)view.findViewById(R.id.et_tasa_cup);
        layoutTasaCUP = (RelativeLayout)view.findViewById(R.id.layout_tasa_cup);
    }

    private void setItUp(){
        myCallBack.udUI(FragmentAjustes.TAG);
        showInfoVendedor();
        tvNombreVendedor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                tvNombreVendedor.setVisibility(View.GONE);
                layoutEditarNombreVendedor.setVisibility(View.VISIBLE);
                etNombreVendedor.setText(MySharedPreferences.getNombreVendedor(getContext()));
            }
        });
        tvTelefonoVendedor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                tvTelefonoVendedor.setVisibility(View.GONE);
                layoutEditarTelefonoVendedor.setVisibility(View.VISIBLE);
                etTelefonoVendedor.setText(MySharedPreferences.getTelefonoVendedor(getContext()));
            }
        });
        tvAgenciaVendedor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                tvAgenciaVendedor.setVisibility(View.GONE);
                layoutEditarAgenciaVendedor.setVisibility(View.VISIBLE);
                etAgenciaVendedor.setText(MySharedPreferences.getAgenciaVendedor(getContext()));
            }
        });

        btnNombreVendedor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MySharedPreferences.storeNombreVendedor(getContext(),etNombreVendedor.getText().toString());
                showInfoVendedor();
            }
        });
        btnTelefonoVendedor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MySharedPreferences.storeTelefonoVendedor(getContext(),etTelefonoVendedor.getText().toString());
                showInfoVendedor();
            }
        });
        btnAgenciaVendedor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MySharedPreferences.storeAgenciaVendedor(getContext(),etAgenciaVendedor.getText().toString());
                showInfoVendedor();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int idItemChecked) {
                switch (idItemChecked){
                    case R.id.rb_iniciar_home_page:
                        MySharedPreferences.storeFragmentInicio(getContext(), MisConstantes.INICIAR_HOME_PAGE);
                        break;
                    case R.id.rb_iniciar_liquidacion:
                        MySharedPreferences.storeFragmentInicio(getContext(),MisConstantes.INICIAR_LIQUIDACIONES);
                        break;
                    case R.id.rb_iniciar_reservas:
                        MySharedPreferences.storeFragmentInicio(getContext(),MisConstantes.INICIAR_EXCURSIONES_SALIENDO);
                        break;
                }
            }
        });

        radioGroup.clearCheck();
        if(MySharedPreferences.getFragmentInicio(getContext())==MisConstantes.INICIAR_HOME_PAGE){
            rbHomePage.setChecked(true);
        }else if(MySharedPreferences.getFragmentInicio(getContext())==MisConstantes.INICIAR_LIQUIDACIONES){
            rbLiquidacion.setChecked(true);
        }else if(MySharedPreferences.getFragmentInicio(getContext())==MisConstantes.INICIAR_EXCURSIONES_SALIENDO){
            rbExcDelDia.setChecked(true);
        }

        cbIncluirDevEnLiq.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MySharedPreferences.storeIncluirDevEnLiquidacion(getContext(),compoundButton.isChecked());
            }
        });

        cbIncluirDevEnLiq.setChecked(MySharedPreferences.getIncluirDevEnLiquidacion(getContext()));

        cbPredecirPrecio.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MySharedPreferences.storePredecirPrecio(getContext(),compoundButton.isChecked());
            }
        });

        cbPredecirPrecio.setChecked(MySharedPreferences.getPredecirPrecio(getContext()));

        cbIncluirPrecioCUP.setChecked(MySharedPreferences.getIncluirPrecioCUP(getContext()));
        udEstadoETTasa();

        cbIncluirPrecioCUP.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MySharedPreferences.storeIncluirPrecioCUP(getContext(),compoundButton.isChecked());
                udEstadoETTasa();
            }
        });

        if(MySharedPreferences.getTasaCUP(getContext())!=0) {
            etTasaCUP.setText(String.valueOf(MySharedPreferences.getTasaCUP(getContext())));
        }else {
            etTasaCUP.setText("");
        }

        etTasaCUP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if(!etTasaCUP.getText().toString().equals("")) {
                        MySharedPreferences.storeTasaCUP(getContext(), Float.parseFloat(etTasaCUP.getText().toString()));
                    }else {
                        MySharedPreferences.storeTasaCUP(getContext(),0);
                    }
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void udEstadoETTasa(){
        if(cbIncluirPrecioCUP.isChecked()){
            etTasaCUP.setEnabled(true);
        }else {
            etTasaCUP.setEnabled(false);
        }
    }

    private void showInfoVendedor(){
        if(MySharedPreferences.getNombreVendedor(getContext()).equals("")){
            layoutEditarNombreVendedor.setVisibility(View.VISIBLE);
            tvNombreVendedor.setVisibility(View.GONE);
        }else {
            layoutEditarNombreVendedor.setVisibility(View.GONE);
            String nombre = "nombre: " + MySharedPreferences.getNombreVendedor(getContext());
            tvNombreVendedor.setText(nombre);
            tvNombreVendedor.setVisibility(View.VISIBLE);
        }
        if(MySharedPreferences.getTelefonoVendedor(getContext()).equals("")){
            layoutEditarTelefonoVendedor.setVisibility(View.VISIBLE);
            tvTelefonoVendedor.setVisibility(View.GONE);
        }else {
            layoutEditarTelefonoVendedor.setVisibility(View.GONE);
            String telefono = "teléfono: " + MySharedPreferences.getTelefonoVendedor(getContext());
            tvTelefonoVendedor.setText(telefono);
            tvTelefonoVendedor.setVisibility(View.VISIBLE);
        }
        if(MySharedPreferences.getAgenciaVendedor(getContext()).equals("")){
            layoutEditarAgenciaVendedor.setVisibility(View.VISIBLE);
            tvAgenciaVendedor.setVisibility(View.GONE);
        }else {
            layoutEditarAgenciaVendedor.setVisibility(View.GONE);
            String agencia = "agencia: " + MySharedPreferences.getAgenciaVendedor(getContext());
            tvAgenciaVendedor.setText(agencia);
            tvAgenciaVendedor.setVisibility(View.VISIBLE);
        }
    }

    private void exportarBD(){
        File carpeta = new File(Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name));
        if(!carpeta.exists()){carpeta.mkdir();}
        carpeta = new File(carpeta.getAbsolutePath()+"/Salva BD");
        if(!carpeta.exists()){carpeta.mkdir();}

        final String archivoAgenda = carpeta.toString() + "/" + "BD"+
                DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR).replace("/","")+".csv";

        try {
            FileWriter fileWriter = new FileWriter(archivoAgenda);
            addResservasToExport(fileWriter);

            fileWriter.close();
            myCallBack.showSnackBar("Salva creada correctamente: "+archivoAgenda);
        } catch (Exception e) {
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void addResservasToExport(FileWriter fileWriter){
        List<Reserva> listaReservas = ReservaBDHandler.getAllReservasFromDB(getContext());
        if(listaReservas.isEmpty()){
            Toast.makeText(getContext(), "No hay reservas registradas", Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            String firstRow = "Tabla: "+ReservaBDHandler.TABLE_NAME+"\n";
            fileWriter.append(firstRow);
            for(Reserva reserva:listaReservas){
                fileWriter.append(String.valueOf(reserva.getId()));
                fileWriter.append(",");
                fileWriter.append(reserva.getNoTE());
                fileWriter.append(",");
                fileWriter.append(formatStringToExport(reserva.getExcursion()));
                fileWriter.append(",");
                fileWriter.append(reserva.getAgencia());
                fileWriter.append(",");
                fileWriter.append(reserva.getNoHab());
                fileWriter.append(",");
                fileWriter.append(reserva.getCliente());
                fileWriter.append(",");
                fileWriter.append(reserva.getHotel());
                fileWriter.append(",");
                fileWriter.append(reserva.getFechaConfeccion());
                fileWriter.append(",");
                fileWriter.append(reserva.getFechaEjecucion());
                fileWriter.append(",");
                fileWriter.append(reserva.getFechaOriginalEjecucion());
                fileWriter.append(",");
                fileWriter.append(reserva.getFechaReporteVenta());
                fileWriter.append(",");
                fileWriter.append(String.valueOf(reserva.getAdultos()));
                fileWriter.append(",");
                fileWriter.append(String.valueOf(reserva.getMenores()));
                fileWriter.append(",");
                fileWriter.append(String.valueOf(reserva.getInfantes()));
                fileWriter.append(",");
                fileWriter.append(String.valueOf(reserva.getAcompanantes()));
                fileWriter.append(",");
                fileWriter.append(reserva.getIdioma());
                fileWriter.append(",");
                fileWriter.append(String.valueOf(reserva.getPrecio()));
                fileWriter.append(",");
                fileWriter.append(String.valueOf(reserva.getEstado()));
                fileWriter.append(",");
                fileWriter.append(reserva.getFechaDevolucion());
                fileWriter.append(",");
                fileWriter.append(reserva.getFechaCancelacion());
                fileWriter.append(",");
                fileWriter.append(String.valueOf(reserva.getImporteDevuelto()));
                fileWriter.append(",");
                fileWriter.append(reserva.getHistorial());
                fileWriter.append(",");
                String observaciones = formatStringToExport(reserva.getObservaciones());
                fileWriter.append(observaciones);
                fileWriter.append("\n");
            }
        }catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            //System.out.println(e.getMessage());
        }
    }

    private void importarBD(){
        //// TODO: 19/11/2023  
    }

    private String formatStringToExport(String texto){
        return texto.replace(",","#").replace("\n","^");
    }

    private void checkForPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //if(menu!=null){menu.clear();}
        //inflater.inflate(R.menu.menu_frag_ajustes,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_exportar_bd:
                checkForPermissions();
                exportarBD();
                break;
            case R.id.menu_item_importar_bd:
                importarBD();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface MyCallBack{
        void udUI(String tag);
        void showSnackBar(String mensaje);
    }

}
