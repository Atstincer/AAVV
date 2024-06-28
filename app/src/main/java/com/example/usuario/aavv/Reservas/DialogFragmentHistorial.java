package com.example.usuario.aavv.Reservas;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.usuario.aavv.R;

/**
 * Created by user on 3/23/2024.
 */

public class DialogFragmentHistorial extends DialogFragment {

    public static String TAG = "DialogFragmentHistorial";

    TextView tvInfo;
    Button btnOk;

    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_historial,container,false);
        bindcomponents(view);
        setItUp();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myCallBack = (MyCallBack)getTargetFragment();
    }

    private void bindcomponents(View view){
        tvInfo = view.findViewById(R.id.tv_info_historial);
        btnOk = view.findViewById(R.id.btn_ok_historial);
        btnOk.setOnClickListener(view1 -> dismiss());
    }

    private void setItUp(){
        Reserva reserva = ReservaBDHandler.getReservaFromDB(getContext(),myCallBack.getIdReserva());
        String info = "";
        if(reserva.getHistorial()==null || reserva.getHistorial().isEmpty()){
            info += "Historial\n\nNo hay información para mostrar";
        }else {
            info += "TE"+reserva.getNoTE()+"\n\n"+reserva.getHistorial();
        }
        tvInfo.setText(info);
    }

    interface MyCallBack{
        String getIdReserva();
    }
}
