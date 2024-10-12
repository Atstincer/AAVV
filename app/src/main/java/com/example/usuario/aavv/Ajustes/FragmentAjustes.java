package com.example.usuario.aavv.Ajustes;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.aavv.Almacenamiento.BDExporter;
import com.example.usuario.aavv.Almacenamiento.BDImporter;
import com.example.usuario.aavv.Almacenamiento.DialogFragmentExporter;
import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
import com.example.usuario.aavv.R;
import com.example.usuario.aavv.Util.MisConstantes;

import java.util.regex.Pattern;


/**
 * Created by usuario on 9/08/2023.
 */

public class FragmentAjustes extends Fragment implements BDImporter.CallFromImporter, DialogFragmentExporter.MyCallBack {

    public static final String TAG = "FragmentAjustes";
    private LinearLayout layoutEditarNombreVendedor,layoutEditarTelefonoVendedor,layoutEditarAgenciaVendedor,
            layoutDefaultMails; //layoutEditarMailAdress
    private EditText etNombreVendedor, etTelefonoVendedor, etAgenciaVendedor, etTasaCUP, etMailAdress, etDiaCierre;
    private TextView tvNombreVendedor, tvTelefonoVendedor, tvAgenciaVendedor, tvDirectorioApp; //tvDefaultMailAdress
    private Button btnNombreVendedor, btnTelefonoVendedor,btnAgenciaVendedor, btnAddMailAdress;
    private RadioGroup rgPaginaInicio, rgFechaFiltrar;
    private RadioButton rbHomePage, rbLiquidacion, rbExcDelDia, rbFechaExcursion, rbFechaConfecion;
    private CheckBox cbIncluirDevEnLiq, cbPredecirPrecio, cbIncluirPrecioCUP, cbIncluirDevEnRepVenta,
            cbCierreRegular, cbCierreDiaEspecifico;
    private FrameLayout layoutLoading;

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
        rgPaginaInicio = view.findViewById(R.id.rg_fragment_inicio);
        rbHomePage = view.findViewById(R.id.rb_iniciar_home_page);
        rbLiquidacion = view.findViewById(R.id.rb_iniciar_liquidacion);
        rbExcDelDia = view.findViewById(R.id.rb_iniciar_reservas);
        cbIncluirDevEnLiq = view.findViewById(R.id.cb_incluir_dev_liquidacion);
        cbPredecirPrecio = view.findViewById(R.id.cb_predecir_precio);
        cbIncluirPrecioCUP = view.findViewById(R.id.cb_incluir_precio_cup);
        etTasaCUP = view.findViewById(R.id.et_tasa_cup);
        layoutDefaultMails = view.findViewById(R.id.layout_default_email);
        etMailAdress = view.findViewById(R.id.et_default_mail);
        btnAddMailAdress = view.findViewById(R.id.btn_add_default_mail);
        tvDirectorioApp = view.findViewById(R.id.tv_defaul_directory);
        rgFechaFiltrar = view.findViewById(R.id.rg_fecha_filtrar);
        rbFechaConfecion = view.findViewById(R.id.rb_fecha_confeccion);
        rbFechaExcursion = view.findViewById(R.id.rb_fecha_excursion);
        layoutLoading = view.findViewById(R.id.layout_loading);
        cbIncluirDevEnRepVenta = view.findViewById(R.id.cb_incluir_dev_repventa);
        cbCierreRegular = view.findViewById(R.id.cb_cierre_mes_regular);
        cbCierreDiaEspecifico = view.findViewById(R.id.cb_cierre_mes_dia);
        etDiaCierre = view.findViewById(R.id.et_dia_cierre);
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
        rgPaginaInicio.setOnCheckedChangeListener((radioGroup, idItemChecked) -> {
            if(idItemChecked == R.id.rb_iniciar_home_page){
                MySharedPreferences.storeFragmentInicio(getContext(), MisConstantes.INICIAR_HOME_PAGE);
            }else if(idItemChecked == R.id.rb_iniciar_liquidacion){
                MySharedPreferences.storeFragmentInicio(getContext(),MisConstantes.INICIAR_LIQUIDACIONES);
            }else if(idItemChecked == R.id.rb_iniciar_reservas){
                MySharedPreferences.storeFragmentInicio(getContext(),MisConstantes.INICIAR_EXCURSIONES_SALIENDO);
            }
        });

        rgFechaFiltrar.setOnCheckedChangeListener((radioGroup, idItemChecked) -> {
            if(idItemChecked == R.id.rb_fecha_excursion){
                MySharedPreferences.storeTipoFechaFiltrar(getContext(),MisConstantes.Filtrar.FECHA_EXCURSION.ordinal());
            }else if(idItemChecked == R.id.rb_fecha_confeccion){
                MySharedPreferences.storeTipoFechaFiltrar(getContext(),MisConstantes.Filtrar.FECHA_CONFECCION.ordinal());
            }
        });

        cbIncluirDevEnLiq.setOnCheckedChangeListener((compoundButton, b) ->
                MySharedPreferences.storeIncluirDevEnLiquidacion(getContext(),compoundButton.isChecked()));
        cbPredecirPrecio.setOnCheckedChangeListener((compoundButton, b) ->
                MySharedPreferences.storePredecirPrecio(getContext(),compoundButton.isChecked()));
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
                    Log.e("ajustes","error textChangeListener parseando etTasaCUP",e);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etDiaCierre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int diaCierre = 0;
                    if(!etDiaCierre.getText().toString().isEmpty()) {
                        int dia = Integer.parseInt(etDiaCierre.getText().toString());
                        /*if(dia > 0 && dia < 31){
                            diaCierre = dia;
                        }*/
                        diaCierre = dia;
                    }
                    if(diaCierre == 0){
                        MySharedPreferences.storeCierreRegular(getContext(), true);
                    }
                    MySharedPreferences.storeDiaCierre(getContext(), diaCierre);
                    myCallBack.setLastDesde("");
                    myCallBack.setLastHasta("");
                }catch (Exception e){
                    Log.e("ajustes","error textChangeListener parseando etDiaCierre",e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cbIncluirDevEnRepVenta.setOnCheckedChangeListener((compoundButton, b) ->
                MySharedPreferences.storeIncluirDevEnRepVenta(getContext(),compoundButton.isChecked()));
        btnAddMailAdress.setOnClickListener(view ->{
            String mail = etMailAdress.getText().toString();
            if(isValidMail(mail)){
                MySharedPreferences.addNewMail(getContext(),mail);
                addMailView(mail);
                etMailAdress.setText("");
            }else {
                Toast.makeText(getContext(),"Mail no es valido",Toast.LENGTH_SHORT).show();
            }
        });
        cbCierreRegular.setOnClickListener((view) -> {
            boolean cierreRegularStored = MySharedPreferences.isCierreRegular(getContext());
            if(!cierreRegularStored && cbCierreRegular.isChecked()){
                MySharedPreferences.storeCierreRegular(getContext(),true);
                cbCierreDiaEspecifico.setChecked(false);
                etDiaCierre.setText("");
                etDiaCierre.setEnabled(false);
            }else if(cierreRegularStored){
                cbCierreRegular.setChecked(true);
            }
        });
        cbCierreDiaEspecifico.setOnClickListener((view) -> {
            boolean cierreRegularStored = MySharedPreferences.isCierreRegular(getContext());
            if(cierreRegularStored && cbCierreDiaEspecifico.isChecked()){
                MySharedPreferences.storeCierreRegular(getContext(),false);
                cbCierreRegular.setChecked(false);
                etDiaCierre.setEnabled(true);
                etDiaCierre.requestFocus();
            }else if(!cierreRegularStored){
                cbCierreDiaEspecifico.setChecked(true);
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

    private boolean isValidMail(String emailStr) {
//        String otherPatter = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        return Pattern.compile("^(.+)@(\\S+)$")
                .matcher(emailStr)
                .matches();
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
        if(MySharedPreferences.getTipoFechaFiltrar(getContext()) == MisConstantes.Filtrar.FECHA_EXCURSION.ordinal()){
            rbFechaExcursion.setChecked(true);
        }else if(MySharedPreferences.getTipoFechaFiltrar(getContext()) == MisConstantes.Filtrar.FECHA_CONFECCION.ordinal()){
            rbFechaConfecion.setChecked(true);
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
        cbIncluirDevEnRepVenta.setChecked(MySharedPreferences.getIncluirDevEnRepVenta(getContext()));
        showDefaultMails();
        if(!MySharedPreferences.getUriExtSharedDir(getContext()).isEmpty()){
            Uri uri = Uri.parse(MySharedPreferences.getUriExtSharedDir(getContext()));
            String [] segments = uri.getLastPathSegment().split(":");
            if(segments.length > 1){
                tvDirectorioApp.setText(segments[1]);
            }else {
                tvDirectorioApp.setText(uri.getLastPathSegment());
            }
        }
        if(MySharedPreferences.isCierreRegular(getContext()) ||
                MySharedPreferences.getDiaCierre(getContext()) == 0){
            cbCierreRegular.setChecked(true);
            cbCierreDiaEspecifico.setChecked(false);
            etDiaCierre.setText("");
            etDiaCierre.setEnabled(false);
        } else {
            cbCierreRegular.setChecked(false);
            cbCierreDiaEspecifico.setChecked(true);
            etDiaCierre.setText(
                    String.valueOf(MySharedPreferences.getDiaCierre(getContext()))
            );
            etDiaCierre.setEnabled(true);
        }
    }

    @Override
    public void refreshUI(){
        showInfoGeneral();
    }

    @Override
    public void starLoading() {
        layoutLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void endLoading() {
        layoutLoading.setVisibility(View.GONE);
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

    private void showDefaultMails(){
        layoutDefaultMails.removeAllViews();
        if(!MySharedPreferences.getMails(getContext()).isEmpty()){
            for(String mail:MySharedPreferences.getArrayOfMails(getContext())){
                addMailView(mail);
            }
        }
    }

    private void addMailView(String mail){
        TextView textView = new TextView(getContext());
        textView.setTextColor(getResources().getColor(R.color.colorAccent));
        textView.setText(mail);
        layoutDefaultMails.addView(textView);
        textView.setOnClickListener(view -> {
            confirmarEliminar((TextView)view);
        });
    }

    private void confirmarEliminar(TextView textView){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Seguro que desea eliminar '"+textView.getText().toString()+"'");
        builder.setPositiveButton("Eliminar", (dialog, which) -> eliminarMail(textView));
        builder.setNegativeButton("Cancelar",((dialog, which) -> dialog.dismiss()));
        builder.create().show();
    }

    private void eliminarMail(TextView textView){
        if(layoutDefaultMails.getChildCount()==0){return;}
        String mail = textView.getText().toString();
        for (int i = 0; i < layoutDefaultMails.getChildCount(); i++) {
            TextView tv = (TextView) layoutDefaultMails.getChildAt(i);
            if(tv.getText().toString().equals(mail)){
                layoutDefaultMails.removeView(layoutDefaultMails.getChildAt(i));
                MySharedPreferences.removeMail(getContext(),mail);
            }
        }
    }

    private void showDialogExporter(){
        DialogFragmentExporter dialog = new DialogFragmentExporter();
        dialog.setTargetFragment(this,0);
        dialog.show(getChildFragmentManager(),DialogFragmentExporter.TAG);
    }

    @Override
    public void exportarBD(Boolean[] whatToExport){
        BDExporter bdExporter = new BDExporter(getContext(),getActivity(),myCallBack);
        bdExporter.exportar(whatToExport);
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
            //exportarBD();
            showDialogExporter();
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
        void setLastDesde(String lastDesde);
        void setLastHasta(String lastHasta);
    }
}
