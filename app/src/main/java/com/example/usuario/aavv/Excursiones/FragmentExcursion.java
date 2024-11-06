package com.example.usuario.aavv.Excursiones;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
import com.example.usuario.aavv.R;

import java.util.Objects;

/**
 * Created by usuario on 1/10/2023.
 */

public class FragmentExcursion extends Fragment {

    public static final String TAG = "FragmentExcursion";

    private EditText etExcursion, etPrecioAdulto, etPrecioMenor, etPrecioAcompanante, etPrecioRango, etPaxHasta;
    private TextView tvPaxAdicional, tvEliminar;
    private RadioGroup radioGroup;
    private RadioButton rbPrecioPax,rbPrecioRango;
    private LinearLayout layoutPrecioRango;
    private CheckBox checkBoxIdiomaNecesario;
    private Button btn;

    private long selectedExcursionID;

    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_excursion,container,false);
        bindComponents(view);
        if(getArguments()!=null){
            selectedExcursionID = getArguments().getLong("id");
        }else {
            selectedExcursionID = 0;
        }
        setItUp();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCallBack = (MyCallBack)context;
    }

    private void bindComponents(View view){
        tvEliminar = view.findViewById(R.id.tv_eliminar);
        etExcursion = view.findViewById(R.id.et_excursion);
        etPrecioAdulto = view.findViewById(R.id.et_precio_adulto);
        etPrecioMenor = view.findViewById(R.id.et_precio_menor);
        etPrecioAcompanante = view.findViewById(R.id.et_precio_acompanante);
        etPrecioRango = view.findViewById(R.id.et_precio_rango);
        etPaxHasta = view.findViewById(R.id.et_pax_hasta);
        tvPaxAdicional = view.findViewById(R.id.tv_pax_adicional);
        radioGroup = view.findViewById(R.id.rg_tipo_precio);
        rbPrecioPax = view.findViewById(R.id.rb_precio_pax);
        rbPrecioRango = view.findViewById(R.id.rb_precio_rango);
        layoutPrecioRango = view.findViewById(R.id.layout_precio_rango);
        checkBoxIdiomaNecesario = view.findViewById(R.id.checkbox_idioma_necesario);
        btn = view.findViewById(R.id.btn_fragment_excursion);
    }

    private void setItUp(){
        myCallBack.udUI(TAG);
        showHideLayout();
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> showHideLayout());
        if(selectedExcursionID==0){
            tvEliminar.setVisibility(View.GONE);
            btn.setText("Registrar");
        }else {
            tvEliminar.setVisibility(View.VISIBLE);
            showInfoSelectedExcursion();
            btn.setText("Actualizar");
        }
        tvEliminar.setOnClickListener(view -> showDialogConfirmEliminar());
        btn.setOnClickListener(view -> {
            if(selectedExcursionID==0) {
                registrar();
            }else {
                actualizar();
            }
        });
    }

    private void registrar(){
        if(!isValid()){return;}
        Excursion excursion = getInfoFragment();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues values = ExcursionBDHandler.getContentValues(excursion);
        db.insert(ExcursionBDHandler.TABLE_NAME,null,values);
        Toast.makeText(getContext(),"Registrado correctamente",Toast.LENGTH_SHORT).show();
        clearUI();
        etExcursion.requestFocus();
    }

    private void actualizar(){
        if(!isValid()){return;}
        Excursion excursion = getInfoFragment();
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(getContext(),AdminSQLiteOpenHelper.BD_NAME,null,AdminSQLiteOpenHelper.BD_VERSION);
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues values = ExcursionBDHandler.getContentValues(excursion);
        db.update(ExcursionBDHandler.TABLE_NAME,values,"id=?",new String[]{String.valueOf(selectedExcursionID)});
        Toast.makeText(getContext(),"Actualizado correctamente",Toast.LENGTH_SHORT).show();
    }

    private void showDialogConfirmEliminar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Seguro que desea eliminar esta excursión?");
        builder.setPositiveButton("Eliminar",((dialog, which) -> eliminarExc()));
        builder.setNegativeButton("Cancelar",((dialog, which) -> {}));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void eliminarExc(){
        AdminSQLiteOpenHelper admin = AdminSQLiteOpenHelper.getInstance(
                getContext(),
                AdminSQLiteOpenHelper.BD_NAME,
                null,
                AdminSQLiteOpenHelper.BD_VERSION
        );
        SQLiteDatabase db = admin.getWritableDatabase();
        db.execSQL("DELETE FROM " + ExcursionBDHandler.TABLE_NAME + " WHERE id=?",
                new String[]{String.valueOf(selectedExcursionID)});
        Toast.makeText(getContext(),"Eliminado correctamente",Toast.LENGTH_SHORT).show();
        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    private void showInfoSelectedExcursion(){
        Excursion excursion = ExcursionBDHandler.getExcursionfromDB(getContext(),selectedExcursionID);
        etExcursion.setText(excursion.getNombre());
        switch (excursion.getTipoPrecio()){
            case Excursion.PRECIO_X_PAX:
                rbPrecioPax.setChecked(true);
                break;
            case Excursion.PRECIO_X_RANGO:
                rbPrecioRango.setChecked(true);
                break;
        }
        showHideLayout();
        if(excursion.getRangoHasta()!=0) {
            etPaxHasta.setText(String.valueOf(excursion.getRangoHasta()));
        }
        if(excursion.getPrecioRango()!=0){
            etPrecioRango.setText(String.valueOf(excursion.getPrecioRango()));
        }
        if(excursion.getPrecioAd()!=0){
            etPrecioAdulto.setText(String.valueOf(excursion.getPrecioAd()));
        }
        if(excursion.getPrecioMenor()!=0){
            etPrecioMenor.setText(String.valueOf(excursion.getPrecioMenor()));
        }
        if(excursion.getPrecioAcomp()!=0){
            etPrecioAcompanante.setText(String.valueOf(excursion.getPrecioAcomp()));
        }
        switch (excursion.getIdiomaNecesario()){
            case Excursion.IDIOMA_NECESARIO:
                checkBoxIdiomaNecesario.setChecked(true);
                break;
            case Excursion.IDIOMA_NO_NECESARIO:
                checkBoxIdiomaNecesario.setChecked(false);
                break;
        }
    }

    private Excursion getInfoFragment(){
        Excursion excursion = new Excursion();
        excursion.setNombre(etExcursion.getText().toString());
        if(radioGroup.getCheckedRadioButtonId() == R.id.rb_precio_pax){
            excursion.setTipoPrecio(Excursion.PRECIO_X_PAX);
        }else if(radioGroup.getCheckedRadioButtonId() == R.id.rb_precio_rango){
            excursion.setTipoPrecio(Excursion.PRECIO_X_RANGO);
        }
        if(etPaxHasta.getText().toString().isEmpty()){
            excursion.setRangoHasta(0);
        }else {
            excursion.setRangoHasta(Integer.parseInt(etPaxHasta.getText().toString()));
        }
        if(etPrecioRango.getText().toString().isEmpty()){
            excursion.setPrecioRango(0);
        }else {
            excursion.setPrecioRango(Float.parseFloat(etPrecioRango.getText().toString()));
        }
        if(etPrecioAdulto.getText().toString().isEmpty()){
            excursion.setPrecioAd(0);
        }else {
            excursion.setPrecioAd(Float.parseFloat(etPrecioAdulto.getText().toString()));
        }
        if(etPrecioMenor.getText().toString().isEmpty()){
            excursion.setPrecioMenor(0);
        }else {
            excursion.setPrecioMenor(Float.parseFloat(etPrecioMenor.getText().toString()));
        }
        if(etPrecioAcompanante.getText().toString().isEmpty()){
            excursion.setPrecioAcomp(0);
        }else {
            excursion.setPrecioAcomp(Float.parseFloat(etPrecioAcompanante.getText().toString()));
        }
        if(checkBoxIdiomaNecesario.isChecked()){
            excursion.setIdiomaNecesario(Excursion.IDIOMA_NECESARIO);
        }else {
            excursion.setIdiomaNecesario(Excursion.IDIOMA_NO_NECESARIO);
        }
        return excursion;
    }

    private boolean isValid(){
        if(etExcursion.getText().toString().isEmpty()){
            Toast.makeText(getContext(),"Debe escribir un nombre",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showHideLayout(){
        if(radioGroup.getCheckedRadioButtonId() == R.id.rb_precio_pax){
            etPaxHasta.setText("");
            etPrecioRango.setText("");
            layoutPrecioRango.setVisibility(View.GONE);
            tvPaxAdicional.setVisibility(View.GONE);
        }else if(radioGroup.getCheckedRadioButtonId() == R.id.rb_precio_rango){
            layoutPrecioRango.setVisibility(View.VISIBLE);
            tvPaxAdicional.setVisibility(View.VISIBLE);
        }
    }

    private void clearUI(){
        etExcursion.setText("");
        etPaxHasta.setText("");
        etPrecioRango.setText("");
        etPrecioAdulto.setText("");
        etPrecioMenor.setText("");
        etPrecioAcompanante.setText("");
        checkBoxIdiomaNecesario.setChecked(false);
    }

    public interface MyCallBack{
        void udUI(String tag);
    }

}
