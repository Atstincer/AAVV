package com.example.usuario.aavv.Ajustes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.aavv.Almacenamiento.AdminSQLiteOpenHelper;
import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
import com.example.usuario.aavv.Excursiones.Excursion;
import com.example.usuario.aavv.Excursiones.ExcursionBDHandler;
import com.example.usuario.aavv.Hoteles.Hotel;
import com.example.usuario.aavv.Hoteles.HotelBDHandler;
import com.example.usuario.aavv.MainActivity;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Reservas.Reserva;
import com.example.usuario.aavv.Reservas.ReservaBDHandler;
import com.example.usuario.aavv.TTOO.TTOO;
import com.example.usuario.aavv.TTOO.TTOOBDHandler;
import com.example.usuario.aavv.Util.DateHandler;
import com.example.usuario.aavv.Util.MisConstantes;
import com.example.usuario.aavv.Util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 9/08/2023.
 */

public class FragmentAjustes extends Fragment {

    public static final String TAG = "FragmentAjustes";

//    private final String carpetaSalva = Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name)+"/Salva BD";

    private LinearLayout layoutEditarNombreVendedor,layoutEditarTelefonoVendedor,layoutEditarAgenciaVendedor;
    private EditText etNombreVendedor, etTelefonoVendedor, etAgenciaVendedor, etTasaCUP;
    private TextView tvNombreVendedor, tvTelefonoVendedor, tvAgenciaVendedor;
    private Button btnNombreVendedor, btnTelefonoVendedor,btnAgenciaVendedor;
    private RadioGroup radioGroup;
    private RadioButton rbHomePage, rbLiquidacion, rbExcDelDia;
    private CheckBox cbIncluirDevEnLiq, cbPredecirPrecio, cbIncluirPrecioCUP;

    private final int SELEC_FILE_SALVA = 200;

    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ajustes,container,false);
        setHasOptionsMenu(true);
        bindComponents(view);
        setItUp();
        showInfo();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        myCallBack = (MyCallBack)context;
        super.onAttach(context);
    }

    private void bindComponents(@NonNull View view){
        layoutEditarNombreVendedor = view.findViewById(R.id.layout_editar_nombre_vendedor);
        layoutEditarTelefonoVendedor = view.findViewById(R.id.layout_editar_telefono_vendedor);
        layoutEditarAgenciaVendedor = view.findViewById(R.id.layout_editar_agencia_vendedor);
        etNombreVendedor = view.findViewById(R.id.et_nombre_vendedor);
        etTelefonoVendedor = view.findViewById(R.id.et_telefono_vendedor);
        etAgenciaVendedor = view.findViewById(R.id.et_agencia_vendedor);
        tvNombreVendedor = view.findViewById(R.id.tv_nombre_vendedor);
        tvTelefonoVendedor = view.findViewById(R.id.tv_telefono_vendedor);
        tvAgenciaVendedor = view.findViewById(R.id.tv_agencia_vendedor);
        btnNombreVendedor = view.findViewById(R.id.btn_nombre_vendedor);
        btnTelefonoVendedor = view.findViewById(R.id.btn_telefono_vendedor);
        btnAgenciaVendedor = view.findViewById(R.id.btn_agencia_vendedor);
        radioGroup = view.findViewById(R.id.rg_fragment_inicio);
        rbHomePage = view.findViewById(R.id.rb_iniciar_home_page);
        rbLiquidacion = view.findViewById(R.id.rb_iniciar_liquidacion);
        rbExcDelDia = view.findViewById(R.id.rb_iniciar_reservas);
        cbIncluirDevEnLiq = view.findViewById(R.id.cb_incluir_devolucion);
        cbPredecirPrecio = view.findViewById(R.id.cb_predecir_precio);
        cbIncluirPrecioCUP = view.findViewById(R.id.cb_incluir_precio_cup);
        etTasaCUP = view.findViewById(R.id.et_tasa_cup);
    }

    private void setItUp(){
        myCallBack.udUI(FragmentAjustes.TAG);
        tvNombreVendedor.setOnClickListener(view -> {
            tvNombreVendedor.setVisibility(View.GONE);
            layoutEditarNombreVendedor.setVisibility(View.VISIBLE);
            etNombreVendedor.setText(MySharedPreferences.getNombreVendedor(getContext()));
        });
        tvTelefonoVendedor.setOnClickListener(view -> {
            tvTelefonoVendedor.setVisibility(View.GONE);
            layoutEditarTelefonoVendedor.setVisibility(View.VISIBLE);
            etTelefonoVendedor.setText(MySharedPreferences.getTelefonoVendedor(getContext()));
        });
        tvAgenciaVendedor.setOnClickListener(view -> {
            tvAgenciaVendedor.setVisibility(View.GONE);
            layoutEditarAgenciaVendedor.setVisibility(View.VISIBLE);
            etAgenciaVendedor.setText(MySharedPreferences.getAgenciaVendedor(getContext()));
        });
        btnNombreVendedor.setOnClickListener(view -> {
            MySharedPreferences.storeNombreVendedor(getContext(),etNombreVendedor.getText().toString());
            showInfoVendedor();
        });
        btnTelefonoVendedor.setOnClickListener(view -> {
            MySharedPreferences.storeTelefonoVendedor(getContext(),etTelefonoVendedor.getText().toString());
            showInfoVendedor();
        });
        btnAgenciaVendedor.setOnClickListener(view -> {
            MySharedPreferences.storeAgenciaVendedor(getContext(),etAgenciaVendedor.getText().toString());
            showInfoVendedor();
        });
        radioGroup.setOnCheckedChangeListener((radioGroup, idItemChecked) -> {
            if(idItemChecked == R.id.rb_iniciar_home_page){
                MySharedPreferences.storeFragmentInicio(getContext(), MisConstantes.INICIAR_HOME_PAGE);
            }else if(idItemChecked == R.id.rb_iniciar_liquidacion){
                MySharedPreferences.storeFragmentInicio(getContext(),MisConstantes.INICIAR_LIQUIDACIONES);
            }else if(idItemChecked == R.id.rb_iniciar_reservas){
                MySharedPreferences.storeFragmentInicio(getContext(),MisConstantes.INICIAR_EXCURSIONES_SALIENDO);
            }
        });
        cbIncluirDevEnLiq.setOnCheckedChangeListener((compoundButton, b) -> MySharedPreferences.storeIncluirDevEnLiquidacion(getContext(),compoundButton.isChecked()));
        cbPredecirPrecio.setOnCheckedChangeListener((compoundButton, b) -> MySharedPreferences.storePredecirPrecio(getContext(),compoundButton.isChecked()));
        cbIncluirPrecioCUP.setOnCheckedChangeListener((compoundButton, b) -> {
            MySharedPreferences.storeIncluirPrecioCUP(getContext(),compoundButton.isChecked());
            udEstadoETTasa();
        });
        etTasaCUP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if(!etTasaCUP.getText().toString().isEmpty()) {
                        MySharedPreferences.storeTasaCUP(getContext(), Float.parseFloat(etTasaCUP.getText().toString()));
                    }else {
                        MySharedPreferences.storeTasaCUP(getContext(),0);
                    }
                }catch (Exception e){
                    Log.e("ajustes","error textChangeListener etTasaCUP",e);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void showInfo(){
        showInfoVendedor();
        //radioGroup.clearCheck();
        if(MySharedPreferences.getFragmentInicio(getContext())==MisConstantes.INICIAR_HOME_PAGE){
            rbHomePage.setChecked(true);
        }else if(MySharedPreferences.getFragmentInicio(getContext())==MisConstantes.INICIAR_LIQUIDACIONES){
            rbLiquidacion.setChecked(true);
        }else if(MySharedPreferences.getFragmentInicio(getContext())==MisConstantes.INICIAR_EXCURSIONES_SALIENDO){
            rbExcDelDia.setChecked(true);
        }
        cbIncluirDevEnLiq.setChecked(MySharedPreferences.getIncluirDevEnLiquidacion(getContext()));
        cbPredecirPrecio.setChecked(MySharedPreferences.getPredecirPrecio(getContext()));
        cbIncluirPrecioCUP.setChecked(MySharedPreferences.getIncluirPrecioCUP(getContext()));
        udEstadoETTasa();
        if(MySharedPreferences.getTasaCUP(getContext())!=0) {
            etTasaCUP.setText(String.valueOf(MySharedPreferences.getTasaCUP(getContext())));
        }else {
            etTasaCUP.setText("");
        }
    }

    private void refreshUI(){
        showInfo();
    }

    private void udEstadoETTasa(){
        etTasaCUP.setEnabled(cbIncluirPrecioCUP.isChecked());
    }

    private void showInfoVendedor(){
        if(MySharedPreferences.getNombreVendedor(getContext()).isEmpty()){
            layoutEditarNombreVendedor.setVisibility(View.VISIBLE);
            tvNombreVendedor.setVisibility(View.GONE);
        }else {
            layoutEditarNombreVendedor.setVisibility(View.GONE);
            String nombre = "nombre: " + MySharedPreferences.getNombreVendedor(getContext());
            tvNombreVendedor.setText(nombre);
            tvNombreVendedor.setVisibility(View.VISIBLE);
        }
        if(MySharedPreferences.getTelefonoVendedor(getContext()).isEmpty()){
            layoutEditarTelefonoVendedor.setVisibility(View.VISIBLE);
            tvTelefonoVendedor.setVisibility(View.GONE);
        }else {
            layoutEditarTelefonoVendedor.setVisibility(View.GONE);
            String telefono = "teléfono: " + MySharedPreferences.getTelefonoVendedor(getContext());
            tvTelefonoVendedor.setText(telefono);
            tvTelefonoVendedor.setVisibility(View.VISIBLE);
        }
        if(MySharedPreferences.getAgenciaVendedor(getContext()).isEmpty()){
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
        new Thread(() -> {
            File carpeta = new File(Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name));
            if(!carpeta.exists()){carpeta.mkdir();}
            carpeta = new File(carpeta.getAbsolutePath()+"/Salva BD");
            if(!carpeta.exists()){carpeta.mkdir();}

            final String archivoSalva = carpeta + "/" + "BD"+
                    DateHandler.getToday(MisConstantes.FormatoFecha.MOSTRAR).replace("/","")+".csv";

            List<Reserva> reservasList = ReservaBDHandler.getAllReservasFromDB(getContext());
            List<Excursion> excursionList = ExcursionBDHandler.getAllExcursionesfromDB(getContext());
            List<TTOO> agenciasList = TTOOBDHandler.getAllTTOOfromDB(getContext());
            List<Hotel> hotelList = HotelBDHandler.getAllHotelesfromDB(getContext());
            if(reservasList.isEmpty() && excursionList.isEmpty() && agenciasList.isEmpty() && hotelList.isEmpty()){
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(),"No hay registros para exportar.",Toast.LENGTH_SHORT).show();
                });
                return;
            }
            try {
                FileWriter fileWriter = new FileWriter(archivoSalva);
                addConfig(fileWriter);
                if(!reservasList.isEmpty()) {
                    addResservasToExport(fileWriter,reservasList);
                }
                if(!excursionList.isEmpty()){
                    addExcursionesToExport(fileWriter,excursionList);
                }
                if(!agenciasList.isEmpty()){
                    addAgenciasToExport(fileWriter,agenciasList);
                }
                if(!hotelList.isEmpty()){
                    addHotelesToExport(fileWriter,hotelList);
                }
                fileWriter.close();
                myCallBack.showSnackBar("Salva creada correctamente: "+archivoSalva);
            } catch (Exception e) {
                Log.d("Exportando","Error creando salva: "+e.getMessage());
            }
        }).start();
    }

    private void addConfig(FileWriter fileWriter){
        try{
            fileWriter.append("Config\n");
            fileWriter.append(MySharedPreferences.getNombreVendedor(getContext()));//nombre
            fileWriter.append("|");
            fileWriter.append(MySharedPreferences.getTelefonoVendedor(getContext()));//telefono
            fileWriter.append("|");
            fileWriter.append(MySharedPreferences.getAgenciaVendedor(getContext()));//agencia
            fileWriter.append("|");
            fileWriter.append(String.valueOf(MySharedPreferences.getFragmentInicio(getContext())));//frag inicio
            fileWriter.append("|");
            fileWriter.append(String.valueOf(MySharedPreferences.getPredecirPrecio(getContext())));//predecir precio
            fileWriter.append("|");
            fileWriter.append(String.valueOf(MySharedPreferences.getIncluirDevEnLiquidacion(getContext())));//incluir dev en liquidacion
            fileWriter.append("|");
            fileWriter.append(String.valueOf(MySharedPreferences.getIncluirPrecioCUP(getContext())));//incluir precio en cup
            fileWriter.append("|");
            fileWriter.append(String.valueOf(MySharedPreferences.getTasaCUP(getContext())));//tasa de cambio
            fileWriter.append("\n");
        }catch (Exception e){
            Log.e("exportando","Error exportando configuracion.",e);
        }
    }

    private void addHotelesToExport(FileWriter fileWriter, List<Hotel> hoteles){
        try{
            String firstRow = HotelBDHandler.TABLE_NAME+"\n";
            fileWriter.append(firstRow);
            for(Hotel hotel:hoteles){
                fileWriter.append(String.valueOf(hotel.getId()));//0
                fileWriter.append("|");
                fileWriter.append(hotel.getNombre());//1
                fileWriter.append("\n");
            }
        }catch (Exception e){
            Log.e("exportando","Error exportando hoteles.",e);
        }
    }

    private void addAgenciasToExport(FileWriter fileWriter, List<TTOO> agencias){
        try{
            String firstRow = TTOOBDHandler.TABLE_NAME+"\n";
            fileWriter.append(firstRow);
            for(TTOO ttoo:agencias){
                fileWriter.append(String.valueOf(ttoo.getId()));//0
                fileWriter.append("|");
                fileWriter.append(ttoo.getNombre());//1
                fileWriter.append("\n");
            }
        }catch (Exception e){
            Log.e("exportando","Error exportando agencias.",e);
        }
    }

    private void addExcursionesToExport(FileWriter fileWriter, List<Excursion> excursiones){
        try{
            String firstRow = ExcursionBDHandler.TABLE_NAME+"\n";
            fileWriter.append(firstRow);
            for(Excursion excursion:excursiones){
                fileWriter.append(String.valueOf(excursion.getId()));//0
                fileWriter.append("|");
                fileWriter.append(excursion.getNombre());//1
                fileWriter.append("|");
                fileWriter.append(String.valueOf(excursion.getTipoPrecio()));//2
                fileWriter.append("|");
                fileWriter.append(String.valueOf(excursion.getPrecioAd()));//3
                fileWriter.append("|");
                fileWriter.append(String.valueOf(excursion.getPrecioMenor()));//4
                fileWriter.append("|");
                fileWriter.append(String.valueOf(excursion.getPrecioAcomp()));//5
                fileWriter.append("|");
                fileWriter.append(String.valueOf(excursion.getPrecioRango()));//6
                fileWriter.append("|");
                fileWriter.append(String.valueOf(excursion.getIdiomaNecesario()));//7
                fileWriter.append("|");
                fileWriter.append(String.valueOf(excursion.getRangoHasta()));//8
                fileWriter.append("\n");
            }
        }catch (Exception e){
            Log.e("exportando","Error exportando excursiones.",e);
        }
    }

    private void addResservasToExport(FileWriter fileWriter, List<Reserva> reservas){
        try{
            String firstRow = ReservaBDHandler.TABLE_NAME+"\n";
            fileWriter.append(firstRow);
            for(Reserva reserva:reservas){
                fileWriter.append(reserva.getNoTE());//0
                fileWriter.append("|");
                fileWriter.append(formatStringToExport(reserva.getExcursion()));//1
                fileWriter.append("|");
                fileWriter.append(reserva.getAgencia());//2
                fileWriter.append("|");
                fileWriter.append(reserva.getNoHab());//3
                fileWriter.append("|");
                fileWriter.append(reserva.getCliente());//4
                fileWriter.append("|");
                fileWriter.append(reserva.getHotel());//5
                fileWriter.append("|");
                fileWriter.append(reserva.getFechaConfeccion());//6
                fileWriter.append("|");
                fileWriter.append(reserva.getFechaEjecucion());//7
                fileWriter.append("|");
                fileWriter.append(reserva.getFechaOriginalEjecucion());//8
                fileWriter.append("|");
                fileWriter.append(String.valueOf(reserva.incluirEnRepVenta()));//9
                fileWriter.append("|");
                fileWriter.append(reserva.getFechaReporteVenta());//10
                fileWriter.append("|");
                fileWriter.append(String.valueOf(reserva.getAdultos()));//11
                fileWriter.append("|");
                fileWriter.append(String.valueOf(reserva.getMenores()));//12
                fileWriter.append("|");
                fileWriter.append(String.valueOf(reserva.getInfantes()));//13
                fileWriter.append("|");
                fileWriter.append(String.valueOf(reserva.getAcompanantes()));//14
                fileWriter.append("|");
                fileWriter.append(reserva.getIdioma());//15
                fileWriter.append("|");
                fileWriter.append(String.valueOf(reserva.getPrecio()));//16
                fileWriter.append("|");
                fileWriter.append(String.valueOf(reserva.getEstado()));//17
                fileWriter.append("|");
                fileWriter.append(reserva.getFechaDevolucion());//18
                fileWriter.append("|");
                fileWriter.append(reserva.getFechaCancelacion());//19
                fileWriter.append("|");
                fileWriter.append(String.valueOf(reserva.getImporteDevuelto()));//20
                fileWriter.append("|");
                if(reserva.getHistorial()!=null) {//21
                    fileWriter.append(formatStringToExport(reserva.getHistorial()));
                }else {
                    fileWriter.append("");
                }
                fileWriter.append("|");
                if(reserva.getObservaciones()==null || reserva.getObservaciones().isEmpty()){//22
                    fileWriter.append("null");
                }else {
                    String observaciones = formatStringToExport(reserva.getObservaciones());
                    fileWriter.append(observaciones);
                }
                fileWriter.append("\n");
            }
        }catch (Exception e){
            Log.e("exportando","Error exportando reservas.",e);
        }
    }

    private void showFileChooser(){
        Uri carpetaSalvas = Uri.parse(Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name)+"/Salva BD");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.setDataAndType(carpetaSalvas, "*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    SELEC_FILE_SALVA);
        } catch (android.content.ActivityNotFoundException ex) {
            Log.d("Importando", "Error en startActivityForResult: "+ex.getMessage());
        }
    }

    private String formatStringToExport(String texto){
        if(texto!=null) {
            return texto.replace("\n", "^");
        }
        return "";
    }

    private String formatStringToImport(String texto){
        if(texto!=null) {
            return texto.replace("^", "\n");
        }
        return "";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK && requestCode == SELEC_FILE_SALVA) {
            String[] segmentos = data.getData().getPath().split("/");
            //tiene que estar obligatoriamente en la carpeta Salva BD
            String direccionFile = Environment.getExternalStorageDirectory()+"/"+getString(R.string.app_name)+"/Salva BD"
                    +"/"+segmentos[segmentos.length-1];
            File file = new File(direccionFile);
            Log.d("Importando","Archivo seleccionado correctamente: "+file);
            importCSVInfo(file);
        }
    }

    private void importCSVInfo(File f){
        new Thread(()->{
            TipoDato tipoDato = TipoDato.RESERVAS;
            List<Reserva> reservaList = new ArrayList<>();
            List<Excursion> excursionList = new ArrayList<>();
            List<TTOO> ttooList = new ArrayList<>();
            List<Hotel> hotelList = new ArrayList<>();
            try {
                FileReader file = new FileReader(f);
                BufferedReader buffer = new BufferedReader(file);
                String line;
                while ((line = buffer.readLine()) != null) {
                    if(line.equals(ReservaBDHandler.TABLE_NAME)){tipoDato=TipoDato.RESERVAS;continue;}//Reservas
                    if(line.equals(ExcursionBDHandler.TABLE_NAME)){tipoDato=TipoDato.EXCURSIONES;continue;}//Excursiones
                    if(line.equals(TTOOBDHandler.TABLE_NAME)){tipoDato=TipoDato.AGENCIAS;continue;}//Agencias
                    if(line.equals(HotelBDHandler.TABLE_NAME)){tipoDato=TipoDato.HOTELES;continue;}//Hoteles
                    if(line.equals("Config")){tipoDato=TipoDato.CONFIGURACION;continue;}//Configuracion

                    switch (tipoDato){
                        case RESERVAS:
                            Reserva reserva = getReserva(line);
                            if (reserva != null) {
                                reservaList.add(reserva);
                            }
                            break;
                        case EXCURSIONES:
                            Excursion excursion = getExcursion(line);
                            if (excursion != null) {
                                excursionList.add(excursion);
                            }
                            break;
                        case AGENCIAS:
                            TTOO agencia = getAgencia(line);
                            if (agencia != null) {
                                ttooList.add(agencia);
                            }
                            break;
                        case HOTELES:
                            Hotel hotel = getHotel(line);
                            if (hotel != null) {
                                hotelList.add(hotel);
                            }
                            break;
                        case CONFIGURACION:
                            saveConfig(line);
                            break;
                    }
                }

                if(!reservaList.isEmpty()){
                    storeReservasEnBD(reservaList);
                }
                if(!excursionList.isEmpty()){
                    storeExcursionesEnBD(excursionList);
                }
                if(!ttooList.isEmpty()){
                    storeAgenciasEnBD(ttooList);
                }
                if(!hotelList.isEmpty()){
                    storeHotelesEnBD(hotelList);
                }
                getActivity().runOnUiThread(()->{
                    refreshUI();
                    Toast.makeText(getContext(),"Archivo importado correctamente",Toast.LENGTH_SHORT).show();
                });
                Log.d("Importando","Importado correctamente: "+reservaList.size()+" reservas en la lista.");
            }catch (Exception e){
                Log.e("Importando","Error en importCSVInfo",e);
            }
        }).start();
    }

    private void saveConfig(String line){
        try{
            String[] str = line.split("[|]");
            if(hasValue(str[0])) {//nombre vendedor
                MySharedPreferences.storeNombreVendedor(getContext(),str[0]);
            }
            if(hasValue(str[1])) {//telefono vendedor
                MySharedPreferences.storeTelefonoVendedor(getContext(),str[1]);
            }
            if(hasValue(str[2])) {//agencia
                MySharedPreferences.storeAgenciaVendedor(getContext(),str[2]);
            }
            if(hasValue(str[3])) {//inicio
                MySharedPreferences.storeFragmentInicio(getContext(),Integer.parseInt(str[3]));
            }
            if(hasValue(str[4])) {//predecir precio
                if(str[4].equals("true")) {
                    MySharedPreferences.storePredecirPrecio(getContext(), true);
                }else if(str[4].equals("false")){
                    MySharedPreferences.storePredecirPrecio(getContext(), false);
                }
            }
            if(hasValue(str[5])) {//incluir dev en liquidaciones
                if(str[5].equals("true")) {
                    MySharedPreferences.storeIncluirDevEnLiquidacion(getContext(), true);
                }else if(str[5].equals("false")){
                    MySharedPreferences.storeIncluirDevEnLiquidacion(getContext(), false);
                }
            }
            if(hasValue(str[6])) {//incluir precio en cup
                if(str[6].equals("true")) {
                    MySharedPreferences.storeIncluirPrecioCUP(getContext(), true);
                }else if(str[6].equals("false")){
                    MySharedPreferences.storeIncluirPrecioCUP(getContext(), false);
                }
            }
            if(hasValue(str[7])) {//tasa cup
                MySharedPreferences.storeTasaCUP(getContext(),Float.parseFloat(str[7]));
            }
        }catch (Exception e){
            Log.e("Importando","Error salvando la configuracion",e);
        }
    }

    private void storeHotelesEnBD(List<Hotel> hoteles){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        for(Hotel hotel:hoteles){
            Hotel hotelBD = HotelBDHandler.getHotelfromDB(getContext(),hotel.getNombre());
            if(hotelBD.getNombre()!=null && hotelBD.getNombre().equals(hotel.getNombre())){continue;}
            ContentValues values = HotelBDHandler.getContentValues(hotel);
            db.insert(HotelBDHandler.TABLE_NAME, null, values);
        }
    }

    private void storeAgenciasEnBD(List<TTOO> agencias){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        for(TTOO agencia:agencias){
            TTOO agenciaBD = TTOOBDHandler.getTTOOfromDB(getContext(),agencia.getNombre());
            if(agenciaBD.getNombre()!=null && agenciaBD.getNombre().equals(agencia.getNombre())){continue;}
            ContentValues values = TTOOBDHandler.getContentValues(agencia);
            db.insert(TTOOBDHandler.TABLE_NAME, null, values);
        }
    }

    private void storeExcursionesEnBD(List<Excursion> excursiones){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        for(Excursion excursion:excursiones){
            Excursion excBD = ExcursionBDHandler.getExcursionfromDB(getContext(),excursion.getNombre());
            if(excBD.getNombre()!=null && excBD.getNombre().equals(excursion.getNombre())){continue;}
            ContentValues values = ExcursionBDHandler.getContentValues(excursion);
            db.insert(ExcursionBDHandler.TABLE_NAME, null, values);
        }
    }

    private void storeReservasEnBD(List<Reserva> reservaList){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(), AdminSQLiteOpenHelper.BD_NAME, null, AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        for(Reserva reserva:reservaList){
            Reserva reservaBD = ReservaBDHandler.getReservaFromDB(getContext(),reserva.getNoTE());
            if(reservaBD.getNoTE()!=null && reservaBD.getNoTE().equals(reserva.getNoTE())){continue;}
            ContentValues values = ReservaBDHandler.getContentValues(reserva);
            db.insert(ReservaBDHandler.TABLE_NAME, null, values);
        }
    }

    private boolean hasValue(@NonNull String value){
        return !value.equals("null") && !value.isEmpty();
    }

    private Excursion getExcursion(@NonNull String line){
        String[] str = line.split("[|]");
        Excursion excursion = new Excursion();
        try{
            if(hasValue(str[0])){
                excursion.setId(Long.parseLong(str[0]));
            }
            if(hasValue(str[1])){
                excursion.setNombre(str[1]);
            }
            if(hasValue(str[2])){
                excursion.setTipoPrecio(Integer.parseInt(str[2]));
            }
            if(hasValue(str[3])){
                excursion.setPrecioAd(Float.parseFloat(str[3]));
            }
            if(hasValue(str[4])){
                excursion.setPrecioMenor(Float.parseFloat(str[4]));
            }
            if(hasValue(str[5])){
                excursion.setPrecioAcomp(Float.parseFloat(str[5]));
            }
            if(hasValue(str[6])){
                excursion.setPrecioRango(Float.parseFloat(str[6]));
            }
            if(hasValue(str[7])){
                excursion.setIdiomaNecesario(Integer.parseInt(str[7]));
            }
            if(hasValue(str[8])){
                excursion.setRangoHasta(Integer.parseInt(str[8]));
            }
            return excursion;
        }catch (Exception e){
            Log.e("importando","Error getting excursion.",e);
            return null;
        }
    }

    private TTOO getAgencia(@NonNull String line){
        String[] str = line.split("[|]");
        TTOO agencia = new TTOO();
        try{
            if(hasValue(str[0])){
                agencia.setId(Long.parseLong(str[0]));
            }
            if(hasValue(str[1])){
                agencia.setNombre(str[1]);
            }
            return agencia;
        }catch (Exception e){
            Log.e("importando","Error getting agencia.",e);
            return null;
        }
    }

    private Hotel getHotel(@NonNull String line){
        String[] str = line.split("[|]");
        Hotel hotel = new Hotel();
        try{
            if(hasValue(str[0])){
                hotel.setId(Long.parseLong(str[0]));
            }
            if(hasValue(str[1])){
                hotel.setNombre(str[1]);
            }
            return hotel;
        }catch (Exception e){
            Log.e("importando","Error getting hotel.",e);
            return null;
        }
    }

    private Reserva getReserva(@NonNull String line){
        String[] str = line.split("[|]");
        Reserva reserva = new Reserva();
        try{
            if(hasValue(str[0])){
                reserva.setNoTE(str[0]);
            }
            if(hasValue(str[1])){
                reserva.setExcursion(formatStringToImport(str[1]));
            }
            if(hasValue(str[2])){
                reserva.setAgencia(str[2]);
            }
            if(hasValue(str[3])){
                reserva.setNoHab(str[3]);
            }
            if(hasValue(str[4])){
                reserva.setCliente(str[4]);
            }
            if(hasValue(str[5])){
                reserva.setHotel(str[5]);
            }
            if(hasValue(str[6])){
                reserva.setFechaConfeccion(str[6]);
            }
            if(hasValue(str[7])){
                reserva.setFechaEjecucion(str[7]);
            }
            if(hasValue(str[8])){
                reserva.setFechaOriginalEjecucion(str[8]);
            }
            if(hasValue(str[9])){
                if(str[9].equals("true")){
                    reserva.setIncluirEnRepVenta(true);
                }else if(str[9].equals("false")){
                    reserva.setIncluirEnRepVenta(false);
                }
            }
            if(hasValue(str[10])){
                reserva.setFechaReporteVenta(str[10]);
            }
            if(hasValue(str[11])){
                reserva.setAdultos(Integer.parseInt(str[11]));
            }
            if(hasValue(str[12])){
                reserva.setMenores(Integer.parseInt(str[12]));
            }
            if(hasValue(str[13])){
                reserva.setInfantes(Integer.parseInt(str[13]));
            }
            if(hasValue(str[14])){
                reserva.setAcompanante(Integer.parseInt(str[14]));
            }
            if(hasValue(str[15])){
                reserva.setIdioma(str[15]);
            }
            if(hasValue(str[16])){
                reserva.setPrecio(Double.parseDouble(str[16]));
            }
            if(hasValue(str[17])){
                reserva.setEstado(Integer.parseInt(str[17]));
            }
            if(hasValue(str[18])){
                reserva.setFechaDevolucion(str[18]);
            }
            if(hasValue(str[19])){
                reserva.setFechaCancelacion(str[19]);
            }
            if(hasValue(str[20])){
                reserva.setImporteDevuelto(Double.parseDouble(str[20]));
            }
            if(hasValue(str[21])){
                reserva.setHistorial(formatStringToImport(str[21]));
            }
            if(hasValue(str[22])){
                reserva.setObservaciones(formatStringToImport(str[22]));
            }
            return reserva;
        }catch (Exception e){
            Log.e("importando","Error getting reserva.",e);
            return null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu!=null){menu.clear();}
        inflater.inflate(R.menu.menu_frag_ajustes,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_item_exportar_bd){
            if(Util.isPermissionGranted(getContext())){
                exportarBD();
            }else {
                myCallBack.requestPermisionAccessExternalStorage();
            }
        } else if(item.getItemId() == R.id.menu_item_importar_bd){
            if(Util.isPermissionGranted(getContext())){
                showFileChooser();
            }else {
                myCallBack.requestPermisionAccessExternalStorage();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private enum TipoDato{
        RESERVAS,
        EXCURSIONES,
        AGENCIAS,
        HOTELES,
        CONFIGURACION
    }

    public interface MyCallBack{
        void udUI(String tag);
        void showSnackBar(String mensaje);
        void requestPermisionAccessExternalStorage();
    }

}
