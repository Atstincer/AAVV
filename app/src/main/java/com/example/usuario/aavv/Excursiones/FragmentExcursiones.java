package com.example.usuario.aavv.Excursiones;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
 * Created by usuario on 22/07/2023.
 */

public class FragmentExcursiones extends Fragment implements ExcursionRVAdapter.MyCallBack{

    public static final String TAG = "FragmentExcursiones";

    private ExcursionRVAdapter adapter;
    private List<Excursion> excursionesList;
    private long idExcursionSelected;

    private MyCallBack callBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_excursiones,container,false);
        idExcursionSelected = 0;
        bindComponents(v);
        callBack.udUI(FragmentExcursiones.TAG);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        callBack = (MyCallBack)context;
        super.onAttach(context);
    }

    private void bindComponents(View v){
        FloatingActionButton btnAddExcursion = (FloatingActionButton)v.findViewById(R.id.btn_add_excursion);
        RecyclerView rvExcursiones = (RecyclerView)v.findViewById(R.id.rv_excursiones);
        udExcursionesList();
        adapter = new ExcursionRVAdapter(excursionesList,this);
        rvExcursiones.setAdapter(adapter);
        rvExcursiones.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAddExcursion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                idExcursionSelected = 0;
                callBack.setUpNewFragmentExcursion();
            }
        });
    }

    void udExcursionesList(){
        if(excursionesList != null){excursionesList.clear();}
        excursionesList = ExcursionBDHandler.getAllExcursionesfromDB(getContext());
        Collections.sort(excursionesList,Excursion.nameAscending);
    }

    @Override
    public void itemClicked(int position) {
        //Toast.makeText(getContext(),"Item clicked "+position,Toast.LENGTH_SHORT).show();
        callBack.setUpFragmentExcursion(excursionesList.get(position).getId());
    }

    public interface MyCallBack{
        void udUI(String tag);
        void setUpNewFragmentExcursion();
        void setUpFragmentExcursion(long id);
    }
}
