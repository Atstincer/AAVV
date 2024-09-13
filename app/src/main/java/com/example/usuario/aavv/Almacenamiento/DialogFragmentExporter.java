package com.example.usuario.aavv.Almacenamiento;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.usuario.aavv.R;


public class DialogFragmentExporter extends DialogFragment {

    public static String TAG = "DialogFragmentExporter";

    private CheckBox cbConfiguracion, cbVenta, cbHoteles, cbAgencias, cbExcursiones;
    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_exporter,container,false);
        bindComponents(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        myCallBack = (MyCallBack) getTargetFragment();
        super.onAttach(context);
    }

    private void bindComponents(View view){
        cbConfiguracion = view.findViewById(R.id.cb_configuracion);
        cbVenta = view.findViewById(R.id.cb_venta);
        cbHoteles = view.findViewById(R.id.cb_hoteles);
        cbAgencias = view.findViewById(R.id.cb_agencias);
        cbExcursiones = view.findViewById(R.id.cb_excursiones);
        Button btnCancelar = view.findViewById(R.id.btn_dfexporter_cancelar);
        Button btnExportar = view.findViewById(R.id.btn_dfexporter_exportar);

        btnCancelar.setOnClickListener(v -> dismiss());
        btnExportar.setOnClickListener(v -> { exportar(); dismiss(); });
    }

    private void exportar(){
        myCallBack.exportarBD(new Boolean[]{
                cbConfiguracion.isChecked(),
                cbVenta.isChecked(),
                cbHoteles.isChecked(),
                cbAgencias.isChecked(),
                cbExcursiones.isChecked()
        });
    }

    public interface MyCallBack{
        void exportarBD(Boolean[] wahtToExport);
    }
}
