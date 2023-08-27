package com.example.usuario.aavv.Ajustes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.usuario.aavv.Almacenamiento.MySharedPreferences;
import com.example.usuario.aavv.R;

/**
 * Created by usuario on 9/08/2023.
 */

public class FragmentAjustes extends Fragment {

    public static final String TAG = "FragmentAjustes";

    private LinearLayout layoutEditarNombreVendedor,layoutEditarTelefonoVendedor,layoutEditarAgenciaVendedor;
    private EditText etNombreVendedor, etTelefonoVendedor, etAgenciaVendedor;
    private TextView tvNombreVendedor, tvTelefonoVendedor, tvAgenciaVendedor;
    private Button btnNombreVendedor, btnTelefonoVendedor,btnAgenciaVendedor;

    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ajustes,container,false);
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

    public interface MyCallBack{
        void udUI(String tag);
    }

}
