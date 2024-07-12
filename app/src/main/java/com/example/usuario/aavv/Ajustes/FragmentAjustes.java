package com.example.usuario.aavv.Ajustes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.usuario.aavv.Almacenamiento.BDExporter;
import com.example.usuario.aavv.Almacenamiento.BDImporter;
import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Util.MisConstantes;


/**
 * Created by usuario on 9/08/2023.
 */

public class FragmentAjustes extends Fragment implements BDImporter.CallFromImporter {

    public static final String TAG = "FragmentAjustes";
    private LinearLayout layoutEditarNombreVendedor,layoutEditarTelefonoVendedor,layoutEditarAgenciaVendedor, layoutEditarMailAdress,
            layoutDefaultMailAdress;
    private EditText etNombreVendedor, etTelefonoVendedor, etAgenciaVendedor, etTasaCUP, etMailAdress;
    private TextView tvNombreVendedor, tvTelefonoVendedor, tvAgenciaVendedor, tvDefaultMailAdress, tvDirectorioApp;
    private Button btnNombreVendedor, btnTelefonoVendedor,btnAgenciaVendedor, btnAddMailAdress;
    private RadioGroup radioGroup;
    private RadioButton rbHomePage, rbLiquidacion, rbExcDelDia;
    private CheckBox cbIncluirDevEnLiq, cbPredecirPrecio, cbIncluirPrecioCUP;

    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ajustes,container,false);
        setHasOptionsMenu(true);
        bindComponents(view);
        setItUp();
        showInfoGeneral();
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
        layoutEditarMailAdress = view.findViewById(R.id.layout_editar_default_email);
        etMailAdress = view.findViewById(R.id.et_default_mail);
        tvDefaultMailAdress = view.findViewById(R.id.tv_defaul_email);
        btnAddMailAdress = view.findViewById(R.id.btn_add_default_mail);
        tvDirectorioApp = view.findViewById(R.id.tv_defaul_directory);
        layoutDefaultMailAdress = view.findViewById(R.id.layout_default_email);
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

        tvDefaultMailAdress.setOnClickListener(view -> {
            layoutDefaultMailAdress.setVisibility(View.GONE);
            layoutEditarMailAdress.setVisibility(View.VISIBLE);
            etMailAdress.setText(MySharedPreferences.getDefaultMailAdress(getContext()));
        });

        btnAddMailAdress.setOnClickListener(view ->{
            if(!etMailAdress.getText().toString().isEmpty()){
                MySharedPreferences.storeDefaultMailAdress(getContext(),etMailAdress.getText().toString());
                showDefaultMailAdress();
            }
        });
        tvDirectorioApp.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Desea seleccionar una nueva carpeta para guardar los archivos de la aplicacion?");
            builder.setPositiveButton("Si", (dialog, which) -> {
                myCallBack.requestCreateSelectAppDir(false);
            });
            builder.setNegativeButton("Cancelar",((dialog, which) -> {
                dialog.dismiss();
            }));
            builder.create().show();
        });
    }

    private void showInfoGeneral(){
        showInfoVendedor();
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
        showDefaultMailAdress();
        if(!MySharedPreferences.getUriExtSharedDir(getContext()).isEmpty()){
            Uri uri = Uri.parse(MySharedPreferences.getUriExtSharedDir(getContext()));
            /*Log.d("URI","toString: "+uri.toString());
            Log.d("URI","getPath: "+uri.getPath());
            Log.d("URI","getEncodedPath: "+uri.getEncodedPath());
            Log.d("URI","getLastPathSegment: "+uri.getLastPathSegment());
            Log.d("URI","getScheme: "+uri.getScheme());*/
            String [] segments = uri.getLastPathSegment().split("[:]");
            if(segments.length > 1){
                tvDirectorioApp.setText(segments[1]);
            }else {
                tvDirectorioApp.setText(uri.getLastPathSegment());
            }
        }
    }

    @Override
    public void refreshUI(){
        showInfoGeneral();
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

    private void showDefaultMailAdress(){
        if(MySharedPreferences.getDefaultMailAdress(getContext()).isEmpty()){
            layoutEditarMailAdress.setVisibility(View.VISIBLE);
            layoutDefaultMailAdress.setVisibility(View.GONE);
        }else {
            layoutEditarMailAdress.setVisibility(View.GONE);
            tvDefaultMailAdress.setText(MySharedPreferences.getDefaultMailAdress(getContext()));
            layoutDefaultMailAdress.setVisibility(View.VISIBLE);
        }
    }


    private void exportarBD(){
        BDExporter bdExporter = new BDExporter(getContext(),getActivity(),myCallBack);
        bdExporter.exportar();
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
            exportarBD();
        } else if(item.getItemId() == R.id.menu_item_importar_bd){
            myCallBack.showFileChooser();
        }
        return super.onOptionsItemSelected(item);
    }


    public interface MyCallBack{
        void udUI(String tag);
        void showSnackBar(String mensaje);
        void requestCreateSelectAppDir(boolean conAlertDialog);
        void showFileChooser();
    }

}
