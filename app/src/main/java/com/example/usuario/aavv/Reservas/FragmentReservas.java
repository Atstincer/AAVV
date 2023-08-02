package com.example.usuario.aavv.Reservas;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.usuario.aavv.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by usuario on 30/07/2023.
 */

public class FragmentReservas extends Fragment implements ReservaRVAdapter.MyCallBack {

    public static final String TAG = "FragmentReservas";

    //private RecyclerView rvReservas;

    private List<Reserva> reservaList;
    private ReservaRVAdapter adapter;

    private MyCallBack myCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reservas,container,false);
        bindComponents(v);
        myCallBack.udUI(FragmentReservas.TAG);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        myCallBack = (MyCallBack)context;
        super.onAttach(context);
    }

    private void bindComponents(View v){
        RecyclerView rvReservas = (RecyclerView)v.findViewById(R.id.rv_reservas);

        reservaList = ReservaBDHandler.getAllReservasFromDB(getContext());
        Collections.sort(reservaList,Reserva.ordenarPorTE);
        adapter = new ReservaRVAdapter(reservaList,this);
        rvReservas.setAdapter(adapter);
        rvReservas.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void itemClicked(int position) {
        //Toast.makeText(getContext(),"Item clicked: "+position,Toast.LENGTH_SHORT).show();
        myCallBack.setUpFragmentReservar(reservaList.get(position).getId());
    }

    public interface MyCallBack{
        void udUI(String tag);
        void setUpFragmentReservar(long id);
    }
}
